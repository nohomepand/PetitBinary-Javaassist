package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.util.KnownCtClass;

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
				final String str_field_type_clazz = Class.forName(field.getType().getName()).getCanonicalName();
				final StringBuilder sb = new StringBuilder();
				sb
					.append("{")
					.append(KnownCtClass.ISERIALIZE_ADAPTER.CANONICALNAME).append(" ")
						.append(CodeFragments.SERIALIZE_ADAPTER.ID).append(" = ").append(CodeFragments.SERIALIZE_ADAPTER_FACTORY.invoke("getSerializer", str_field_type_clazz + ".class")).append(";\n");
				final ExternStruct esa = (ExternStruct) field.getAnnotation(ExternStruct.class);
				if (esa != null && esa.value() != null && !esa.value().isEmpty()) {
					// ExternStruct.value() is present
					sb
						.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
							.append(" = ").append(CodeFragments.ACCESS_INSTANCE.invoke(esa.value())).append(";\n")
						.append(CodeFragments.SERIALIZE_ADAPTER.invoke("read", CodeFragments.ACCESS_INSTANCE.of(field.getName()), CodeFragments.READER.ID)).append(";");
				} else {
					// ExternStruct.value() is NOT present
					sb
						.append(CodeFragments.ACCESS_INSTANCE.of(field.getName()))
							.append(" = (").append(str_field_type_clazz).append(") ").append(CodeFragments.SERIALIZE_ADAPTER.invoke("read", CodeFragments.READER.ID)).append(";");
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
			try {
				return new StringBuilder()
						.append("if (").append(CodeFragments.ACCESS_INSTANCE.of(field.getName())).append(" != null) {")
							.append(KnownCtClass.ISERIALIZE_ADAPTER.CANONICALNAME).append(" ")
								.append(CodeFragments.SERIALIZE_ADAPTER.ID).append(" = ").append(CodeFragments.SERIALIZE_ADAPTER_FACTORY.invoke("getSerializer", field.getType().toClass().getCanonicalName() + ".class")).append(";\n")
							.append(CodeFragments.SERIALIZE_ADAPTER.invoke("write", CodeFragments.ACCESS_INSTANCE.of(field.getName()), CodeFragments.WRITER.ID))
						.append("}")
						.toString();
			} catch (CannotCompileException e) {
				throw e;
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
	}
	
}
