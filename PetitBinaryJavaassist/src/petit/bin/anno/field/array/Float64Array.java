package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.MetaAgentFactory;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(double[].class)
@SupportType(double[].class)
public @interface Float64Array {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		private static final String store_type = "Double";
		
		@Override
		public String makeReaderSource(CtField field) throws CannotCompileException {
			final MetaAgentFactory.CodeFragmentsSynonym syno = new MetaAgentFactory.CodeFragmentsSynonym(field);
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
				.append("if (").append(syno.field).append(" == null || ").append(syno.fieldLen).append(" != size)\n")
						.append("\t").append(syno.field).append(" = new ").append(syno.fieldComponentType).append("[size];\n")
				.append("for (int i = 0; i < size; i++)\n")
				.append("\t")
					.append(syno.fieldElm).append(" = ").append(CodeFragments.READER.invoke("read" + store_type)).append(';')
			.append('}');
			return sb.toString();
		}
		
		@Override
		public String makeWriterSource(CtField field) throws CannotCompileException {
			final MetaAgentFactory.CodeFragmentsSynonym syno = new MetaAgentFactory.CodeFragmentsSynonym(field);
			final StringBuilder sb = new StringBuilder();
			/*
			 * {
			 *     if (<field> == null) return;
			 *     for (int i = 0; i < <field length>; i++) <write>;
			 * }
			 */
			sb.append('{')
				.append("if (").append(syno.field).append(" == null) return;")
				.append("for (int i = 0; i < ").append(syno.fieldLen).append("; i++)")
					.append(CodeFragments.WRITER.invoke("write" + store_type, syno.fieldElm)).append(';')
				.append('}');
			return sb.toString();
		}
		
	}
	
}
