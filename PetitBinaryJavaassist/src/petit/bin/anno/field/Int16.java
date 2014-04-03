package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * 16ビット符号付整数型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     short, int, long
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     short
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(short.class)
@SupportType({
	short.class,
	int.class,
	long.class})
public @interface Int16 {
	
	public static final class _MA extends PrimitiveTypeMetaAgent {
		
		public _MA() {
			super("Int16", "short", null);
		}
		
	}
	
}
