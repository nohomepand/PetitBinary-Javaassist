package petit.bin.util;

import javassist.ClassPool;

/**
 * デフォルトのクラスプールを持つもの
 * 
 * @author 俺用
 * @since 2014/03/31 PetitBinaryJavaassist
 *
 */
public final class DefaultClassPool {
	
	/**
	 * この環境のデフォルトのクラスプール
	 */
	public static final ClassPool CP = ClassPool.getDefault();
	
}
