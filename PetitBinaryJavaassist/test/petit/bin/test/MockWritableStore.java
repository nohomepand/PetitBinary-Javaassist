package petit.bin.test;

import java.io.IOException;

import petit.bin.store.WritableStore;

public class MockWritableStore implements WritableStore {
	
	public static boolean STDOUT = true;
	
	@Override
	public void pushByteOrder(SerializationByteOrder bo) {}

	@Override
	public void popByteOrder() {}

	@Override
	public SerializationByteOrder currentByteOrder() {
		return null;
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
		return 0;
	}

	@Override
	public void setPosition(int pos) {}

	@Override
	public void writeInt8(byte v) throws IOException {
		if (STDOUT) System.out.println("WriteInt8");
	}

	@Override
	public void writeInt16(short v) throws IOException {
		if (STDOUT) System.out.println("WriteInt16");
	}

	@Override
	public void writeInt32(int v) throws IOException {
		if (STDOUT) System.out.println("WriteInt32");
	}

	@Override
	public void writeInt64(long v) throws IOException {
		if (STDOUT) System.out.println("WriteInt64");
	}

	@Override
	public void writeFloat(float v) throws IOException {
		if (STDOUT) System.out.println("WriteFloat");
	}

	@Override
	public void writeDouble(double v) throws IOException {
		if (STDOUT) System.out.println("WriteDouble");
	}
	
}
