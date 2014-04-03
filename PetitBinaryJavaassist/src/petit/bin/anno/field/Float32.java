package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * IEEE 754 floating-point binary32，float型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     float, double
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     float
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(float.class)
@SupportType({
	float.class,
	double.class})
public @interface Float32 {
	
	public static final class _MA extends PrimitiveTypeMetaAgent {
		
		public _MA() {
			super("Float", "float", null);
		}
		
	}
	
}
