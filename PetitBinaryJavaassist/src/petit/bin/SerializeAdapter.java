package petit.bin;

import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;

/**
 * 直列化された構造体へのアクセスを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 * 
 * @param <T> 対象の型
 */
public interface SerializeAdapter<T> {
	
	/**
	 * 構造体を表す対象のクラスを得る
	 * 
	 * @return 対象のクラス
	 */
	public abstract Class<T> getTargetClass();
	
	/**
	 * 新たに {@link #getTargetClass()} のインスタンスを生成し，そのインスタンスに対して {@link #read(Object, ReadableStore)} した結果を得る
	 * 
	 * @param src 入力元
	 * @return 入力元から読み取られた新たなインスタンス
	 * @throws Exception
	 */
	public abstract T read(final ReadableStore src) throws Exception;
	
	/**
	 * 対象のインスタンスに対して入力元から読み取り，フィールドを設定する
	 * 
	 * @param ao 対象のインスタンス(非null)
	 * @param src 入力元
	 * @return 対象のインスタンスそのもの
	 * @throws Exception
	 */
	public abstract T read(final T ao, final ReadableStore src) throws Exception;
	
	/**
	 * 対象のインスタンスを出力先へ出力する
	 * 
	 * @param ao 対象のインスタンス(非null)
	 * @param dst 出力先
	 * @throws Exception
	 */
	public abstract void write(final T ao, final WritableStore dst) throws Exception;
}
