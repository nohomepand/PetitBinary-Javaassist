package petit.bin.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * リフレクションのユーティリティ
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public final class ReflectionUtil {
	
	/**
	 * 可視性を検証可能な定数
	 * 
	 * @author 俺用
	 * @since 2014/03/19 PetitBinarySerialization
	 *
	 */
	public static enum VisibilityConstraint {
		
		/**
		 * public な可視性<br />
		 * {@link #isVisible(Class scope, Member target)} は targetが publicな場合のみ trueを返す
		 */
		PUBLIC_VIEWPOINT {
			@Override
			public boolean isVisible(Class<?> scope, Member target) {
				return (target.getModifiers() & MASK_PUBLIC_OVER) != 0;
			}
		},
		
		/**
		 * package private な可視性<br />
		 * {@link #isVisible(Class scope, Member target)} は targetが宣言されたクラスと scopeが属するクラスが同一パッケージの場合に trueを返す
		 */
		PACKAGE_VIEWPOINT {
			@Override
			public boolean isVisible(Class<?> scope, Member target) {
				return target.getDeclaringClass().getPackage().equals(scope.getPackage()) &&
						(target.getModifiers() & MASK_PROTECTED_OVER) != 0;
			}
		},
		
		/**
		 * 継承時の可視性<br />
		 * {@link #isVisible(Class scope, Member target)} は targetが宣言されたクラスが，
		 * scopeと等しいクラスの場合は常に trueを，
		 * scopeの親クラスの場合で targetが protectedまたは publicの場合は trueを，
		 * それ以外のクラスの場合で targetが publicの場合に trueを返す
		 */
		INHERITED_CLASS_VIEWPOINT {
			@Override
			public boolean isVisible(Class<?> scope, Member target) {
				final Class<?> target_dec_clazz = target.getDeclaringClass();
				if (target_dec_clazz.equals(scope)) {
					return true;
				} else if (target_dec_clazz.isAssignableFrom(scope)) {
					return (target.getModifiers() & MASK_PROTECTED_OVER) != 0;
				} else {
					if (scope.isMemberClass() && (scope.getModifiers() & Modifier.STATIC) != 0) {
						/*
						 * class ... {
						 *     class <scope> { // if <scope> class is not a static member class
						 *         ...
						 *     }
						 *     ... <target> ... // check whether the <target> member is defined at enclosing class or not.
						 * }
						 */
						for (Class<?> enc = scope.getEnclosingClass(); enc != null; enc = enc.getEnclosingClass()) {
							if (enc.equals(target_dec_clazz))
								return (target.getModifiers() & MASK_PRIVATE_OVER) != 0;
						}
					}
					return (target.getModifiers() & MASK_PUBLIC_OVER) != 0;
				}
			}
		},
		
		/**
		 * 可視性による制約を無視した可視性<br />
		 * {@link #isVisible(Class scope, Member target)} は常に trueを返す
		 */
		ANY {
			@Override
			public boolean isVisible(Class<?> scope, Member target) {
				return true;
			}
		}
		
		;
		
		/**
		 * a mask bit map of modifier flag: {@link Modifier#PUBLIC}
		 */
		private static final int MASK_PUBLIC_OVER = Modifier.PUBLIC;
		
		/**
		 * a mask bit map of modifier flag: {@link Modifier#PUBLIC} | {@link Modifier#PROTECTED}
		 */
		private static final int MASK_PROTECTED_OVER = Modifier.PUBLIC | Modifier.PROTECTED;
		
		/**
		 * a mask bit map of modifier flag: {@link Modifier#PUBLIC} | {@link Modifier#PROTECTED} | {@link Modifier#PRIVATE} 
		 */
		private static final int MASK_PRIVATE_OVER = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
		
		/**
		 * 対象のスコープ内で，対象のメンバが可視かどうか検証する
		 * 
		 * @param scope 対象のスコープ
		 * @param target 対象のコンテキストのメンバ
		 * @return 対象のスコープで対象のコンテキストのメンバが可視の場合は true
		 */
		public abstract boolean isVisible(final Class<?> scope, final Member target);
		
	}
	
	/**
	 * クラスの継承ツリーを渡り歩いてメンバを走査するときに使用されるコールバックメソッド
	 * 
	 * @author 俺用
	 * @since 2014/03/19 PetitBinarySerialization
	 *
	 */
	public static interface WalkClassInheritanceTreeCallBack<M extends Member> {
		
		/**
		 * コールバックメソッド
		 * 
		 * @param ao 対象のメンバ
		 * @return true で走査を継続
		 */
		public abstract boolean visit(final M ao);
		
	}
	
	/**
	 * 対象のクラスの継承関係を親クラスの方へ向けて辿りつつ，全てのクラスの {@link Class#getDeclaredFields()} を走査する
	 * 
	 * @param begin 対象のクラス
	 * @param vc 可視性の制約
	 * @param walker コールバックメソッド
	 */
	public static final void visitField(final Class<?> begin, final VisibilityConstraint vc, final WalkClassInheritanceTreeCallBack<Field> walker) {
		for (Class<?> cur = begin; cur != null; cur = cur.getSuperclass()) {
			for (final Field field : cur.getDeclaredFields()) {
				if (vc.isVisible(begin, field))
					if (!walker.visit(field))
						return;
			}
		}
	}
	
	/**
	 * 対象のクラスの継承関係を親クラスの方へ向けて辿りつつ，全てのクラスの {@link Class#getDeclaredMethods()} を走査する
	 * 
	 * @param begin 対象のクラス
	 * @param vc 可視性の制約
	 * @param walker コールバックメソッド
	 */
	public static final void visitMethod(final Class<?> begin, final VisibilityConstraint vc, final WalkClassInheritanceTreeCallBack<Method> walker) {
		for (Class<?> cur = begin; cur != null; cur = cur.getSuperclass()) {
			for (final Method method : cur.getDeclaredMethods()) {
				if (vc.isVisible(begin, method))
					if (!walker.visit(method))
						return;
			}
		}
	}
	
	public static final List<Constructor<?>> getConstructors(final Class<?> clazz, final VisibilityConstraint vc, final Class<?> ... parameters) {
		if (clazz == null)
			throw new NullPointerException("clazz must not be null");
		if (vc == null)
			throw new NullPointerException("vc must not be null");
		
		final List<Constructor<?>> result = new ArrayList<Constructor<?>>();
		for (final Constructor<?> ctor : clazz.getDeclaredConstructors())
			if (vc.isVisible(clazz, ctor)) {
				final boolean param_check = (parameters == null) || Arrays.equals(parameters, ctor.getParameterTypes());
				if (param_check)
					result.add(ctor);
			}
		return result;
	}
	
	/**
	 * 対象のクラスの継承ツリーを辿り，合致するフィールドを列挙する
	 * 
	 * @param begin 対象のクラス
	 * @param vc 可視性の制約
	 * @param field_name 合致するフィールド名(nullの場合は全てのフィールド名に合致)
	 * @param field_type 合致するフィールドの型(nullの場合は全てのフィールドの型に合致)，アップキャスト可能な場合も合致する
	 * @return 合致したフィールド
	 */
	public static final List<Field> getVisibleFields(final Class<?> begin, final VisibilityConstraint vc, final String field_name, final Class<?> field_type) {
		if (begin == null)
			throw new NullPointerException("begin must not be null");
		if (vc == null)
			throw new NullPointerException("vc must not be null");
		
		final List<Field> result = new ArrayList<Field>();
		visitField(begin, vc, new WalkClassInheritanceTreeCallBack<Field>() {
			@Override
			public boolean visit(Field ao) {
				final boolean name_check = (field_name == null) || field_name.equals(ao.getName());
				final boolean type_check = (field_type == null) || field_type.isAssignableFrom(ao.getType());
				if (name_check && type_check)
					result.add(ao);
				return true;
			}
		});
		return result;
	}
	
	/**
	 * 対象のクラスの継承ツリーを辿り，合致するメソッドを列挙する
	 * 
	 * @param begin 対象のクラス
	 * @param vc 可視性の制約
	 * @param method_name 合致するメソッド名(nullの場合は全てのメソッド名に合致)
	 * @param return_type 合致するメソッドの戻り値の型(nullの場合は全てのメソッドの戻り値の型に合致)，型がアップキャストで合致する場合は合致する
	 * @param parameters 合致するメソッドの仮引数の型のリスト(nullの場合は全てのメソッドの仮引数の型のリストに合致)，アップキャスト可能な場合でも異なる型の場合は合致しない
	 * @return 合致したメソッド
	 */
	public static final List<Method> getVisibleMethods(final Class<?> begin, final VisibilityConstraint vc, final String method_name, final Class<?> return_type, final Class<?> ... parameters) {
		if (begin == null)
			throw new NullPointerException("begin must not be null");
		if (vc == null)
			throw new NullPointerException("vc must not be null");
		
		final List<Method> result = new ArrayList<Method>();
		visitMethod(begin, vc, new WalkClassInheritanceTreeCallBack<Method>() {
			@Override
			public boolean visit(Method ao) {
				final boolean name_check = (method_name == null) || method_name.equals(ao.getName());
				final boolean return_type_check = (return_type == null) || return_type.isAssignableFrom(ao.getReturnType());
				final boolean param_check = (parameters == null) || Arrays.equals(parameters, ao.getParameterTypes());
				
				if (name_check && return_type_check && param_check)
					result.add(ao);
				return true;
			}
		});
		return result;
	}
	
	/**
	 * clazz のインスタンスを構築可能な方法を得る
	 * 
	 * @param clazz クラス
	 * @return clazz のインスタンスを構築可能な方法，または構築可能な方法がなければ null
	 */
	public static final Instantiator getInstantiator(final Class<?> clazz) {
		try {
			return new NullaryInstantiator(clazz);
		} catch (Exception e) {
			if (UnsafeInstantiator.isAvailable())
				return new UnsafeInstantiator(clazz);
			else
				return null;
		}
	}
	
}
