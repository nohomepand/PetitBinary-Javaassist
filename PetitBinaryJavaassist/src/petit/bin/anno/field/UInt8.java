package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.SupportType;

/**
 * 8ビット符号なし整数型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     short, char, int, long
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     なし
 * </pre>
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SupportType({
	short.class,
	char.class,
	int.class,
	long.class,})
public @interface UInt8 {
	
	public static final class _MA extends PrimitiveTypeMetaAgent {
		
		public _MA() {
			super("Int8", "byte", "0xff");
		}
		
	}
	
}
