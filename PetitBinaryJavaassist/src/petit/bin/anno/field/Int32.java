package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * 32ビット符号付整数型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     int, long
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     int
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(int.class)
@SupportType({
	int.class,
	long.class})
public @interface Int32 {
	
	public static final class _MA extends PrimitiveTypeMetaAgent {
		
		public _MA() {
			super("Int32", "int", null);
		}
		
	}
	
}
