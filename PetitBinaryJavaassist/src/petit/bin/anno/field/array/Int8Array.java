package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.DefaultFieldAnnotationType;
import petit.bin.anno.SupportType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@DefaultFieldAnnotationType(byte[].class)
@SupportType(byte[].class)
public @interface Int8Array {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		private static final String raw_type = "byte";
		
		private static final String store_type = "Int8";
		
		@Override
		public String makeReaderSource(CtField field) throws CannotCompileException {
			final String f = field.getName();
			final StringBuilder sb = new StringBuilder();
			/* 
			 * {
			 *     int size = <ind>;
			 *     if (<field> == null || <field>.length != size)
			 *         <field> = new <raw_type>[size];
			 *     for (int i = 0; i < <field>.length; i++)
			 *         <field>[i] = <read>;
			 * }
			 */
			sb.append('{')
				.append("int size = ").append(makeArraySizeIndicator(field)).append(";\n")
				.append("if (")
					.append(CodeFragments.ACCESS_INSTANCE.of(f)).append(" == null || ")
					.append(CodeFragments.ACCESS_INSTANCE.ofArrayLength(f)).append(" != size)\n")
				.append("\t").append(CodeFragments.ACCESS_INSTANCE.of(f)).append(" = new ").append(raw_type).append("[size];\n")
				.append("for (int i = 0; i < size; i++)\n")
				.append("\t")
					.append(CodeFragments.ACCESS_INSTANCE.ofElement(f, "i"))
					.append(" = ")
					.append(CodeFragments.READER.invoke("read" + store_type))
					.append(';')
			.append('}');
			return sb.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) throws CannotCompileException {
			final String f = field.getName();
			final StringBuilder sb = new StringBuilder();
			/*
			 * {
			 *     for (int i = 0; i < <field length>; <write>);
			 * }
			 */
			sb.append('{')
				.append("for (int i = 0; i < ").append(CodeFragments.ACCESS_INSTANCE.ofArrayLength(f)).append("; i++)")
					.append(CodeFragments.WRITER.invoke("write" + store_type, CodeFragments.ACCESS_INSTANCE.ofElement(f, "i"))).append(';')
				.append('}');
			return sb.toString();
		}
		
	}
	
}
