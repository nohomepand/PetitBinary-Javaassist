package petit.bin.util;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.NotFoundException;
import petit.bin.anno.StructMember;

public class JavaassistUtil {
	
	public static final boolean CHECK_FIELD_MODIFIER_PRIVATE = true;
	
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
	
	public static final List<Pair<Class<?>, CtField>> getManagedFields(final ClassPool cp, final Class<?> clazz) throws NotFoundException, ClassNotFoundException {
		final List<Pair<Class<?>, CtField>> result = new ArrayList<>();
		for (final Class<?> c : findVisibleClasses(clazz, false)) {
			final CtClass cur = cp.getOrNull(c.getName());
			for (final CtField field : cur.getDeclaredFields()) {
				final StructMember member_anno = (StructMember) field.getAnnotation(StructMember.class);
				if (member_anno == null)
					continue;
				if (CHECK_FIELD_MODIFIER_PRIVATE && (field.getModifiers() & Modifier.PRIVATE) != 0)
					throw new UnsupportedOperationException(field.getSignature() + " is private member(public, protected or default are applicable)");
				
				result.add(new Pair<Class<?>, CtField>(c, field));
			}
		}
		
		Collections.sort(result, new Comparator<Pair<Class<?>, CtField>>() {
			@Override
			public int compare(Pair<Class<?>, CtField> o1, Pair<Class<?>, CtField> o2) {
				try {
					final StructMember v1 = (StructMember) o1.SECOND.getAnnotation(StructMember.class);
					final StructMember v2 = (StructMember) o2.SECOND.getAnnotation(StructMember.class);
					return v1.value() - v2.value();
				} catch (ClassNotFoundException e) {
					return 0;
				}
			}
		});
		
		return result;
	}
	
	public static final String join(final String ... args) {
		final StringBuilder sb = new StringBuilder();
		for (final String str : args)
			sb.append(str);
		return sb.toString();
	}
	
	public static final CtField createPrivateFinalField(final CtClass type, final String name, final CtClass parent) throws CannotCompileException {
		final CtField field = new CtField(type, name, parent);
		field.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
		return field;
	}
	
}
