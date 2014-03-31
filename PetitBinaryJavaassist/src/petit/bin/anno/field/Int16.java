package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType(short.class)
@SupportType({
	short.class, Short.class,
	int.class, Integer.class,
	long.class, Long.class})
public @interface Int16 {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
					.append(" = ")
					.append(CodeFragments.READER.invoke("readInt16"))
					.append(';')
					.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.WRITER.invoke("writeInt16", "(short)" + CodeFragments.ACCESS_INSTANCE.of(field.getName())))
					.append(';')
					.toString();
		}
		
	}
	
}
