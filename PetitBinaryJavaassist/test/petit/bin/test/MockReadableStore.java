package petit.bin.test;

import java.io.IOException;

import petit.bin.store.ReadableStore;

public class MockReadableStore implements ReadableStore {

	@Override
	public void pushByteOrder(SerializationByteOrder bo) {
		
	}

	@Override
	public void popByteOrder() {
		
	}

	@Override
	public SerializationByteOrder currentByteOrder() {
		return null;
	}

	@Override
	public void pushType(Class<?> structure) {
		
	}

	@Override
	public void popType() {
		
	}

	@Override
	public Class<?> currentType() {
		return null;
	}

	@Override
	public int position() {
		return 0;
	}

	@Override
	public void setPosition(int pos) {
		
	}

	@Override
	public byte readInt8() throws IOException {
		System.out.println("ReadInt8");
		return 0;
	}

	@Override
	public short readInt16() throws IOException {
		System.out.println("ReadInt16");
		return 0;
	}

	@Override
	public int readInt32() throws IOException {
		System.out.println("ReadInt32");
		return 0;
	}

	@Override
	public long readInt64() throws IOException {
		System.out.println("ReadInt64");
		return 0;
	}

	@Override
	public float readFloat() throws IOException {
		System.out.println("ReadFloat");
		return 0;
	}

	@Override
	public double readDouble() throws IOException {
		System.out.println("ReadDouble");
		return 0;
	}
	
}
