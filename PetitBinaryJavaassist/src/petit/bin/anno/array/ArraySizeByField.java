package petit.bin.anno.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * [byte, short, int, or long] なフィールドでサイズが指定された配列であることを指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 * @see ArraySizeByFieldIndicator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArraySizeByField {
	
	/**
	 * フィールド名
	 * 
	 * @return フィールド名
	 */
	public abstract String value();
	
}
