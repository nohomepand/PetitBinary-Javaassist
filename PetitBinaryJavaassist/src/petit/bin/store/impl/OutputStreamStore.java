package petit.bin.store.impl;

import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

import petit.bin.store.Store;
import petit.bin.store.WritableStore;

/**
 * {@link OutputStream} をラップした {@link WritableStore}<br />
 * 書き込んだ量に応じて自動的にフラッシュする
 * 
 * @author 俺用
 * @since 2014/04/13 PetitBinaryJavaassist
 * 
 */
public final class OutputStreamStore implements WritableStore, Closeable, Flushable {
	
	/**
	 * 自動的にフラッシュする閾値
	 */
	public static final int DEFAULT_FLUSH_THRESHOLD = 1024;
	
	private final DataOutputStream _dst;
	
	private final LinkedList<SerializationByteOrder> _bs;
	
	private boolean _curBigEndian;
	
	private int _flush_thresh;
	
	private int _last_flush_pos;
	
	private int _cur_pos;
	
	private Object _ctx;
	
	/**
	 * 初期化<br />
	 * 自動的にフラッシュするための閾値は {@value #DEFAULT_FLUSH_THRESHOLD} が与えられる
	 * 
	 * @param dst 対象の {@link OutputStream}
	 */
	public OutputStreamStore(final OutputStream dst) {
		this(dst, DEFAULT_FLUSH_THRESHOLD);
	}
	
	/**
	 * 初期化
	 * 
	 * @param dst 対象の {@link OutputStream}
	 * @param flush_thresh 自動的にフラッシュするための閾値(>0)
	 */
	public OutputStreamStore(final OutputStream dst, final int flush_thresh) {
		if (dst == null)
			throw new NullPointerException("Argument dst must not be null");
		
		
		_dst = (DataOutputStream) (dst instanceof DataOutputStream ? dst : new DataOutputStream(dst));
		_bs = new LinkedList<Store.SerializationByteOrder>();
		setFlushThreshold(flush_thresh);
		_cur_pos = _last_flush_pos = 0;
		_ctx = null;
		defaultEndian();
	}
	
	private final void defaultEndian() {
		_curBigEndian = true;
	}
	
	private final void setEndian(final SerializationByteOrder bo) {
		switch (bo) {
		case NEUTRAL: break;
		case BIG_ENDIAN: _curBigEndian = true; break;
		case LITTLE_ENDIAN: _curBigEndian = false; break;
		}
	}
	
	private static final short switch2(final short v) {
		return (short) (((v >> 8) & 0xff) | ((v & 0xff) << 8));
	}
	
	private static final int switch4(final int v) {
		return
				((v >> 24) & 0xff)       |
				((v >> 16) & 0xff) <<  8 |
				((v >> 8)  & 0xff) << 16 |
				( v        & 0xff) << 24;
	}
	
	private static final long switch8(final long v) {
		return ((long) switch4((int) (v & 0xffffffff))) << 32 | switch4((int) ((v >> 32) & 0xffffffff));
	}
	
	private final void incrementPos() throws IOException {
		_cur_pos++;
		if (_flush_thresh < (_cur_pos - _last_flush_pos)) {
			_flush_thresh = _cur_pos;
			_dst.flush();
		}
	}
	
	private final void incrementPos(final int diff) throws IOException {
		_cur_pos += diff;
		if (_flush_thresh < (_cur_pos - _last_flush_pos)) {
			_flush_thresh = _cur_pos;
			_dst.flush();
		}
	}
	
	@Override
	public void close() throws IOException {
		_dst.close();
	}
	
	@Override
	public void flush() throws IOException {
		_dst.flush();
	}
	
	/**
	 * 自動的にフラッシュするための閾値を得る
	 * 
	 * @return 自動的にフラッシュするための閾値
	 */
	public final int getFlushThreshold() {
		return _flush_thresh;
	}
	
	/**
	 * 自動的にフラッシュするための閾値を設定する
	 * 
	 * @param flush_thresh 自動的にフラッシュするための閾値(>0)
	 */
	public final void setFlushThreshold(final int flush_thresh) {
		if (flush_thresh < 0)
			throw new IllegalArgumentException("Argument flush_thresh must be more than or equal to 0");
		
		_flush_thresh = flush_thresh;
	}
	
	@Override
	public void pushByteOrder(SerializationByteOrder bo) {
		_bs.push(bo);
		setEndian(bo);
	}
	
	@Override
	public void popByteOrder() {
		if (_bs.isEmpty()) {
			defaultEndian();
			return;
		}
		setEndian(_bs.pop());
	}
	
	@Override
	public SerializationByteOrder currentByteOrder() {
		if (_bs.isEmpty())
			return SerializationByteOrder.NEUTRAL;
		else
			return _bs.peek();
	}
	
	@Override
	public void pushType(Class<?> structure) {}
	
	@Override
	public void popType() {}
	
	@Override
	public Class<?> currentType() {
		return null;
	}
	
	@Override
	public int position() {
		return _cur_pos;
	}
	
	@Override
	public void setPosition(int pos) {
		throw new UnsupportedOperationException("setPosition is not supported");
	}
	
	@Override
	public void setContext(Object ctx) {
		_ctx = ctx;
	}
	
	@Override
	public Object getContext() {
		return _ctx;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getContext(Class<T> as) {
		return (T) _ctx;
	}
	
	@Override
	public void writeInt8(byte v) throws IOException {
		_dst.writeByte(v & 0xff);
		incrementPos();
	}
	
	@Override
	public void writeInt16(short v) throws IOException {
		_dst.writeShort(_curBigEndian ? v : switch2(v));
		incrementPos(2);
	}
	
	@Override
	public void writeInt32(int v) throws IOException {
		_dst.writeInt(_curBigEndian ? v : switch4(v));
		incrementPos(4);
	}
	
	@Override
	public void writeInt64(long v) throws IOException {
		_dst.writeLong(_curBigEndian ? v : switch8(v));
		incrementPos(8);
	}
	
	@Override
	public void writeFloat(float v) throws IOException {
		final int tmp = Float.floatToRawIntBits(v);
		_dst.writeInt(_curBigEndian ? tmp : switch4(tmp));
		incrementPos(4);
	}
	
	@Override
	public void writeDouble(double v) throws IOException {
		final long tmp = Double.doubleToRawLongBits(v);
		_dst.writeLong(_curBigEndian ? tmp : switch8(tmp));
		incrementPos(8);
	}
	
}
