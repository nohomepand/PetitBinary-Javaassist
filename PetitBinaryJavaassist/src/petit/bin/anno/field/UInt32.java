package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.SupportType;

/**
 * 32ビット符号なし整数型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     long
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     なし
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@SupportType({
	long.class})
public @interface UInt32 {
	
	public static final class _MA extends PrimitiveTypeMetaAgent {
		
		public _MA() {
			super("Int32", "long", "0xffffffffL");
		}
		
	}
	
}
