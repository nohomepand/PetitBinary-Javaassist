package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import javassist.CtMethod;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.util.Util;

/**
 * boolean, byte, short, char, int, long, float, double型以外の型を表す<br />
 * <br />
 * {@link #value()} はフィールドの読み込み時に，フィールドの具象クラスまたはインスタンスを解決するメソッド名を指定することができる<br />
 * メソッド名を指定した場合，対応するメソッドのシグネチャは次の 2種類の内いずれかである
 * <pre>
 * {@link #value()} の指定         | メソッドのシグネチャ  | メソッドの説明
 * あり                    | ()Ljava/lang/Class;   | 戻り値: 配列のコンポーネント型を表す
 * あり                    | ()Ljava/lang/Object;  | 戻り値: 配列の要素の具象型を表す
 * </pre>
 * 
 * <pre>
 * 対応するフィールドの型:
 *     boolean, byte, short, char, int, long, float, double型以外の型
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     boolean, byte, short, char, int, long, float, double型以外の型
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	/**
	 * フィールドの読み込み時に，フィールドの具象クラスまたはインスタンスを解決するメソッド名を指定する
	 *  
	 * @return フィールドの読み込み時に，フィールドの具象クラスまたはインスタンスを解決するメソッド名
	 * @see ExternStruct
	 */
	public abstract String value();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public void checkField(CtField field) throws CannotCompileException {
			try {
				if (Enum.class.isAssignableFrom(Util.toClass(field.getDeclaringClass()).FIRST)) {
					System.err.println(field + " is a Enum, " + TypeSafeValue.class.getCanonicalName() + " annotation is better way to use.");
				}
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
			return; //なんでもOK
		}
		
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final ExternStruct esa = (ExternStruct) field.getAnnotation(ExternStruct.class);
				if (esa != null && esa.value() != null && !esa.value().isEmpty()) {
					cg.map("esa", esa.value());
					// ExternStruct.value() is present
					final CtMethod return_type_class_method = getCtMethod(field.getDeclaringClass(), esa.value(), Class.class);
					if (return_type_class_method != null) {
						// esa.value() points to OpenflowVersion (?)Ljava/lang/Class; method
						return cg.replaceAll(
								"{\n" +
								"	Class c = $varTarget$.$esa$();\n" +
								"	if (c != null) {\n" +
								"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer(c);\n" +
								"		$varField$ = ($typeField$) sa.read($varReader$);\n" +
								"	}\n" +
								"}");
					} else {
						return cg.replaceAll(
								"{\n" +
								"	$varField$ = ($typeField$) $varTarget$.$esa$();\n" +
								"	if ($varField$ != null) {\n" +
								"		$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$.getClass());\n" +
								"		sa.read($varField$, $varReader$);\n" +
								"	}\n" +
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
							"{\n" +
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeField$.class);\n" +
							"	$varField$ = ($typeField$) sa.read($varReader$);\n" +
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
					"if ($varField$ != null) {\n" +
					"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$.getClass());\n" +
					"	sa.write($varField$, $varWriter$);\n" +
					"}");
		}
		
	}
	
}
