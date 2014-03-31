package petit.bin;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.anno.field.ExternStruct;
import petit.bin.anno.field.Float32;
import petit.bin.anno.field.Float64;
import petit.bin.anno.field.Int16;
import petit.bin.anno.field.Int32;
import petit.bin.anno.field.Int64;
import petit.bin.anno.field.Int8;
import petit.bin.anno.field.Int8Boolean;
import petit.bin.anno.field.UInt16;
import petit.bin.anno.field.UInt32;
import petit.bin.anno.field.UInt8;
import petit.bin.anno.field.array.ExternStructArray;
import petit.bin.anno.field.array.Float32Array;
import petit.bin.anno.field.array.Float64Array;
import petit.bin.anno.field.array.Int16Array;
import petit.bin.anno.field.array.Int32Array;
import petit.bin.anno.field.array.Int64Array;
import petit.bin.anno.field.array.Int8Array;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;

/**
 * {@link MemberAnnotationMetaAgent} のファクトリ
 * 
 * @author 俺用
 * @since 2014/03/31 PetitBinaryJavaassist
 *
 */
public final class MetaAgentFactory {
	
	private static final Map<Class<? extends Annotation>, MemberAnnotationMetaAgent> _member_anno_map = new HashMap<>();
	
	private static final Map<Class<?>, MemberAnnotationMetaAgent> _default_agent_map = new HashMap<>();
	
	private static MemberAnnotationMetaAgent _extern_ma, _extern_array_ma;
	
	private static final Map<CtClass, Class<?>> _primitive_ctclass_map;
	
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
		
		addMetaAgent(UInt8.class);
		addMetaAgent(UInt16.class);
		addMetaAgent(UInt32.class);
		addMetaAgent(Int8.class);
		addMetaAgent(Int16.class);
		addMetaAgent(Int32.class);
		addMetaAgent(Int64.class);
		addMetaAgent(Int8Boolean.class);
		addMetaAgent(Float32.class);
		addMetaAgent(Float64.class);
		addMetaAgent(Int8Array.class);
		addMetaAgent(Int16Array.class);
		addMetaAgent(Int32Array.class);
		addMetaAgent(Int64Array.class);
		addMetaAgent(Float32Array.class);
		addMetaAgent(Float64Array.class);
//		addMetaAgent(CharArray.class);
		try {
			{
				final Class<?> mac = findMetaAgentClass(ExternStruct.class);
				_extern_ma = (MemberAnnotationMetaAgent) mac.newInstance();
			} {
				final Class<?> mac = findMetaAgentClass(ExternStructArray.class);
				_extern_array_ma = (MemberAnnotationMetaAgent) mac.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
			_extern_ma = null;
			_extern_array_ma = null;
		}
	}
	
	private static final void addMetaAgent(final Class<? extends Annotation> member_anno) {
		try {
			final Class<?> metaag_clazz = findMetaAgentClass(member_anno);
			final DefaultFieldAnnotationType default_types = member_anno.getAnnotation(DefaultFieldAnnotationType.class);
			final SupportType support_types = member_anno.getAnnotation(SupportType.class);
			final MemberAnnotationMetaAgent metaag = (MemberAnnotationMetaAgent) metaag_clazz.newInstance();
			metaag.setSupportTypes(support_types == null ? null : support_types.value());
			_member_anno_map.put(member_anno, metaag);
			if (default_types != null) {
				for (final Class<?> c : default_types.value()) {
					if (_default_agent_map.containsKey(c))
						throw new IllegalStateException("Default type of " + c + " is already defined by " + _default_agent_map.get(c).getTargetAnnotation());
					_default_agent_map.put(c, metaag);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static final Class<?> findMetaAgentClass(final Class<?> member_anno) {
		for (final Class<?> mc : member_anno.getDeclaredClasses()) {
			if (MemberAnnotationMetaAgent.class.isAssignableFrom(mc))
				return mc;
		}
		throw new UnsupportedOperationException("Cannot find a sub-class of" + MemberAnnotationMetaAgent.class.getCanonicalName() + " in " + member_anno);
	}
	
	public static final MemberAnnotationMetaAgent getMetaAgent(final CtField field) {
		for (final Entry<Class<? extends Annotation>, MemberAnnotationMetaAgent> ent : _member_anno_map.entrySet()) {
			try {
				final Object anno = field.getAnnotation(ent.getKey());
				if (anno != null)
					return ent.getValue();
			} catch (ClassNotFoundException e) {}
		}
		
		// use default
		try {
			final CtClass field_ctype = field.getType();
			final Class<?> field_type;
			if (field_ctype.isPrimitive())
				field_type = _primitive_ctclass_map.get(field_ctype);
			else
				field_type = field_ctype.toClass();
//			System.err.println(field_ctype + " -> " + field_type + " -> " + _default_agent_map.get(field_type));
			final MemberAnnotationMetaAgent maybe_null = _default_agent_map.get(field_type);
			if (maybe_null != null)
				return maybe_null;
			
			// use extern struct
//			if (ct)
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UnsupportedOperationException("Cannot find a meta-agent for " + field.getSignature());
	}
	
	/**
	 * ソースコード中の識別子の定数
	 * 
	 * @author 俺用
	 * @since 2014/03/31 PetitBinaryJavaassist
	 *
	 */
	public static enum CodeFragments {
		/**
		 * シリアライズクラス型のインスタンスを持つ変数名
		 */
		ACCESS_INSTANCE("_1"),
		
		/**
		 * {@link ReadableStore} のインスタンスを持つ変数名
		 */
		READER("src"),
		
		/**
		 * {@link WritableStore} のインスタンスを持つ変数名
		 */
		WRITER("dst")
		;
		
		/**
		 * 識別子
		 */
		public final String ID;
		
		private CodeFragments(final String id) {
			ID = id;
		}
		
		/**
		 * {@link #ID}.expr な文字列を返す
		 * 
		 * @param expr 式
		 * @return {@link #ID}.expr な文字列
		 */
		public final String of(final String expr) {
			return ID + "." + expr;
		}
		
		/**
		 * {@link #ID}.expr.length な文字列を返す
		 * 
		 * @param expr 式
		 * @return {@link #ID}.expr.length な文字列
		 */
		public final String ofArrayLength(final String expr) {
			return ID + "." + expr + ".length";
		}
		
		/**
		 * {@link #ID}.expr[index_expr] な文字列を返す
		 * 
		 * @param expr 式
		 * @param index_expr インデックス
		 * @return {@link #ID}.expr.length な文字列
		 */
		public final String ofElement(final String expr, final String index_expr) {
			return ID + "." + expr + "[" + index_expr + "]";
		}
		
		/**
		 * {@link #ID}.name() な文字列を返す
		 * 
		 * @param name メソッド名
		 * @return {@link #ID}.name() な文字列
		 */
		public final String invoke(final String name) {
			return new StringBuilder().append(ID).append('.').append(name).append("()").toString();
		}
		
		/**
		 * {@link #ID}.name(args[0], ...) な文字列を返す
		 * 
		 * @param name メソッド名
		 * @param args 引数リスト
		 * @return {@link #ID}.name(args[0], ...) な文字列
		 */
		public final String invoke(final String name, final String ... args) {
			final StringBuilder sb = new StringBuilder();
			sb.append(ID).append('.').append(name).append('(');
			for (int i = 0; i < args.length; i++) {
				sb.append(args[i]);
				if (i != args.length - 1)
					sb.append(',');
			}
			return sb.append(')').toString();
		}
		
	}
	
	/**
	 * シリアライズクラスのメンバのアノテーション({@link UInt8}など)のメタ情報を保存し，
	 * 実際のメンバが与えられた場合の動作を処理可能なものを表す
	 * 
	 * @author 俺用
	 * @since 2014/03/31 PetitBinaryJavaassist
	 *
	 */
	public static abstract class MemberAnnotationMetaAgent {
		
		private Class<? extends Annotation> _target_anno;
		
		private final Set<Class<?>> _support_types;
		
		public MemberAnnotationMetaAgent() {
			_support_types = new HashSet<>();
		}
		
		/**
		 * 対応する型を設定する
		 * 
		 * @param support_types 対応する型
		 */
		final void setSupportTypes(final Class<?>[] support_types) {
			_support_types.addAll(Arrays.asList(support_types));
		}
		
		/**
		 * 対応する型を得る
		 * 
		 * @return 対応する型
		 */
		public final Set<Class<?>> getSupportTypes() {
			return _support_types;
		}
		
		final void setTargetAnnotation(final Class<? extends Annotation> target_anno) {
			_target_anno = target_anno;
		}
		
		public final Class<? extends Annotation> getTargetAnnotation() {
			return _target_anno;
		}
		
		/**
		 * {@link ArraySizeConstant} または {@link ArraySizeByField} または {@link ArraySizeByMethod} によって指示される対象のフィールドの配列の大きさを得るためのコードの断片を得る<br />
		 * 典型的には次のようなものが得られる
		 * <pre>
		 * {@literal @ArraySizeConstant(10)} = "10"
		 * {@literal @ArraySizeByField("foo")} = "_1.foo"
		 * {@literal @ArraySizeByMethod("bar")} = "_1.bar(_2)"
		 * </pre>
		 * _1や_2などの識別子の意味は {@link CodeFragments} で定義されている
		 * 
		 * @param field 対象のフィールド
		 * @return 対象のフィールドの配列の大きさを得るためのコードの断片
		 * @throws CannotCompileException
		 */
		public final String makeArraySizeIndicator(final CtField field) throws CannotCompileException {
			try {
				{
					final ArraySizeConstant i1 = (ArraySizeConstant) field.getAnnotation(ArraySizeConstant.class);
					if (i1 != null) {
						return Integer.toString(i1.value());
					}
				} {
					final ArraySizeByField i2 = (ArraySizeByField) field.getAnnotation(ArraySizeByField.class);
					if (i2 != null) {
						return CodeFragments.ACCESS_INSTANCE.of(field.getName());
					}
				} {
					final ArraySizeByMethod i3 = (ArraySizeByMethod) field.getAnnotation(ArraySizeByMethod.class);
					if (i3 != null) {
						return CodeFragments.ACCESS_INSTANCE.invoke(i3.value(), CodeFragments.READER.ID);
					}
				}
				
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
			
			throw new CannotCompileException("No array size indicator found for " + field);
		}
		
		/**
		 * 対象のメンバに対する読み込みを表すソースコードを生成する
		 * 
		 * @param field 対象のメンバ
		 * @return 対象のメンバに対する読み込みを表すソースコード
		 */
		public abstract String makeReaderSource(final CtField field) throws CannotCompileException;
		
		/**
		 * 対象のメンバを書き込むことを表すソースコードを生成する
		 * 
		 * @param field 対象のメンバ
		 * @return 対象のメンバを書き込むことを表すソースコード
		 */
		public abstract String makeWriterSource(final CtField field) throws CannotCompileException;
		
//		/**
//		 * 対象のメンバに対する読み込みを表すソースコードを生成する
//		 * 
//		 * @param access_base 対象のフィールドにアクセスしようとしているクラス
//		 * @param access_inst access_base 型のインスタンスの参照を表す文字列
//		 * @param field 対象のメンバが定義されたクラスと，対象のメンバからなるペア
//		 * @return 対象のメンバに対する読み込みを表すソースコード
//		 */
//		public abstract String makeReaderSource(final Pair<Class<?>, CtClass> access_base, final String access_inst, final Pair<Class<?>, CtField> field);
//		
//		/**
//		 * 対象のメンバを書き込むことを表すソースコードを生成する
//		 * 
//		 * @param access_base 対象のフィールドにアクセスしようとしているクラス
//		 * @param access_inst access_base 型のインスタンスの参照を表す文字列
//		 * @param field 対象のメンバが定義されたクラスと，対象のメンバからなるペア
//		 * @return 対象のメンバを書き込むことを表すソースコード
//		 */
//		public abstract String makeWriterSource(final Pair<Class<?>, CtClass> access_base, final String access_inst, final Pair<Class<?>, CtField> field);
		
	}
}
