package petit.bin.anno.field.buffer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.SupportType;

/**
 * byte 型の配列の代わりに {@link ByteBuffer} を使うもの
 * 
 * @author 俺用
 * @since 2014/04/13 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SupportType({
	ByteBuffer.class,})
public @interface Int8Buffer {
	
	/**
	 * フィールドの読み込み前に {@link ByteBuffer} を初期化するメソッド名<br />
	 * このメソッドのシグネチャは ()Ljava/nio/ByteBuffer; でなければならない<br />
	 * このメソッドの呼び出し結果である {@link ByteBuffer} に対して， {@link ByteBuffer#position()} から {@link ByteBuffer#limit()} までが読み込まれる
	 * 
	 * @return フィールドの読み込み前に {@link ByteBuffer} を初期化するメソッド名
	 */
	public abstract String preLoad();
	
	/**
	 * フィールドの書き込み前に {@link ByteBuffer} を初期化するメソッド名<br />
	 * このメソッドのシグネチャは ()Ljava/nio/ByteBuffer; でなければならない<br />
	 * このメソッドの呼び出し結果である {@link ByteBuffer} に対して， {@link ByteBuffer#position()} から {@link ByteBuffer#limit()} までが書き込まれる
	 * 
	 * @return フィールドの書き込み前に {@link ByteBuffer} を初期化するメソッド名
	 */
	public abstract String preStore();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final Int8Buffer anno = (Int8Buffer) field.getAnnotation(Int8Buffer.class);
				cg.map("strPreLoad", anno.preLoad());
				return cg.replaceAll(
						"{\n" +
						"	$varField$ = $varTarget$.$strPreLoad$();\n" +
						"	if ($varField$ != null) {\n" +
						"		for (int i = 0, size = $varField$.limit(); i < size; i++)\n" +
						"			$varField$.put($varReader$.readInt8());\n" +
						"		$varField$.flip();\n" +
						"	}\n" +
						"}");
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final Int8Buffer anno = (Int8Buffer) field.getAnnotation(Int8Buffer.class);
				cg.map("strPreStore", anno.preStore());
				return 	cg.replaceAll(
						"if ($varField$ != null) {\n" +
						"	$varField$ = $varTarget$.$strPreStore$();\n" +
						"	for (int i = 0, size = $varField$.limit(); i < size; i++)\n" +
						"		$varWriter$.writeInt8($varField$.get());\n" +
						"	$varField$.flip();\n" +
						"}");
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
	}
	
}
