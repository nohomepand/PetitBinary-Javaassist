package petit.bin.store;

/**
 * ストアの基本型を表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public interface Store {
	
	/**
	 * {@link Store} のバイトオーダーを表す定数
	 * 
	 * @author 俺用
	 * @since 2014/03/30 PetitBinaryJavaassist
	 *
	 */
	public static enum SerializationByteOrder {
		/**
		 * 直前のバイトオーダーをそのまま使うことを表す
		 */
		NEUTRAL,
		
		/**
		 * ビッグエンディアンなバイトオーダーを使うことを表す
		 */
		BIG_ENDIAN,
		
		/**
		 * リトルエンディアンなバイトオーダーを使うことを表す
		 */
		LITTLE_ENDIAN;
	}
	
	// push/pop SerializationByteOrder
	/**
	 * バイトオーダーのスタックを一つ下げ，新たにバイトオーダーを対象のバイトオーダーに設定する
	 * 
	 * @param bo 対象のバイトオーダー
	 */
	public abstract void pushByteOrder(final SerializationByteOrder bo);
	
	/**
	 * バイトオーダーのスタックを一つ上げる<br />
	 * この操作はバイトオーダーのスタックが空の場合は何も作用しない
	 */
	public abstract void popByteOrder();
	
	/**
	 * 現在のバイトオーダーであるバイトオーダーのスタックの最上位の要素を得る
	 * 
	 * @return 現在のバイトオーダー
	 */
	public abstract SerializationByteOrder currentByteOrder();
	
	// push/pop member
	/**
	 * シリアライズクラスのスタックを一つ下げ，新たにシリアライズするクラスに設定する<br />
	 * このメソッドはシリアライズの動作に直接関わらないが，ある種のマークとして利用できる
	 * 
	 * @param structure 対象のシリアライズクラス
	 */
	public abstract void pushType(final Class<?> structure);
	
	/**
	 * シリアライズクラスのスタックを一つ上げる<br />
	 * このメソッドはシリアライズの動作に直接関わらないが，ある種のマークとして利用できる
	 */
	public abstract void popType();
	
	/**
	 * 現在のシリアライズクラスであるシリアライズクラスのスタックの最上位の要素を得る
	 * 
	 * @return 現在のシリアライズクラス
	 */
	public abstract Class<?> currentType();
	
	// pack
	/**
	 * シリアライズ時の要素のパッキングサイズを得る
	 * 
	 * @return シリアライズ時の要素のパッキングサイズ
	 */
	public abstract int packSize();
	
	/**
	 * シリアライズ時の要素のパッキングサイズを設定する
	 * 
	 * @param size シリアライズ時の要素のパッキングサイズ
	 */
	public abstract void packSize(final int size);
	
	/**
	 * 現在位置を得る
	 * 
	 * @return 現在位置
	 */
	public abstract int position();
	
	/**
	 * 現在位置を設定する
	 * 
	 * @param pos 位置
	 */
	public abstract void setPosition(final int pos);
	
}
