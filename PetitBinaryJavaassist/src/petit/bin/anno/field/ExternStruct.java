package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.CtMethod;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.CodeFragmentsSynonym;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	/**
	 * Specify a method which is used for resolving concrete class instance of the vVarField.<br />
	 * If the value is null the concrete class is treated as the vVarField's type.<br />
	 * The method must be defined as the following signature.
	 * <pre>
	 * [Object which extends this vVarField's type] [method name]({@link Object}, {@link Field})
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
			 *         <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField's class>);
			 *         <vVarField> = sa.read(<reader>);
			 *     }
			 * else
			 *     if the method which is indicated by ExternStrcut.value() does NOT return Class
			 *     {
			 *         <vVarField> = <method which is indicated by ExternStruct.value()>();
			 *         <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField.getClass()>);
			 *         sa.read(<vVarField>, <reader>);
			 *     }
			 *     else
			 *     {
			 *         Class ac = <method which is indicated by ExternStruct.value()>();
			 *         <SerializeAdapter> sa = <PetitSerializer>.getSerializer(ac);
			 *         <vVarField> = sa.read(<reader>);
			 *     }
			 */
			
			try {
				final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
				final StringBuilder sb = new StringBuilder();
				sb
					.append("{");
				
				final ExternStruct esa = (ExternStruct) field.getAnnotation(ExternStruct.class);
				if (esa != null && esa.value() != null && !esa.value().isEmpty()) {
					// ExternStruct.value() is present
					sb.append(CodeFragments.TEMPORARY_CLASS.defineVar("Class", null));
					final CtMethod return_type_class_method = getCtMethod(field.getDeclaringClass(), esa.value(), Class.class);
					if (return_type_class_method != null) {
						// esa.value() points to a (?)Ljava/lang/Class; method
						sb.append(CodeFragments.TEMPORARY_CLASS.ID).append(" = ").append(CodeFragments.ACCESS_INSTANCE.invoke(esa.value())).append(";\n");
					} else {
						// esa.value() points to (?)L?; method
						sb.append(syno.field).append(" = (").append(syno.fieldType).append(") ").append(CodeFragments.ACCESS_INSTANCE.invoke(esa.value())).append(";\n");
						sb.append(CodeFragments.TEMPORARY_CLASS.ID).append(" = ").append(syno.fieldObjectType).append(";\n");
					}
					sb
						.append(syno.assignTemporaryClassSerializer).append(";\n")
						.append(CodeFragments.SERIALIZER.invoke("read", syno.field, CodeFragments.READER.ID)).append(";");
				} else {
					// ExternStruct.value() is NOT present
					sb
						.append(syno.assignFieldTypeSerializeAdapter).append("\n")
						.append(syno.field)
							.append(" = (").append(syno.fieldType).append(") ").append(CodeFragments.SERIALIZER.invoke("read", CodeFragments.READER.ID)).append(";");
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
			 *     <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField>.getClass());
			 *     sa.write(<vVarField>, <reader>);
			 * }
			 */
			final CodeFragmentsSynonym syno = new CodeFragmentsSynonym(field);
			return new StringBuilder()
					.append("if (").append(syno.field).append(" != null) {")
//						.append(syno.assignFieldTypeSerializeAdapter) vVarField typeではなくて実際の値の型
						.append(syno.ass)
						.append(syno.)
						.append(CodeFragments.SERIALIZE_ADAPTER.invoke("write", syno.field, CodeFragments.WRITER.ID)).append(";")
					.append("}")
					.toString();
		}
		
	}
	
}
