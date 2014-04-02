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
import javassist.CtMethod;
import javassist.NotFoundException;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;
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
import petit.bin.util.Pair;
import petit.bin.util.Util;

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
	
//	private static final Map<CtClass, Class<?>> _primitive_ctclass_map;
	
	static {
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
			final MemberDefaultType default_types = member_anno.getAnnotation(MemberDefaultType.class);
			final SupportType support_types = member_anno.getAnnotation(SupportType.class);
			final MemberAnnotationMetaAgent metaag = (MemberAnnotationMetaAgent) metaag_clazz.newInstance();
			metaag.setSupportTypes(support_types == null ? null : support_types.value());
			metaag.setTargetAnnotation(member_anno);
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
		throw new UnsupportedOperationException("Cannot find a sub-class of " + MemberAnnotationMetaAgent.class.getCanonicalName() + " in " + member_anno);
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
			final Pair<Class<?>, Boolean> clazz = Util.toClass(field.getType());
			if (clazz.SECOND == null) {
				return _default_agent_map.get(clazz.FIRST);
			} else if (clazz.SECOND) {
				return _extern_array_ma;
			} else {
				return _extern_ma;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new UnsupportedOperationException("Cannot find a meta-agent for " + field.getSignature());
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
		
		/**
		 * 初期化
		 */
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
		
		/**
		 * 対象のフィールドが正しい型か検証する
		 * 
		 * @param vVarField 対象のフィールド
		 */
		public final void checkSupportTypes(final CtField field) throws CannotCompileException {
			if (_support_types == null || _support_types.isEmpty())
				return; // any type is ok
			
			try {
				final Pair<Class<?>, Boolean> type = Util.toClass(field.getType());
				for (final Class<?> vt : _support_types)
					if (vt.equals(type.FIRST))
						return;
				throw new CannotCompileException(field + " is not valid type for " + _target_anno.getCanonicalName() + ". The type must be one of " + _support_types);
			} catch (CannotCompileException e) {
				throw e;
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
			
		}
		
		/**
		 * {@link Int32} などのフィールドアノテーションを設定する
		 * 
		 * @param target_anno フィールドアノテーション
		 */
		final void setTargetAnnotation(final Class<? extends Annotation> target_anno) {
			_target_anno = target_anno;
		}
		
		/**
		 * {@link Int32} などのフィールドアノテーションを得る
		 * 
		 * @return フィールドアノテーション
		 */
		public final Class<? extends Annotation> getTargetAnnotation() {
			return _target_anno;
		}
		
		/**
		 * メソッドの参照を得る
		 * 
		 * @param clazz メソッドが定義されている，またはメソッドが可視なクラス
		 * @param name メソッド名
		 * @param return_type 戻り値の型
		 * @param params パラメータ
		 * @return メソッドの参照(親クラスで定義されているものの場合もある)または検索できない場合は null
		 */
		public final CtMethod getCtMethod(final CtClass clazz, final String name, final Class<?> return_type, final Class<?> ... params) {
			if (clazz == null)
				throw new NullPointerException("Argument clazz must not be null");
			if (name == null)
				throw new NullPointerException("Argument name must not be null");
			if (return_type == null)
				throw new NullPointerException("Argument return_type must not be null");
			if (params == null)
				throw new NullPointerException("Argument params must not be null");
			
			
			final StringBuilder sig = new StringBuilder();
			sig.append("(");
			for (final Class<?> param : params)
				sig.append(param.getName());
			sig.append(")");
			sig.append(return_type.getName());
			try {
				return clazz.getMethod(name, sig.toString());
			} catch (NotFoundException e) {
				return null;
			}
		}
		
		/**
		 * 対象のメンバに対する読み込みを表すソースコードを生成する
		 * 
		 * @param vVarField 対象のメンバ
		 * @return 対象のメンバに対する読み込みを表すソースコード
		 */
		public abstract String makeReaderSource(final CtField field) throws CannotCompileException;
		
		/**
		 * 対象のメンバを書き込むことを表すソースコードを生成する
		 * 
		 * @param vVarField 対象のメンバ
		 * @return 対象のメンバを書き込むことを表すソースコード
		 */
		public abstract String makeWriterSource(final CtField field) throws CannotCompileException;
		
	}
}
