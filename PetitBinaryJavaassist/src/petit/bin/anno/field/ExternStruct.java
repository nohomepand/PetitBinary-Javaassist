package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.CtMethod;
import petit.bin.CodeGenerator;
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
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
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
				final ExternStruct esa = (ExternStruct) field.getAnnotation(ExternStruct.class);
				if (esa != null && esa.value() != null && !esa.value().isEmpty()) {
					cg.map("esa", esa.value());
					// ExternStruct.value() is present
					final CtMethod return_type_class_method = getCtMethod(field.getDeclaringClass(), esa.value(), Class.class);
					if (return_type_class_method != null) {
						// esa.value() points to a (?)Ljava/lang/Class; method
						/*
							{
								Class ac = <method which is indicated by ExternStruct.value()>();
								<SerializeAdapter> sa = <PetitSerializer>.getSerializer(ac);
								<vVarField> = sa.read(<reader>);
							}
						 */
						return cg.replaceAll(
								"{" +
								"	Class c = $varTarget$.$esa$();" +
								"	if (c != null) {" +
								"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer(c);" +
								"		$varField$ = ($typeField$) sa.read($varReader$);" +
								"	}" +
								"}");
					} else {
						/*
						 *     {
						 *         <vVarField> = <method which is indicated by ExternStruct.value()>();
						 *         <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField.getClass()>);
						 *         sa.read(<vVarField>, <reader>);
						 *     }
						 */
						return cg.replaceAll(
								"{" +
								"	$varField$ = ($typeField$) $varTarget$.$esa$();" +
								"	if ($varField$ != null) {" +
								"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$.getClass());" +
								"		sa.read($varField$, $varReader$);" +
								"	}" +
								"}");
					}
				} else {
					// ExternStruct.value() is NOT present
					/*
					 *     {
					 *         <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField's class>);
					 *         <vVarField> = sa.read(<reader>);
					 *     }
					 */
					return cg.replaceAll(
							"{" +
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeField$.class);" +
							"	$varField$ = ($typeField$) sa.read($varReader$);" +
							"}");
				}
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			/*
			 * if (<vVarField> != null) {
			 *     <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField>.getClass());
			 *     sa.write(<vVarField>, <reader>);
			 * }
			 */
			return cg.replaceAll(
					"{" +
					"	if ($varField$ != null) {" +
					"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$.getClass());" +
					"		sa.write($varField$, $varWriter$);" +
					"	}" +
					"}");
		}
		
	}
	
}
