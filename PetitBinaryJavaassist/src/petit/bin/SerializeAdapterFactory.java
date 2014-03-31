package petit.bin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.test.MockReadableStore;
import petit.bin.test.Test1;
import petit.bin.test.Test1.Inner3;
import petit.bin.util.DefaultClassPool;
import petit.bin.util.JavaassistUtil;
import petit.bin.util.KnownCtClass;
import petit.bin.util.Pair;

/**
 * シリアライズクラスに対する実際の {@link Skeleton_SerializeAdapter} を得るためのファクトリ
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public final class SerializeAdapterFactory {
	
	public static final String CONCRETE_SERIALIZER_CLASS_NAME = "$__SerializerAdapter__";
	
	/**
	 * シリアライズクラスに対する実際の {@link SerializeAdapter} のマップ
	 */
	private static final Map<Class<?>, SerializeAdapter<?>> _clazz_serializers = new HashMap<Class<?>, SerializeAdapter<?>>();
	
	@SuppressWarnings("unchecked")
	public static final <T> SerializeAdapter<T> getSerializer(final Class<T> clazz) throws CannotCompileException, NotFoundException {
		SerializeAdapter<?> maybe_null = _clazz_serializers.get(clazz);
		if (maybe_null == null) {
			maybe_null = createSerializeAdapter(clazz);
			_clazz_serializers.put(clazz, maybe_null);
		}
		return (SerializeAdapter<T>) maybe_null;
	}
	
	private static final void checkStructClassModifier(final Class<?> clazz) throws CannotCompileException {
		final int mod = clazz.getModifiers();
		if ((mod & java.lang.reflect.Modifier.FINAL) != 0)
			throw new CannotCompileException(clazz.getCanonicalName() +  ": final class is not supported");
		if ((mod & java.lang.reflect.Modifier.INTERFACE) != 0)
			throw new CannotCompileException(clazz.getCanonicalName() +  ": interface type is not supported");
		if (clazz.isMemberClass() && (mod & java.lang.reflect.Modifier.STATIC) == 0)
			throw new CannotCompileException(clazz.getCanonicalName() +  ": non-static member class is not supported");
	}
	
	@SuppressWarnings("unchecked")
	private static final SerializeAdapter<?> createSerializeAdapter(final Class<?> clazz) throws CannotCompileException, NotFoundException {
		if (KnownCtClass.ISERIALIZE_ADAPTER.CT_CLAZZ == null)
			throw new NotFoundException("Cannot locate SerializeAdapter class");
		checkStructClassModifier(clazz);
		
		try {
			final CtClass target_clazz = DefaultClassPool.CP.get(clazz.getName());
			final CtClass adapter_clazz = target_clazz.makeNestedClass(CONCRETE_SERIALIZER_CLASS_NAME, true);
			
			// add interface SerializeAdapter to adapter_clazz
			adapter_clazz.addInterface(KnownCtClass.ISERIALIZE_ADAPTER.CT_CLAZZ);
			
			// add fields to adapter_clazz
			adapter_clazz.addField(JavaassistUtil.createPrivateFinalField(KnownCtClass.ACLASS.CT_CLAZZ, CodeFragments.ACCESS_CLASS.ID, adapter_clazz));
			adapter_clazz.addField(JavaassistUtil.createPrivateFinalField(KnownCtClass.INSTANTIATOR.CT_CLAZZ, CodeFragments.ACCESS_INSTANTIATOR.ID, adapter_clazz));
			adapter_clazz.addField(JavaassistUtil.createPrivateFinalField(KnownCtClass.STRUCT.CT_CLAZZ, CodeFragments.ACCESS_STRUCTANNO.ID, adapter_clazz));
			
			// create constructor <init>(Class) of adapter_clazz
			final CtConstructor adapter_ctor = new CtConstructor(new CtClass[] {KnownCtClass.ACLASS.CT_CLAZZ}, adapter_clazz);
			adapter_ctor.setModifiers(Modifier.PUBLIC);
			adapter_ctor.setBody(JavaassistUtil.join(
					"{",
						CodeFragments.ACCESS_CLASS.ID, " = $1;",
						CodeFragments.ACCESS_INSTANTIATOR.ID, " = ", CodeFragments.REFLECTIONUTIL.invoke("getInstantiator", "_clazz"), ";",
						CodeFragments.ACCESS_STRUCTANNO.ID, " = ", CodeFragments.ACCESS_CLASS.invoke("getAnnotation", KnownCtClass.STRUCT.CANONICALNAME + ".class"), ";",
						"if (", CodeFragments.ACCESS_STRUCTANNO.ID, " == null) throw new IllegalArgumentException(\"",
							KnownCtClass.STRUCT.CANONICALNAME," annotation is not present\");",
					"}"
					));
			adapter_clazz.addConstructor(adapter_ctor);
			
			// add general method(s)
			adapter_clazz.addMethod(CtMethod.make("public final Class getTargetClass() { return _clazz; }", adapter_clazz));
			
			// add serialization methods
			final List<Pair<Class<?>, CtField>> managed_fields = JavaassistUtil.getManagedFields(DefaultClassPool.CP, clazz);
			
			adapter_clazz.addMethod(CtMethod.make(JavaassistUtil.join(
					"public final Object read(Object ao, ", KnownCtClass.READABLE_STORE.CANONICALNAME, " ", CodeFragments.READER.ID, ") throws Exception {",
						CodeFragments.READER.invoke("pushByteOrder", CodeFragments.ACCESS_STRUCTANNO.invoke("byteOrder")), ";",
						CodeFragments.READER.invoke("pushType", CodeFragments.ACCESS_CLASS.ID), ";",
						
						makeReadMethodBody(clazz, adapter_clazz, managed_fields),
						
						CodeFragments.READER.invoke("popByteOrder"), ";",
						CodeFragments.READER.invoke("popType"), ";",
						"return ao;",
					"}"
					), adapter_clazz));
			
			adapter_clazz.addMethod(CtMethod.make(JavaassistUtil.join(
					"public final Object read(", KnownCtClass.READABLE_STORE.CANONICALNAME, " ", CodeFragments.READER.ID, ") throws Exception {",
						"return read(", CodeFragments.ACCESS_INSTANTIATOR.invoke("newInstance"), ", ", CodeFragments.READER.ID, ");",
					"}"
					), adapter_clazz));
			
			return (SerializeAdapter<?>) adapter_clazz.toClass().getConstructor(Class.class).newInstance(clazz);
		} catch (CannotCompileException e) {
			throw e;
		} catch (Exception e) {
			throw new CannotCompileException(e);
		}
		
	}
	
	private static final String makeReadMethodBody(final Class<?> target_clazz, final CtClass adapter_clazz, final List<Pair<Class<?>, CtField>> managed_fields) throws CannotCompileException {
		final StringBuilder sb = new StringBuilder();
		
		/*
		 * <target_class> <ACCESS_INSTANCE.ID> = (<target_class>) ao;
		 * <read elements>
		 */
		sb.append(target_clazz.getCanonicalName()).append(" ").append(CodeFragments.ACCESS_INSTANCE.ID).append(" = (").append(target_clazz.getCanonicalName()).append(") ao;");
		sb.append("System.out.println(ao.getClass());\n");
		for (final Pair<Class<?>, CtField> field : managed_fields) {
			final MemberAnnotationMetaAgent ma = MetaAgentFactory.getMetaAgent(field.SECOND);
			if (ma == null)
				throw new UnsupportedOperationException("Cannot find meta-agent for " + field.SECOND + " (MAY BE BUG)");
			sb.append(ma.makeReaderSource(field.SECOND)).append("\n");
		}
		System.err.println(sb.toString());
		return sb.toString();
	}
	
	public static void main(String[] args) throws Exception {
		final SerializeAdapter<Test1.Inner3> adapter = getSerializer(Test1.Inner3.class);
//		System.out.println(adapter.getTargetClass());
		final Inner3 ao = new Test1.Inner3();
		ao.iv2 = 1.234;
		adapter.read(ao, new MockReadableStore());
	}
	
}
