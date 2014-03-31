package petit.bin.anno;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import petit.bin.util.Instantiator;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.ReflectionUtil.VisibilityConstraint;

/**
 * TODO document
 * <pre>
 * default: use {@link Instantiator}
 * Class [method name]()
 * Class [method name](Object inst, Field field)
 * Object [method name]()
 * Object [method name](Object inst, Field field)
 * </pre>
 * 
 * @author 俺用
 * @since 2014/03/23 PetitBinary
 *
 */
public abstract class FieldObjectInstantiator {
	
	private static final Map<Class<?>, DefaultInstantiator> _clazz_nullary_instantiator_map = new HashMap<Class<?>, DefaultInstantiator>();
	
	private static final DefaultInstantiator getDefaultInstantiator(final Class<?> clazz) {
		if (clazz == null)
			throw new NullPointerException("Argument clazz must not be null");
		
		DefaultInstantiator maybe_instor = _clazz_nullary_instantiator_map.get(clazz);
		if (maybe_instor == null) {
			maybe_instor = new DefaultInstantiator(clazz);
			_clazz_nullary_instantiator_map.put(clazz, maybe_instor);
		}
		return maybe_instor;
	}
	
	private static final boolean isFullArgumentsMethod(final Method m) {
		final Class<?>[] params = m.getParameterTypes();
		if (params.length == 2) {
			return params[0].equals(Object.class) && params[1].equals(Field.class);
		}
		return false;
	}
	
	/**
	 * @param field_origin フィールドのオリジナルの型，またはフィールドが配列型の場合はそのコンポーネント型でも可
	 * @param resolver_enclosing_class resolver_function が属するクラス
	 * @param resolver_function フィールドの実際の型を解決するメソッド
	 * @return 実際のクラスを解決し，そのオブジェクトを生成することが出来る {@link FieldObjectInstantiator}
	 * @throws NoSuchMethodException 
	 */
	public static final FieldObjectInstantiator getResolver(final Class<?> field_origin, final Class<?> resolver_enclosing_class, final String resolver_function) throws NoSuchMethodException {
		if (resolver_function == null) {
			return getDefaultInstantiator(field_origin);
		} else {
			for (final Method method : ReflectionUtil.getVisibleMethods(resolver_enclosing_class, VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, resolver_function, null, (Class<?>[]) null)) {
				if (isFullArgumentsMethod(method)) {
					if (method.getReturnType().equals(Class.class)) {
						return new InstantiatorWithConcreteClassResolver_FullArguments(method);
					} else {
						return new WrappedInstantiator_FullArguments(method);
					}
				} else if (method.getParameterTypes().length == 0) {
					if (method.getReturnType().equals(Class.class)) {
						return new InstantiatorWithConcreteClassResolver(method);
					} else {
						return new WrappedInstanceResolver(method);
					}
				}
			}
			
			throw new NoSuchMethodException("No applicable method " + resolver_function + " is found");
		}
	}
	
	/**
	 * enc_inst のフィールド field の実際のクラスのオブジェクトを得る
	 * 
	 * @param invoker_enc_inst 呼び出し元のクラスのインスタンス
	 * @param field_enc_inst field を持つクラスのインスタンス
	 * @param field フィールド
	 * @return フィールドに設定される実際のオブジェクト
	 * @throws Exception
	 */
	public abstract Object getConcreteClassInstance(final Object invoker_enc_inst, final Object field_enc_inst, final Field field) throws Exception;
	
	/**
	 * フィールドのオブジェクトのインスタンス化方法が指定されていない場合のデフォルトのインスタンシエータ
	 * 
	 * @author 俺用
	 * @since 2014/03/23 PetitBinary
	 *
	 */
	public static final class DefaultInstantiator extends FieldObjectInstantiator {
		
		private final Class<?> _field_clazz;
		
		private final Instantiator _ctor;
		
		private DefaultInstantiator(final Class<?> field_clazz) {
			_field_clazz = field_clazz;
			_ctor = ReflectionUtil.getInstantiator(field_clazz);
		}
		
		@Override
		public Object getConcreteClassInstance(Object invoker_enc_inst, Object field_enc_inst, Field field) throws Exception {
			if (_ctor == null)
				throw new InstantiationException("Cannot create instance of " + _field_clazz);
			
			try {
				return _ctor.newInstance();
			} catch (Exception e) {
				throw new InstantiationException(e.getLocalizedMessage());
			}
		}
		
	}
	
	/**
	 * Class [method name]()
	 * 
	 * @author 俺用
	 * @since 2014/03/23 PetitBinary
	 *
	 */
	public static final class InstantiatorWithConcreteClassResolver extends FieldObjectInstantiator {
		
		private final Method _class_resolver;
		
		private InstantiatorWithConcreteClassResolver(final Method class_resolver) {
			_class_resolver = class_resolver;
			_class_resolver.setAccessible(true);
		}
		
		@Override
		public Object getConcreteClassInstance(Object invoker_enc_inst, Object field_enc_inst, Field field) throws Exception {
			final Class<?> concrete_clazz = (Class<?>) _class_resolver.invoke(invoker_enc_inst);
			return getDefaultInstantiator(concrete_clazz).getConcreteClassInstance(invoker_enc_inst, field_enc_inst, field);
		}
		
	}
	
	/**
	 * Class [method name](Object inst, Field field)
	 * 
	 * @author 俺用
	 * @since 2014/03/23 PetitBinary
	 *
	 */
	public static final class InstantiatorWithConcreteClassResolver_FullArguments extends FieldObjectInstantiator {
		
		private final Method _class_resolver;
		
		private InstantiatorWithConcreteClassResolver_FullArguments(final Method class_resolver) {
			_class_resolver = class_resolver;
			_class_resolver.setAccessible(true);
		}
		
		@Override
		public Object getConcreteClassInstance(Object invoker_enc_inst, Object field_enc_inst, Field field) throws Exception {
			final Class<?> concrete_clazz = (Class<?>) _class_resolver.invoke(invoker_enc_inst, field_enc_inst, field);
			return getDefaultInstantiator(concrete_clazz).getConcreteClassInstance(invoker_enc_inst, field_enc_inst, field);
		}
		
	}
	
	/**
	 * Object [method name]()
	 * 
	 * @author 俺用
	 * @since 2014/03/23 PetitBinary
	 *
	 */
	public static final class WrappedInstanceResolver extends FieldObjectInstantiator {
		
		private final Method _object_resolver;
		
		public WrappedInstanceResolver(final Method object_resolver) {
			_object_resolver = object_resolver;
			_object_resolver.setAccessible(true);
		}
		
		@Override
		public Object getConcreteClassInstance(Object invoker_enc_inst, Object field_enc_inst, Field field) throws Exception {
			return _object_resolver.invoke(invoker_enc_inst);
		}
		
	}
	
	/**
	 * Object [method name](Object inst, Field field)
	 * @author 俺用
	 * @since 2014/03/23 PetitBinary
	 *
	 */
	public static final class WrappedInstantiator_FullArguments extends FieldObjectInstantiator {
		
		private final Method _object_resolver;
		
		public WrappedInstantiator_FullArguments(final Method object_resolver) {
			_object_resolver = object_resolver;
			_object_resolver.setAccessible(true);
		}
		
		@Override
		public Object getConcreteClassInstance(Object invoker_enc_inst, Object field_enc_inst, Field field) throws Exception {
			return _object_resolver.invoke(invoker_enc_inst, field_enc_inst, field);
		}
		
	}
	
}
