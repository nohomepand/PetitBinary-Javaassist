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
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;

/**
 * コンポーネント型が byte, short, char, int, long, float, double型以外の配列型を表す<br />
 * <br />
 * {@link #value()} および {@link #useIndex()} はフィールドの読み込み時に，コンポーネント型，もしくはコンポーネント型のインスタンスを解決するメソッド名を指定することができる<br />
 * メソッド名を指定した場合，対応するメソッドのシグネチャは次の 4種類の内いずれかである
 * <pre>
 * {@link #value()} の指定         | {@link #useIndex()} の指定         | メソッドのシグネチャ  | メソッドの説明
 * あり                    | false                      | ()Ljava/lang/Class;   | 戻り値: 配列のコンポーネント型の具象型を表す
 * あり                    | false                      | ()Ljava/lang/Object;  | 戻り値: 配列の要素の具象型を表す
 * あり                    | true                       | (I)Ljava/lang/Class;  | 戻り値: 配列のコンポーネント型の具象型を表す 引数: 現在の読み込まれている配列要素のインデックス
 * あり                    | true                       | (I)Ljava/lang/Object; | 戻り値: 配列の要素の具象型を表す 引数: 現在の読み込まれている配列要素のインデックス
 * </pre>
 * 
 * <pre>
 * 対応するフィールドの型:
 *     コンポーネント型が byte, short, char, int, long, float, double型以外の配列型
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     コンポーネント型が byte, short, char, int, long, float, double型以外の配列型
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStructArray {
	
	/**
	 * コンポーネント型，もしくはコンポーネント型のインスタンスを解決するメソッド名を指定する
	 * 
	 * @return コンポーネント型，もしくはコンポーネント型のインスタンスを解決するメソッド名
	 * @see ExternStructArray
	 */
	public abstract String value();
	
	/**
	 * {@link #value()} で指定されるメソッドが，引数として配列要素のインデックスを受け付けるかどうかを指定する
	 * 
	 * @return メソッドが引数として配列要素のインデックスを受け付ける場合は true(デフォルト値: false)
	 */
	public abstract boolean useIndex() default false;
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public void checkField(CtField field) throws CannotCompileException {
			if (	field.hasAnnotation(ArraySizeConstant.class) ||
					field.hasAnnotation(ArraySizeByField.class) ||
					field.hasAnnotation(ArraySizeByMethod.class)) {
				// FIXME enum の配列とか作ったらここも変える
//				if (Enum.class.isAssignableFrom(Util.toClass(field.getDeclaringClass()).FIRST))
//					throw new CannotCompileException("Enum type is not supported");
				return;
			} else
				throw new CannotCompileException("No array size annotation is defined");
		}
		
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final ExternStructArray esaa = (ExternStructArray) field.getAnnotation(ExternStructArray.class);
				if (esaa != null && esaa.value() != null && !esaa.value().isEmpty()) {
					// ExternStructArray.value() is present
					final CtMethod return_type_class_method = getCtMethod(field.getDeclaringClass(), esaa.value(), Class.class);
					cg.map("esaa_invoke", esaa.value() + (esaa.useIndex() ? "(i)" : "()"));
					if (return_type_class_method != null) {
						// esa.value() points to OpenflowVersion (?)Ljava/lang/Class; method
						return cg.replaceAll(
								"{\n" +
								"	int size = $exprFieldSizeGetter$;\n" +
								"	if ($varField$ == null || $varField$.length != size)\n" +
								"		$varField$ = new $typeFieldComponent$[size];\n" +
								
								"	if (c != null) {\n" +
								"		for (int i = 0; i < size; i++) {\n" +
								"			Class c = $varTarget$.$esaa_invoke$;\n" +
								"			$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer(c);\n" +
								"			$varField$[i] = ($typeFieldComponent$) sa.read($varReader$);\n" +
								"		}\n" +
								"	}\n" +
								"}");
					} else {
						return cg.replaceAll(
								"{\n" +
								"	int size = $exprFieldSizeGetter$;\n" +
								"	if ($varField$ == null || $varField$.length != size)\n" +
								"		$varField$ = new $typeFieldComponent$[size];\n" +
								
								"	for (int i = 0; i < size; i++) {\n" +
								"		$varField$[i] = ($typeFieldComponent$) $varTarget$.$esaa_invoke$;\n" +
								"		if ($varField$[i] != null) {\n" +
								"			$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($varField$[i].getClass());\n" +
								"			sa.read($varField$, $varReader$);\n" +
								"		}\n" +
								"	}\n" +
								"}");
					}
				} else {
					// ExternStructArray.value() is NOT present
					return cg.replaceAll(
							"{\n" +
							"	int size = $exprFieldSizeGetter$;\n" +
							"	if ($varField$ == null || $varField$.length != size)\n" +
							"		$varField$ = new $typeFieldComponent$[size];\n" +
							
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeFieldComponent$.class);\n" +
							"	for (int i = 0; i < size; i++)\n" +
							"		$varField$[i] = sa.read($varReader$);\n" +
							"}");
				}
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				if (field.hasAnnotation(ExternStructArray.class)) { 
					// field は内部で field's type とは異なる型のインスタンスを持っているかもしれない
					return cg.replaceAll(
							"if ($varField$ != null) {\n" +
							"	$typeSerAdap$ sa;\n" +
							"	for (int i = 0; i < $varField$.length; i++) {\n" +
							"		if ($varField$[i] != null) {\n" +
							"			sa = $typeSerAdapFactory$.getSerializer($varField$[i].getClass());\n" +
							"			sa.write($varField$[i], $varWriter$);\n" +
							"		}\n" +
							"	}\n" +
							"}");
				} else {
					// field の内部は全て field's type と同一
					return cg.replaceAll(
							"if ($varField$ != null) {\n" +
							"	$typeSerAdap$ sa = $typeSerAdapFactory$.getSerializer($typeFieldComponent$.class);\n" +
							"	for (int i = 0; i < $varField$.length; i++)\n" +
							"		if ($varField$[i] != null)\n" +
							"			sa.write($varField$[i], $varWriter$);\n" +
							"}");
				}
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
	}
	
}
