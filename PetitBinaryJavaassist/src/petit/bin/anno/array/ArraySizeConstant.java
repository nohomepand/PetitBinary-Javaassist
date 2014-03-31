package petit.bin.anno.array;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 固定サイズな配列であることを指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 * @see ArraySizeByMethodIndicator
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArraySizeConstant {
	
	/**
	 * 配列のサイズ
	 * 
	 * @return 配列のサイズ
	 */
	public abstract int value();
	
}
