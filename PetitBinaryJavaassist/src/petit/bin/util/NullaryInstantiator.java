package petit.bin.util;

import java.lang.reflect.Constructor;


/**
 * nullary なコンストラクタを呼んでインスタンスを生成するもの
 * 
 * @author 俺用
 * @since 2014/03/17 PetitBinarySerialization
 *
 */
final class NullaryInstantiator extends Instantiator {
	
	private final Constructor<?> _ctor;
	
	/**
	 * 初期化
	 * 
	 * @param clazz クラス
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	public NullaryInstantiator(final Class<?> clazz) throws SecurityException, NoSuchMethodException {
		super(clazz);
		_ctor = clazz.getConstructor();
		_ctor.setAccessible(true);
	}
	
	@Override
	public Object newInstance() throws Exception {
		return _ctor.newInstance();
	}
	
}
