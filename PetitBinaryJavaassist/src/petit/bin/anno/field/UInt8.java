package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.SupportType;

/**
 * 符号なし 8ビット整数値を表す
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SupportType({
	short.class,
	int.class,
	long.class,})
public @interface UInt8 {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
					.append(" = (short)")
					.append('(')
					.append(CodeFragments.READER.invoke("readInt8"))
					.append(" & 0xff)")
					.append(';')
					.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.WRITER.invoke(
							"writeInt8",
							"(byte)(" + CodeFragments.ACCESS_INSTANCE.of(field.getName()) + " & 0xff)"
					))
					.append(';')
					.toString();
		}
		
	}
	
}
