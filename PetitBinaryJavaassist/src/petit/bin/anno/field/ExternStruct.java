package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.CodeFragmentsSynonym;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	/**
	 * Specify a method which is used for resolving concrete class instance of the field.<br />
	 * If the value is null the concrete class is treated as the field's type.<br />
	 * The method must be defined as the following signature.
	 * <pre>
	 * [Object which extends this field's type] [method name]({@link Object}, {@link Field})
	 * </pre>
	 * 
	 * @return name of concrete class resolver method
	 */
	public abstract String value();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) throws CannotCompileException {
			/*
			 * if ExternStruct.value() is not present:
			 *     {
			 *         <SerializeAdapter> sa = <SerializeAdapterFactory>.getSerializer(<field's class>);
			 *         <field> = sa.read(<reader>);
			 *     }
			 * else
			 *     {
			 *         <SerializeAdapter> sa = <SerializeAdapterFactory>.getSerializer(<field's class>);
			 *         <field> = <method which is indicated by ExternStruct.value()>();
			 *         sa.read(<field>, <reader>);
			 *     }
			 */
			
			try {
				final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
				final StringBuilder sb = new StringBuilder();
				sb
					.append("{")
					.append(syno.assignFieldTypeSerializeAdapter).append("\n");
				
				final ExternStruct esa = (ExternStruct) field.getAnnotation(ExternStruct.class);
				if (esa != null && esa.value() != null && !esa.value().isEmpty()) {
					// ExternStruct.value() is present
					sb
						.append(syno.field)
							.append(" = ").append(CodeFragments.ACCESS_INSTANCE.invoke(esa.value())).append(";\n")
						.append(CodeFragments.SERIALIZE_ADAPTER.invoke("read", syno.field, CodeFragments.READER.ID)).append(";");
				} else {
					// ExternStruct.value() is NOT present
					sb
						.append(syno.field)
							.append(" = (").append(syno.fieldType).append(") ").append(CodeFragments.SERIALIZE_ADAPTER.invoke("read", CodeFragments.READER.ID)).append(";");
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
			 * if (<field> != null) {
			 *     <SerializeAdapter> sa = <SerializeAdapterFactory>.getSerializer(<field>.getClass());
			 *     sa.write(<field>, <reader>);
			 * }
			 */
			final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
			return new StringBuilder()
					.append("if (").append(syno.field).append(" != null) {")
						.append(syno.assignFieldTypeSerializeAdapter)
						.append(CodeFragments.SERIALIZE_ADAPTER.invoke("write", syno.field, CodeFragments.WRITER.ID))
					.append("}")
					.toString();
		}
		
	}
	
}
