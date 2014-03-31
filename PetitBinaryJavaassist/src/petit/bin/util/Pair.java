package petit.bin.util;

/**
 * ペアを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 * 
 * @param <T1> 第一要素の型
 * @param <T2> 第二要素の型
 */
public final class Pair<T1, T2> {
	/**
	 * 第一要素
	 */
	public T1 FIRST;
	
	/**
	 * 第二要素
	 */
	public T2 SECOND;
	
	/**
	 * 初期化
	 * 
	 * @param first 第一要素
	 * @param second 第二要素
	 */
	public Pair(final T1 first, final T2 second) {
		FIRST = first;
		SECOND = second;
	}
}