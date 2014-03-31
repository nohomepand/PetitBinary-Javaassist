package petit.bin.anno;

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
public @interface Struct {
	
	/**
	 * アライメントのByte Packingのサイズ
	 * 
	 * @return 構造体のアライメント
	 */
	@Deprecated
	public abstract int packSize() default 1;
	
	/**
	 * バイトオーダー
	 * 
	 * @return バイトオーダー
	 */
	public abstract SerializationByteOrder byteOrder() default SerializationByteOrder.NEUTRAL;
	
}
