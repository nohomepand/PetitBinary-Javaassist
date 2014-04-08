package petit.bin.util;

import java.util.LinkedList;

/**
 * {@link Object}を保存しておくもの<br />
 * あえて raw型
 * 
 * @author 俺用
 * @since 2014/04/08 PetitBinaryJavaassist
 *
 */
public class ObjectStocker {
	
	/**
	 * {@link ObjectStocker} に保存されているオブジェクト
	 * 
	 * @author 俺用
	 * @since 2014/04/08 PetitBinaryJavaassist
	 *
	 */
	public final class StockObject {
		
		private boolean _is_released;
		
		private final Object _obj;
		
		/**
		 * 初期化
		 * 
		 * @param object オブジェクト
		 */
		private StockObject(final Object object) {
			_obj = object;
			_is_released = false;
		}
		
		/**
		 * 保存されたオブジェクトを得る<br />
		 * {@link #release()} されている場合，このメソッドは {@link IllegalStateException} 例外を発生させる
		 * 
		 * @return 保存されたオブジェクト
		 * @throws IllegalStateException 
		 */
		public final Object get() throws IllegalStateException {
			if (_is_released)
				throw new IllegalStateException("This object " + _obj + " is already released");
			
			return _obj;
		}
		
		/**
		 * このオブジェクトを親の {@link ObjectStocker} へ保存しなおす
		 */
		public final void release() {
			ObjectStocker.this.release(this);
			_is_released = true;
		}
		
		/**
		 * このオブジェクトが親の {@link ObjectStocker} へ保存されているか検証する
		 * 
		 * @return このオブジェクトが親の {@link ObjectStocker} へ保存されている場合は true
		 */
		public final boolean isReleased() {
			return _is_released;
		}
		
	}
	
	private final LinkedList<StockObject> _store;
	
	/**
	 * 初期化
	 */
	public ObjectStocker() {
		_store = new LinkedList<>();
	}
	
	/**
	 * 新たにオブジェクトを保存する
	 * 
	 * @param obj 保存されるオブジェクト
	 */
	public final void stock(final Object obj) {
		_store.addLast(new StockObject(obj));
	}
	
	private final void release(final StockObject sobj) {
		_store.add(sobj);
	}
	
	/**
	 * オブジェクトが一つでも保存されているか検証する
	 * 
	 * @return オブジェクトが一つでも保存されている場合は true
	 */
	public final boolean hasStock() {
		return !_store.isEmpty();
	}
	
	/**
	 * 保存されているオブジェクトを一つ取り出す
	 * 
	 * @return 保存されているオブジェクト，または何も保存されていない場合は null
	 */
	public final StockObject take() {
		if (_store.isEmpty())
			return null;
		else {
			final StockObject so = _store.removeFirst();
			so._is_released = false;
			return so;
		}
	}
	
}
