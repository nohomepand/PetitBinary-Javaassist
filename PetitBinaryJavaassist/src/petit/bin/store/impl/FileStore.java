package petit.bin.store.impl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.LinkedList;

import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;

/**
 * {@link File} のストア
 * 
 * @author 俺用
 * @since 2014/04/02 PetitBinaryJavaassist
 * 
 */
public final class FileStore implements ReadableStore, WritableStore {
	
	public static final FileStore openRead(final File f) throws IOException {
		return open(f, MapMode.READ_ONLY, (int) f.length());
	}
	
	public static final FileStore openWrite(final File f, final int size) throws IOException {
		return open(f, MapMode.READ_WRITE, size);
	}
	
	public static final FileStore open(final File f, final MapMode mode, final int size) throws IOException {
		final FileChannel ch;
		if (mode == MapMode.READ_ONLY || mode == MapMode.PRIVATE)
			ch = new RandomAccessFile(f, "r").getChannel();
		else
			ch = new RandomAccessFile(f, "rw").getChannel();
		return new FileStore(ch, ch.map(mode, 0, size));
	}
	
	private final FileChannel _fc;
	
	private final MappedByteBuffer _buf;
	
	private final LinkedList<SerializationByteOrder> _bs;
	
	private Object _ctx;
	
	/**
	 * 初期化
	 * 
	 * @param fc 元のファイルチャネル
	 * @param mbb 元のファイルチャネルから生成した {@link MappedByteBuffer}
	 */
	private FileStore(final FileChannel fc, final MappedByteBuffer mbb) {
		if (fc == null)
			throw new NullPointerException("Argument fc must not be null");
		if (mbb == null)
			throw new NullPointerException("Argument mbb must not be null");
		
		_fc = fc;
		_buf = mbb;
		_bs = new LinkedList<>();
		_ctx = null;
	}
	
	public final MappedByteBuffer buffer() {
		return _buf;
	}
	
	public final void close() throws IOException {
		_fc.close();
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
	}
	
	@Override
	public void pushByteOrder(SerializationByteOrder bo) {
		_bs.push(bo);
		switch (bo) {
		case NEUTRAL: break;
		case BIG_ENDIAN: _buf.order(ByteOrder.BIG_ENDIAN); break;
		case LITTLE_ENDIAN: _buf.order(ByteOrder.LITTLE_ENDIAN); break;
		}
	}
	
	@Override
	public void popByteOrder() {
		if (_bs.isEmpty())
			return;
		_bs.pop();
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
	
	@Override
	public Object getContext() {
		return _ctx;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getContext(Class<T> as) {
		return (T) as;
	}
	
	@Override
	public void setContext(Object ctx) {
		_ctx = ctx;
	}
	
}
