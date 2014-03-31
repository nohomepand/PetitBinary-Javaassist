package petit.bin.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * Unsafe を使ってインスタンスを生成するもの
 * 
 * @author 俺用
 * @since 2014/03/17 PetitBinarySerialization
 *
 */
final class UnsafeInstantiator extends Instantiator {
	
	private static Object UNSAFE;
	
	private static Method _allocateInstance;
	
	static {
		UNSAFE = null;
		try {
			final Class<?> unsafe_clazz = Class.forName("sun.misc.Unsafe");
			final Field theUnsafe = unsafe_clazz.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			UNSAFE = theUnsafe.get(null);
			
			_allocateInstance = unsafe_clazz.getMethod("allocateInstance", Class.class);
		} catch (Exception e) {}
	}
	
	/**
	 * {@link UnsafeInstantiator} が利用可能かどうか
	 * 
	 * @return {@link UnsafeInstantiator} が利用可能な場合は true
	 */
	public static final boolean isAvailable() {
		return UNSAFE != null;
	}
	
	/**
	 * 初期化
	 * 
	 * @param clazz クラス
	 * @throws IllegalStateException UnsafeInstantiator が利用可能出ない場合
	 */
	public UnsafeInstantiator(final Class<?> clazz) throws IllegalStateException {
		super(clazz);
		if (!isAvailable())
			throw new IllegalStateException();
	}
	
	@Override
	public Object newInstance() throws Exception {
		if (UNSAFE == null)
			throw new Exception("Cannot load UNSAFE object");
		else
			return _allocateInstance.invoke(UNSAFE, _clazz);
	}
	
}
