package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;

/**
 * コンポーネント型が char型の配列型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     char[]
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     char[]
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(char[].class)
@SupportType(char[].class)
public @interface UInt16Array {
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public void checkField(CtField field) throws CannotCompileException {
			if (	field.hasAnnotation(ArraySizeConstant.class) ||
					field.hasAnnotation(ArraySizeByField.class) ||
					field.hasAnnotation(ArraySizeByMethod.class)) {
				super.checkField(field);
			} else
				throw new CannotCompileException("No array size annotation is defined");
		}
		
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll(
					"{\n" +
					"	int size = $exprFieldSizeGetter$;\n" +
					"	if ($varField$ == null || $varField$.length != size)\n" +
					"		$varField$ = new $typeFieldComponent$[size];\n" +
					"	for (int i = 0; i < $varField$.length; i++)\n" +
					"		$varField$[i] = (char) ($varReader$.readInt16() & 0xffff);\n" +
					"}");
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			return cg.replaceAll(
					"if ($varField$ != null) {\n" +
					"	for (int i = 0; i < $varField$.length; i++)\n" +
					"		$varWriter$.writeInt16((short) $varField$[i]);\n" +
					"}");
		}
		
	}
	
}
