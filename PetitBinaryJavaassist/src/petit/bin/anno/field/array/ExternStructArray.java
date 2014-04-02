package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.CodeFragmentsSynonym;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStructArray {
	
	/**
	 * Specifies a component type resolver method which is used to resolve a concrete type of this vVarField's component type.
	 * 
	 * @return name of the component type resolver method
	 */
	public abstract String value();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) throws CannotCompileException {
			/*
			 * {
			 *     int size = <ind>;
			 *     if (<vVarField> == null || <vVarField>.length != size)
			 *         <vVarField> = new <vVarField's type>[size];
			 *     
			 *     <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField's class>);
			 *     for (int i = 0; i < <vVarField>.length; i++)
			 *         if ExternStruct.value() is not present:
			 *             {
			 *                 <vVarField>[i] = sa.read(<reader>);
			 *             }
			 *         else
			 *             {
			 *                 <vVarField>[i] = <method which is indicated by ExternStruct.value()>();
			 *                 sa.read(<vVarField>[i], <reader>);
			 *             }
			 *         
			 * }
			 */
			try {
				final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
				final StringBuilder sb = new StringBuilder();
				sb.append("{")
					.append("int size = ").append(makeArraySizeIndicator(field)).append(";\n")
					.append("if (").append(syno.field).append(" == null || ").append(syno.fieldLen).append(" != size)\n")
							.append("\t").append(syno.field).append(" = new ").append(syno.fieldComponentType).append("[size];\n")
					.append(syno.assignComponentTypeSerializeAdapter).append("\n")
					.append("for (int i = 0; i < size; i++)\n");
				
				final ExternStructArray esaa = (ExternStructArray) field.getAnnotation(ExternStructArray.class);
				if (esaa != null && esaa.value() != null && !esaa.value().isEmpty()) {
					// ExternStructArray.value() is present
					sb.append("\t{")
						.append(syno.fieldElm)
							.append(" = (").append(syno.fieldComponentType).append(") ").append(CodeFragments.ACCESS_INSTANCE.invoke(esaa.value())).append(";")
						.append(CodeFragments.SERIALIZER.invoke("read", syno.fieldElm, CodeFragments.READER.ID)).append(";")
						.append("}\n");
				} else {
					// ExternStructArray.value() is NOT present
					sb.append("\t")
						.append(syno.fieldElm)
							.append(" = (").append(syno.fieldComponentType).append(") ").append(CodeFragments.SERIALIZER.invoke("read", CodeFragments.READER.ID))
						.append(";\n");
				}
				
				sb.append("}");
				return sb.toString();
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtField field) throws CannotCompileException {
			/*
			 * if (<vVarField> != null) {
			 *     <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField's class>);
			 *     for (int i = 0; i < <vVarField's length>; i++)
			 *        sa.write(<vVarField[i]>, <dst>);
			 * }
			 */
			final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
			return new StringBuilder()
					.append("if (").append(syno.field).append(" != null) {")
						.append(syno.assignComponentTypeSerializeAdapter)
						.append("for (int i = 0; i < ").append(syno.fieldLen).append("; i++)")
							.append(CodeFragments.SERIALIZER.invoke("write", syno.fieldElm, CodeFragments.WRITER.ID)).append(";")
					.append("}")
					.toString();
		}
		
	}
	
}
