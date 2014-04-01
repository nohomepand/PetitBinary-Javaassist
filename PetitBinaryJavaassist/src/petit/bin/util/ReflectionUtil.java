package petit.bin.util;

import petit.bin.util.instor.Instantiator;
import petit.bin.util.instor.NullaryInstantiator;
import petit.bin.util.instor.UnsafeInstantiator;

/**
 * リフレクションのユーティリティ
 * 
 * @author 俺用
 * @since 2014/03/19 PetitBinarySerialization
 *
 */
public final class ReflectionUtil {
	
	/**
	 * clazz のインスタンスを構築可能な方法を得る
	 * 
	 * @param clazz クラス
	 * @return clazz のインスタンスを構築可能な方法，または構築可能な方法がなければ null
	 */
	public static final Instantiator getInstantiator(final Class<?> clazz) {
		try {
			return new NullaryInstantiator(clazz);
		} catch (Exception e) {
			if (UnsafeInstantiator.isAvailable())
				return new UnsafeInstantiator(clazz);
			else
				return null;
		}
	}
	
}
