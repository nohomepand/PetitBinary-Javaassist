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
@DefaultFieldAnnotationType(byte.class)
@SupportType({
	byte.class, Byte.class,
	short.class, Short.class,
	int.class, Integer.class,
	long.class, Long.class})
public @interface Int8 {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		public _MA() {
			
		}
		
		@Override
		public String makeReaderSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
					.append(" = ")
					.append(CodeFragments.READER.invoke("readInt8"))
					.append(';')
					.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) {
			return new StringBuilder()
					.append(CodeFragments.WRITER.invoke("writeInt8", "(byte)" + CodeFragments.ACCESS_INSTANCE.of(field.getName())))
					.append(';')
					.toString();
		}
		
	}
//	
//	public static final class _MA extends MemberAccessor {
//		
//		public _MA(final BinaryAccessorFactory ba_fac, final Field f) {
//			super(f);
//		}
//		
//		@Override
//		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
//			_field.setByte(inst, src.readInt8());
//		}
//		
//		@Override
//		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
//			dst.writeInt8(_field.getByte(inst));
//		}
//		
//	}
	
}
