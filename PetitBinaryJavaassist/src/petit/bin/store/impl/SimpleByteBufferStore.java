package petit.bin.store.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;

/**
 * {@link ByteBuffer} を使った {@link ReadableStore} および {@link WritableStore}
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
public class SimpleByteBufferStore implements ReadableStore, WritableStore {
	
	private final ByteBuffer _buf;
	
	private final LinkedList<SerializationByteOrder> _sb_stack;
	
	/**
	 * 初期化
	 * 
	 * @param buf バッファ
	 */
	public SimpleByteBufferStore(final ByteBuffer buf) {
		if (buf == null)
			throw new NullPointerException("Argument buf must not be null");
		
		_buf = buf;
		_sb_stack = new LinkedList<>();
	}
	
	@Override
	public void pushByteOrder(SerializationByteOrder bo) {
		_sb_stack.push(bo);
		if (bo != SerializationByteOrder.NEUTRAL) {
			switch (bo) {
			case BIG_ENDIAN: _buf.order(ByteOrder.BIG_ENDIAN); break;
			case LITTLE_ENDIAN: _buf.order(ByteOrder.LITTLE_ENDIAN); break;
			}
		}
	}
	
	@Override
	public void popByteOrder() {
		_sb_stack.pop();
	}
	
	@Override
	public SerializationByteOrder currentByteOrder() {
		if (_sb_stack.isEmpty())
			return SerializationByteOrder.NEUTRAL;
		else
			return _sb_stack.peek();
	}
	
	@Override
	public void pushType(Class<?> structure) {
		// do nothing
	}
	
	@Override
	public void popType() {
		// do nothing
	}
	
	@Override
	public Class<?> currentType() {
		return null;
	}
	
	@Override
	public int position() {
		return _buf.position();
	}
	
	@Override
	public void setPosition(int pos) {
		_buf.position(pos);
	}
	
	@Override
	public void writeInt8(byte v) throws IOException {
		_buf.put(v);
	}
	
	@Override
	public void writeInt16(short v) throws IOException {
		_buf.putShort(v);
	}
	
	@Override
	public void writeInt32(int v) throws IOException {
		_buf.putInt(v);
	}
	
	@Override
	public void writeInt64(long v) throws IOException {
		_buf.putLong(v);
	}
	
	@Override
	public void writeFloat(float v) throws IOException {
		_buf.putFloat(v);
	}
	
	@Override
	public void writeDouble(double v) throws IOException {
		_buf.putDouble(v);
	}
	
	@Override
	public byte readInt8() throws IOException {
		return _buf.get();
	}
	
	@Override
	public short readInt16() throws IOException {
		return _buf.getShort();
	}
	
	@Override
	public int readInt32() throws IOException {
		return _buf.getInt();
	}
	
	@Override
	public long readInt64() throws IOException {
		return _buf.getLong();
	}
	
	@Override
	public float readFloat() throws IOException {
		return _buf.getFloat();
	}
	
	@Override
	public double readDouble() throws IOException {
		return _buf.getDouble();
	}
	
}
