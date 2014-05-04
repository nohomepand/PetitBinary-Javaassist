package petit.bin.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import petit.bin.store.Store.SerializationByteOrder;

/**
 * 構造体を示すシリアライズクラスであることを指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Struct {
	
	/**
	 * バイトオーダー
	 * 
	 * @return バイトオーダー
	 */
	public abstract SerializationByteOrder byteOrder() default SerializationByteOrder.NEUTRAL;
	
//	/**
//	 * 読み込み後の検証機を表すメソッド名
//	 * 
//	 * @return 読み込み後の検証機を表すメソッド名
//	 */
//	public abstract String readValidator() default "";
//	
//	/**
//	 * 書き込み前の検証機を表すメソッド名
//	 * 
//	 * @return 書き込み前の検証機を表すメソッド名
//	 */
//	public abstract String writeValidator() default "";
	
}
