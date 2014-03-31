package petit.bin.util;

/**
 * あるクラスのインスタンスを生成するもの
 * 
 * @author 俺用
 * @since 2014/03/17 PetitBinarySerialization
 *
 */
public abstract class Instantiator {
	
	/**
	 * 対象のクラス
	 */
	protected final Class<?> _clazz;
	
	/**
	 * 初期化
	 * 
	 * @param clazz クラス
	 */
	public Instantiator(final Class<?> clazz) {
		_clazz = clazz;
	}
	
	/**
	 * インスタンスを生成する
	 * 
	 * @return 生成されたインスタンス
	 * @throws Exception
	 */
	public abstract Object newInstance() throws Exception;
	
}
