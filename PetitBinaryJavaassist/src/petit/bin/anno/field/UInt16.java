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
 * 16ビット符号なし整数型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     char, int, long
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     char
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(char.class)
@SupportType({
	char.class,
	int.class,
	long.class})
public @interface UInt16 {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varField$ = (char) ($varReader$.readInt16() & 0xffff);");
		}
		
		@Override
		public String makeWriterSource(CtClass adapter_clazz, CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varWriter$.writeInt16((short) ($varField$ & 0xffff));");
		}
		
	}
	
}
