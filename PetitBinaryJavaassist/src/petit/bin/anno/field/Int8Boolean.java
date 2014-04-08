package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * 8ビット整数値で表現される真偽型を表す<br />
 * このフィールドの読み込みでは，まず 8ビット整数値を読み込まれ，その値が 0の場合に falseが， 0以外の場合に trueがフィールドに与えられる<br />
 * このフィールドの書き込みでは， trueの場合に 1，false の場合に 0が書き込まれる
 * 
 * <pre>
 * 対応するフィールドの型:
 *     boolean
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     boolean
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(boolean.class)
@SupportType({boolean.class})
public @interface Int8Boolean {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		@Override
		public String makeReaderSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varField$ = $varReader$.readInt8() != 0;");
		}
		
		@Override
		public String makeWriterSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varWriter$.writeInt8((byte) ($varField$ ? 1 : 0));");
		}
		
	}
	
}
