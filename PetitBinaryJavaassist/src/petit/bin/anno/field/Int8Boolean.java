package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(boolean.class)
@SupportType({boolean.class})
public @interface Int8Boolean {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varField$ = $varReader$.readInt8() != 0;");
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll("$varWriter$.writeInt8((byte) ($varField$ ? 1 : 0));");
		}
		
	}
	
}
