package petit.bin.util;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import petit.bin.anno.StructMember;
import petit.bin.util.instor.Instantiator;
import petit.bin.util.instor.NullaryInstantiator;
import petit.bin.util.instor.UnsafeInstantiator;

public class Util {
	
	/**
	 * この環境のデフォルトのクラスプール
	 */
	public static final ClassPool CP = ClassPool.getDefault();
	
	/**
	 * {@link #getManagedFields(ClassPool, Class)} で private なフィールドをチェックする場合は true
	 */
	public static final boolean CHECK_FIELD_MODIFIER_PRIVATE = true;
	
	/**
	 * {@link CtClass} からプリミティブ型およびプリミティブ型の配列へのマッピング
	 */
	private static final Map<CtClass, Class<?>> _primitive_ctclass_map;
	
	private static final Map<Class<?>, Instantiator> _class_instantiator_map;
	
	static {
		_primitive_ctclass_map = new HashMap<>();
		_primitive_ctclass_map.put(CtClass.booleanType, boolean.class);
		_primitive_ctclass_map.put(CtClass.byteType, byte.class);
		_primitive_ctclass_map.put(CtClass.shortType, short.class);
		_primitive_ctclass_map.put(CtClass.charType, char.class);
		_primitive_ctclass_map.put(CtClass.intType, int.class);
		_primitive_ctclass_map.put(CtClass.longType, long.class);
		_primitive_ctclass_map.put(CtClass.floatType, float.class);
		_primitive_ctclass_map.put(CtClass.doubleType, double.class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(byte[].class.getName()), byte[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(short[].class.getName()), short[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(char[].class.getName()), char[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(int[].class.getName()), int[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(long[].class.getName()), long[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(float[].class.getName()), float[].class);
		_primitive_ctclass_map.put(Util.CP.getOrNull(double[].class.getName()), double[].class);
		
		_class_instantiator_map = new HashMap<>();
	}
	
	/**
	 * {@link CtClass} が表すクラスを静的に解決する<br />
	 * このメソッドは {@link CtClass#toClass()} を呼ばない<br />
	 * 戻り値は，次のとおり
	 * <pre>
	 * 対象のクラスがプリミティブ型またはプリミティブ型の配列の場合: Pair(対象のクラス, null)
	 * 対象のクラスが非配列型の場合: Pair(対象のクラス, false)
	 * 対象のクラスが配列型の場合: Pair(対象のクラス, true)
	 * </pre>
	 * 
	 * @param clazz 対象のクラス
	 * @return 対象のクラスを表すクラス，および対象のクラスの種類を表すペア
	 * @throws ClassNotFoundException 
	 * @throws NotFoundException 
	 */
	public static final Pair<Class<?>, Boolean> toClass(final CtClass clazz) throws ClassNotFoundException, NotFoundException {
		if (clazz.isArray()) {
			final CtClass component_ctype = clazz.getComponentType();
			if (component_ctype.isPrimitive())
				return new Pair<Class<?>, Boolean>(_primitive_ctclass_map.get(clazz), null);
			else
				return new Pair<Class<?>, Boolean>(Class.forName("[L" + clazz.getComponentType().getName() + ";"), true);
		} else if (clazz.isPrimitive()) {
			return new Pair<Class<?>, Boolean>(_primitive_ctclass_map.get(clazz), null);
		} else {
			return new Pair<Class<?>, Boolean>(Class.forName(clazz.getName()), false);
		}
	}
	
	/**
	 * 対象のクラスから可視なクラスのリストを得る
	 * 
	 * @param begin 対象のクラス
	 * @param view_outer_class メンバクラスの場合にその外部クラスも含める場合は true
	 * @return 対象のクラスから可視なクラスのリスト
	 */
	public static final List<Class<?>> findVisibleClasses(final Class<?> begin, final boolean view_outer_class) {
		final List<Class<?>> result = new ArrayList<>();
		for (Class<?> cur = begin; cur != null; cur = cur.getSuperclass()) {
			if (!result.contains(cur))
				result.add(cur);
			
			if (view_outer_class && ((cur.getModifiers() & java.lang.reflect.Modifier.STATIC) == 0) && cur.isMemberClass()) {
				for (Class<?> enc = cur.getDeclaringClass(); enc != null; enc = enc.getDeclaringClass()) {
					if (!result.contains(enc))
						result.add(enc);
				}
			}
		}
		
		return result;
	}
	
	/**
	 * 対象のクラスから {@link StructMember} が与えられた {@link CtField} を得る
	 * 
	 * @param cp クラスプール
	 * @param clazz 対象のクラス
	 * @return 対象のクラスからなるリスト
	 * @throws NotFoundException
	 * @throws ClassNotFoundException
	 */
	public static final List<CtField> getManagedFields(final ClassPool cp, final Class<?> clazz) throws NotFoundException, ClassNotFoundException {
		final List<CtField> result = new ArrayList<>();
		for (final Class<?> c : findVisibleClasses(clazz, false)) {
			final CtClass cur = cp.getOrNull(c.getName());
			for (final CtField field : cur.getDeclaredFields()) {
				final StructMember member_anno = (StructMember) field.getAnnotation(StructMember.class);
				if (member_anno == null)
					continue;
				if (CHECK_FIELD_MODIFIER_PRIVATE && (field.getModifiers() & Modifier.PRIVATE) != 0)
					throw new UnsupportedOperationException(field.getSignature() + " is private member(public, protected or default are applicable)");
				
				result.add(field);
			}
		}
		
		Collections.sort(result, new Comparator<CtField>() {
			@Override
			public int compare(CtField o1, CtField o2) {
				try {
					final StructMember v1 = (StructMember) o1.getAnnotation(StructMember.class);
					final StructMember v2 = (StructMember) o2.getAnnotation(StructMember.class);
					return v1.value() - v2.value();
				} catch (ClassNotFoundException e) {
					return 0;
				}
			}
		});
		
		return result;
	}
	
	/**
	 * 文字列を連結する
	 * 
	 * @param args 文字列
	 * @return 連結された文字列
	 */
	public static final String join(final String ... args) {
		final StringBuilder sb = new StringBuilder();
		for (final String str : args)
			sb.append(str);
		return sb.toString();
	}
	
	/**
	 * clazz のインスタンスを構築可能な方法を得る
	 * 
	 * @param clazz クラス
	 * @return clazz のインスタンスを構築可能な方法，または構築可能な方法がなければ null
	 */
	public static final Instantiator getInstantiator(final Class<?> clazz) {
		if (_class_instantiator_map.containsKey(clazz)) {
			return _class_instantiator_map.get(clazz);
		} else {
			if ((clazz.getModifiers() & java.lang.reflect.Modifier.ABSTRACT) != 0)
				throw new IllegalArgumentException(clazz.getCanonicalName() + " is abstract class");
			
			Instantiator instor = null;
			try {
				instor = new NullaryInstantiator(clazz);
			} catch (Exception e) {
				if (UnsafeInstantiator.isAvailable())
					instor = new UnsafeInstantiator(clazz);
				else
					instor = null;
			}
			_class_instantiator_map.put(clazz, instor);
			return instor;
		}
	}
	
	public static final String getMethodSignature(final Method m) {
		return getMethodSignature(m.getReturnType(), m.getParameterTypes());
	}
	
	public static final String getMethodSignature(final Class<?> return_type, final Class<?> ... params) {
		final StringBuilder sig = new StringBuilder();
		sig.append("(");
		for (final Class<?> param : params) {
			sig.append(getTypeSignature(param));
		}
		sig.append(")").append(getTypeSignature(return_type));
		
		return sig.toString();
	}
	
	private static final String getTypeSignature(final Class<?> type) {
		if (type.isArray())
			return "[" + getTypeSignature(type.getComponentType());
		else if (type.isPrimitive()) {
			if (void.class.equals(type))
				return "V";
			else if (boolean.class.equals(type))
				return "Z";
			else if (byte.class.equals(type))
				return "B";
			else if (char.class.equals(type))
				return "C";
			else if (double.class.equals(type))
				return "D";
			else if (float.class.equals(type))
				return "F";
			else if (int.class.equals(type))
				return "I";
			else if (long.class.equals(type))
				return "J";
			else if (short.class.equals(type))
				return "S";
			else
				throw new UnsupportedOperationException("Unknown Primitive Type " + type);
		} else
			return "L" + type.getCanonicalName().replace('.', '/') + ";";
	}
	
	/**
	 * 対象のオブジェクトが集合のいずれかに含まれるか検証する
	 * 
	 * @param target 対象のオブジェクト
	 * @param others 集合
	 * @return 対象のオブジェクトが集合のいずれかに含まれる場合は true
	 */
	@SafeVarargs
	public static final <T> boolean isOneOf(final T target, final T ... others) {
		for (int i = 0; i < others.length; i++)
			if (target.equals(others[i]))
				return true;
		return false;
	}
	
}
