package petit.bin;

import petit.bin.anno.Struct;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;
import petit.bin.util.Util;
import petit.bin.util.instor.Instantiator;

/**
 * 直列化された構造体へのアクセスを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 * 
 * @param <T> 対象のクラス
 */
public abstract class SerializeAdapter<T> {
	
//	フィールドを持たないクラスであっても Read/WriteValidator が動作するためにコードを作る必要があるため，これは廃止
//	/**
//	 * フィールドを持たないクラスに対する {@link SerializeAdapter}
//	 * 
//	 * @author 俺用
//	 * @since 2014/04/06 PetitBinaryJavaassist
//	 * 
//	 * @param <T> 対象のクラス
//	 */
//	static final class NullFieldSerializeAdapter<T> extends SerializeAdapter<T> {
//		
//		/**
//		 * 初期化
//		 * 
//		 * @param clazz 対象のクラス
//		 */
//		public NullFieldSerializeAdapter(final Class<T> clazz) {
//			super(clazz);
//		}
//		
//		@Override
//		public T read(T ao, ReadableStore src) throws Exception {
//			return ao;
//		}
//		
//		@Override
//		public void write(T ao, WritableStore dst) throws Exception {
//			return;
//		}
//		
//	}
	
	/**
	 * 構造体を表す対象のクラス
	 */
	protected final Class<T> _clazz;
	
	/**
	 * {@link #_clazz} を生成するもの
	 */
	protected final Instantiator _instor;
	
	/**
	 * {@link #_clazz} の {@link Struct} アノテーション
	 */
	protected final Struct _anno;
	
	/**
	 * 初期化
	 * 
	 * @param clazz 対象のクラス
	 */
	public SerializeAdapter(final Class<T> clazz) {
		if (clazz == null)
			throw new NullPointerException("Argument clazz must not be null");
		
		_clazz = clazz;
		_instor = Util.getInstantiator(clazz);
		_anno = clazz.getAnnotation(Struct.class);
		if (_anno == null)
			throw new NullPointerException(clazz + " does not have " + Struct.class.getCanonicalName() + " annotation");
		
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
