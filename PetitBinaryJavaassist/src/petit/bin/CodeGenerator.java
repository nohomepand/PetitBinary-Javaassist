package petit.bin;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtField;
import javassist.NotFoundException;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.Struct;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;
import petit.bin.util.KnownCtClass;
import petit.bin.util.Pair;
import petit.bin.util.Util;
import petit.bin.util.instor.Instantiator;

/**
 * DSL的な表現からコードを生成するもの<br />
 * {@link MemberAnnotationMetaAgent#makeReaderSource(CtField)} および {@link MemberAnnotationMetaAgent#makeWriterSource(CtField)} で使用する
 * 
 * @author 俺用
 * @since 2014/04/02 PetitBinaryJavaassist
 *
 */
public final class CodeGenerator {
	
	/**
	 * ソースコード中の識別子の定数
	 * 
	 * @author 俺用
	 * @since 2014/03/31 PetitBinaryJavaassist
	 *
	 */
	public static enum CodeFragments {
		/**
		 * シリアライズクラス型のインスタンスを持つ変数名(多分メソッドのパラメータ)
		 */
		VarTarget("_tgt"),
		
		/**
		 * シリアライズクラス型のインスタンスを生成する {@link Instantiator} 型のインスタンスを持つ変数名(多分インスタンスフィールド)
		 */
		VarTargetInstor("_instor"),
		
		/**
		 * シリアライズクラス型の {@link Class} のインスタンスを持つ変数名(多分インスタンスフィールド)
		 */
		VarTargetClass("_clazz"),
		
		/**
		 * シリアライズクラスに指示された {@link Struct} アノテーションのインスタンスを持つ変数名(多分インスタンスフィールド)
		 */
		VarAccessStructAnnotation("_anno"),
		
		/**
		 * {@link ReadableStore} のインスタンスを持つ変数名(多分メソッドのパラメータ)
		 */
		VarReader("src"),
		
		/**
		 * {@link WritableStore} のインスタンスを持つ変数名(多分メソッドのパラメータ)
		 */
		VarWriter("dst"),
		
		/**
		 * {@link Instantiator} のcanonical name
		 */
		TypeInstor(KnownCtClass.INSTANTIATOR.BINARYNAME),
		
		/**
		 * {@link Util} のcanonical name
		 */
		TypeUtil(Util.class.getCanonicalName()),
		
		/**
		 * {@link PetitSerializer} のcanonical name
		 */
		TypeSerAdapFactory(KnownCtClass.SERIALIZE_ADAPTER_FACTORY.BINARYNAME),
		
		/**
		 * {@link SerializeAdapter} のcanonical name
		 */
		TypeSerAdap(KnownCtClass.ISERIALIZE_ADAPTER.BINARYNAME),
		;
		
		/**
		 * 識別子
		 */
		public final String ID;
		
		private CodeFragments(final String id) {
			ID = id;
		}
		
		/**
		 * この {@link #ID} のメンバを表す式<br />
		 * 
		 * {@link #ID}.expr な文字列を返す
		 * 
		 * @param expr 式
		 * @return {@link #ID}.expr な文字列
		 */
		public final String of(final String expr) {
			return ID + "." + expr;
		}
		
	}
	
	public static enum CGIDs {
		/**
		 * instance.field_name な文字列
		 */
		VarField,
		
		/**
		 * instance.field_name の定義上のcanonical な型名を表す文字列
		 */
		TypeField,
		
		/**
		 * instance.field_name が配列の場合，そのコンポーネント型のcanonical な型名を表す文字列
		 */
		TypeFieldComponent,
		
		/**
		 * instance.field_name が配列の場合，指定された配列の大きさを与える式を表す文字列
		 */
		ExprFieldSizeGetter,
		
	}
	
	/**
	 * $で囲まれた文字列を得るための正規表現
	 */
	public static final Pattern ID_REGEX = Pattern.compile("\\$(.+?)\\$");
	
	private final Map<String, String> _mapper;
	
	public CodeGenerator(final CtField field) throws CannotCompileException {
		_mapper = new HashMap<>();
		
		try {
			if (field != null) {
				map(CGIDs.VarField, CodeFragments.VarTarget.of(field.getName()));
				
				final Pair<Class<?>, Boolean> toc = Util.toClass(field.getType());
				map(CGIDs.TypeField, toc.FIRST.getCanonicalName());
				if (toc.FIRST.isArray()) map(CGIDs.TypeFieldComponent, toc.FIRST.getComponentType().getCanonicalName());
				
				if (field.getType().isArray()) {
					String size_val = null;
					final ArraySizeConstant i1 = (ArraySizeConstant) field.getAnnotation(ArraySizeConstant.class);
					if (i1 != null)
						size_val = Integer.toString(i1.value());
					
					final ArraySizeByField i2 = (ArraySizeByField) field.getAnnotation(ArraySizeByField.class);
					if (i2 != null)
						size_val = CodeFragments.VarTarget.of(field.getName());
					
					final ArraySizeByMethod i3 = (ArraySizeByMethod) field.getAnnotation(ArraySizeByMethod.class);
					if (i3 != null)
						size_val = CodeFragments.VarTarget.ID + "." + i3.value() + "(" + CodeFragments.VarReader.ID + ")";
					
					if (size_val == null)
						throw new CannotCompileException("No array size indicator found for " + field);
				}
			}
			
			for (final CodeFragments elm : CodeFragments.values())
				map(elm, elm.ID);
		} catch (CannotCompileException e) {
			throw e;
		} catch (Exception e) {
			throw new CannotCompileException(e);
		}
		System.out.println(_mapper);
	}
	
	private final void map(final Enum<?> id, final String replacement) {
		_mapper.put(id.name().toLowerCase(), replacement);
	}
	
	public final void map(final String id, final String replacement) {
		_mapper.put(id.toLowerCase(), replacement);
	}
	
	public final String replaceAll(final String src) {
		String result = src;
		Matcher m = ID_REGEX.matcher(result);
		while (m.find()) {
			final String found = m.group(1);
			final String replacement = _mapper.get(found.toLowerCase());
			if (replacement == null)
				throw new IllegalArgumentException(found + " is not mapped");
			result = m.replaceFirst(replacement);
			m = ID_REGEX.matcher(result);
		}
		return result;
	}
	public static void main(String[] args) throws CannotCompileException, NotFoundException {
		final CodeGenerator cg = new CodeGenerator(ClassPool.getDefault().getOrNull("petit.bin.test.Test1").getField("v5"));
		final String s = "{" + "\n" +
				"$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeField.class);" + "\n" + 
				"$varField$ = ($typeField$) sa.read($varReader$);" +  "\n" +
				"}";
		
		System.out.println(cg.replaceAll(s));
	}
	
}
