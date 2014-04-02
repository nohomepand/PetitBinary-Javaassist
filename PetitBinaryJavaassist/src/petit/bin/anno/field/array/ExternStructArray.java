package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.CtMethod;
import petit.bin.CodeGenerator;
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
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
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
				final ExternStructArray esaa = (ExternStructArray) field.getAnnotation(ExternStructArray.class);
				if (esaa != null && esaa.value() != null && !esaa.value().isEmpty()) {
					// ExternStructArray.value() is present
					final CtMethod return_type_class_method = getCtMethod(field.getDeclaringClass(), esaa.value(), Class.class);
					cg.map("esaa", esaa.value());
					if (return_type_class_method != null) {
						// esa.value() points to a (?)Ljava/lang/Class; method
						return cg.replaceAll(
								"{" +
								"	int size = $exprFieldSizeGetter$;" +
								"	if ($varField$ == null || $varField$.length != size)" +
								"		$varField$ = new $typeFieldComponent$[size];" +
								
								"	Class c = $varTarget$.$esaa$();" +
								"	if (c != null) {" +
								"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer(c);" +
								"		for (int i = 0; i < size; i++)" +
								"			$varField$[i] = ($typeFieldComponent$) sa.read($varReader$);" +
								"	}" +
								"}");
					} else {
						return cg.replaceAll(
								"{" +
								"	int size = $exprFieldSizeGetter$;" +
								"	if ($varField$ == null || $varField$.length != size)" +
								"		$varField$ = new $typeFieldComponent$[size];" +
								
								"	for (int i = 0; i < size; i++) {" +
								"		$varField$[i] = ($typeFieldComponent$) $varTarget$.$esaa$();" +
								"		if ($varField$[i] != null) {" +
								"			$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$[i].getClass());" +
								"			sa.read($varField$, $varReader$);" +
								"		}" +
								"	}" +
								"}");
					}
				} else {
					// ExternStructArray.value() is NOT present
					return cg.replaceAll(
							"{" +
							"	int size = $exprFieldSizeGetter$;" +
							"	if ($varField$ == null || $varField$.length != size)" +
							"		$varField$ = new $typeFieldComponent$[size];" +
							
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeFieldComponent$.class);" +
							"	for (int i = 0; i < size; i++)" +
							"		$varField$[i] = sa.read($varReader$);" +
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
			 *     <SerializeAdapter> sa = <PetitSerializer>.getSerializer(<vVarField's class>);
			 *     for (int i = 0; i < <vVarField's length>; i++)
			 *        sa.write(<vVarField[i]>, <dst>);
			 * }
			 */
			try {
				if (field.hasAnnotation(ExternStructArray.class)) { 
					// field は内部で field's type とは異なる型のインスタンスを持っているかもしれない
					return cg.replaceAll(
							"if ($varField$ != null) {" +
							"	$typeSerAdap$ sa;" +
							"	for (int i = 0; i < $varField$.length; i++) {" +
							"		if ($varField$[i] != null) {" +
							"			sa = $typeSerAdapFactory$.getSerializer($varField$[i].getClass());" +
							"			sa.write($varField$[i], $varWriter$);" +
							"		}" +
							"	}" +
							"}");
				} else {
					// field の内部は全て field's type と同一
					return cg.replaceAll(
							"if ($varField$ != null) {" +
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeFieldComponent$.class);" +
							"	for (int i = 0; i < $varField$.length; i++)" +
							"		if ($varField$[i] != null)" +
							"			sa.write($varField$[i], $varWriter$);" +
							"}");
				}
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
	}
	
}
