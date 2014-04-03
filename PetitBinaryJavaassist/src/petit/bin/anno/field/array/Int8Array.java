package petit.bin.anno.field.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.anno.MemberDefaultType;
import petit.bin.anno.SupportType;

/**
 * コンポーネント型が byte型の配列型を表す
 * 
 * <pre>
 * 対応するフィールドの型:
 *     byte[]
 * 次のフィールドの型の場合に自動的にこのアノテーションが指示される:
 *     byte[]
 * </pre>
 * 
 * @author 俺用
 * @since 2014/04/03 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@MemberDefaultType(byte[].class)
@SupportType(byte[].class)
public @interface Int8Array {
	
	public static final class _MA extends PrimitiveArrayTypeMetaAgent {
		
		public _MA() {
			super("Int8");
		}
		
	}
	
}
