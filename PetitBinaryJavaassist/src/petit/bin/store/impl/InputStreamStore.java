package petit.bin.store.impl;

import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import petit.bin.store.ReadableStore;
import petit.bin.store.Store;

/**
 * {@link InputStream} をラップした {@link ReadableStore}
 * 
 * @author 俺用
 * @since 2014/04/12 PetitBinaryJavaassist
 * 
 */
public final class InputStreamStore implements ReadableStore, Closeable {
	
	private final DataInputStream _src;
	
	private final LinkedList<SerializationByteOrder> _bs;
	
	private boolean _curBigEndian;
	
	private int _cur_pos;
	
	private Object _ctx;
	
	public InputStreamStore(final InputStream src) {
		_src = (DataInputStream) (src instanceof DataInputStream ? src : new DataInputStream(src));
		_bs = new LinkedList<Store.SerializationByteOrder>();
		_cur_pos = 0;
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
	
	@Override
	public void close() throws IOException {
		_src.close();
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
	public byte readInt8() throws IOException {
		final byte v = _src.readByte();
		_cur_pos++;
		return v;
	}
	
	@Override
	public short readInt16() throws IOException {
		final short v = _curBigEndian ? _src.readShort() : switch2(_src.readShort());
		_cur_pos += 2;
		return v;
	}
	
	@Override
	public int readInt32() throws IOException {
		final int v = _curBigEndian ? _src.readInt() : switch4(_src.readInt());
		_cur_pos += 4;
		return v;
	}
	
	@Override
	public long readInt64() throws IOException {
		final long v = _curBigEndian ? _src.readLong() : switch8(_src.readLong());
		_cur_pos += 8;
		return v;
	}
	
	@Override
	public float readFloat() throws IOException {
		final int tmp = _src.readInt();
		_cur_pos += 4;
		return _curBigEndian ? Float.intBitsToFloat(tmp) : Float.intBitsToFloat(switch4(tmp));
	}
	
	@Override
	public double readDouble() throws IOException {
		final long tmp = _src.readLong();
		_cur_pos += 8;
		return _curBigEndian ? Double.longBitsToDouble(tmp) : Double.longBitsToDouble(switch8(tmp));
	}
	
}
