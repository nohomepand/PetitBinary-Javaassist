package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * コンポーネント型が double型の配列型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     double
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(double[].class)
@SupportType(double[].class)
public @interface Float64Array {
	
	public static final class _MA extends PrimitiveArrayTypeMetaAgent {
		
		public _MA() {
			super("Double");
		}
		
	}
	
}
