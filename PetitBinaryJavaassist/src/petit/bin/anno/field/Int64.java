package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(long.class)
@SupportType({
	long.class})
public @interface Int64 {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
					.append(" = ")
					.append(CodeFragments.READER.invoke("readInt64"))
					.append(';')
					.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.WRITER.invoke("writeInt64", CodeFragments.ACCESS_INSTANCE.of(field.getName())))
					.append(';')
					.toString();
		}
		
	}
	
}
