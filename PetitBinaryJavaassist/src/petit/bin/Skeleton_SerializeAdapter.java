package petit.bin;

import petit.bin.anno.Struct;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;
import petit.bin.util.ReflectionUtil;
import petit.bin.util.instor.Instantiator;

/**
 * 直列化された構造体へのアクセスを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 * 
 * @param <T> 対象の型
 */
public abstract class Skeleton_SerializeAdapter<T> {
	
	private final Class<T> _clazz;
	
	private final Instantiator _instor;

	private final Struct _anno;
	
	/**
	 * 初期化
	 * 
	 * @param clazz 対象のクラス
	 */
	protected Skeleton_SerializeAdapter(final Class<T> clazz) {
		_clazz = clazz;
		_instor = ReflectionUtil.getInstantiator(clazz);
		_anno = _clazz.getAnnotation(Struct.class);
		
		if (_anno == null)
			throw new IllegalArgumentException(Struct.class.getSimpleName() + " annotation is not present");
	}
	
	/**
	 * 構造体を表す対象のクラスを得る
	 * 
	 * @return 対象のクラス
	 */
	public final Class<T> getTargetClass() {
		return _clazz;
	}
	
	/**
	 * 新たに {@link #getTargetClass()} のインスタンスを生成し，そのインスタンスに対して {@link #read(Object, ReadableStore)} した結果を得る
	 * 
	 * @param src 入力元
	 * @return 入力元から読み取られた新たなインスタンス
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public final T read(final ReadableStore src) throws Exception {
		return read((T) _instor.newInstance(), src);
	}
	
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
