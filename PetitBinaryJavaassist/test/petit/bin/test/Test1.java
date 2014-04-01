package petit.bin.test;

import java.nio.ByteBuffer;

import petit.bin.SerializeAdapter;
import petit.bin.PetitSerializer;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.anno.field.UInt16;
import petit.bin.example.AbstractExample;
import petit.bin.store.ReadableStore;
import petit.bin.store.Store.SerializationByteOrder;
import petit.bin.store.impl.SimpleByteBufferStore;

@Struct(byteOrder = SerializationByteOrder.NEUTRAL)
public class Test1 {
	
	@StructMember(0)
	protected int v1;
	
	@StructMember(1)
	protected int v2;
	
//	@StructMember(2)
	protected int v3;
	
	@StructMember(3)
	@UInt16
	protected int v4;
	
	@StructMember(4)
	protected Inner1 v5;
	
	@StructMember(5)
	@ArraySizeConstant(5)
	protected Inner1[] v6;
	
	private final void test1Private() {
		System.out.println("aaa");
	}
	
	@Struct
	public static class Inner1 {
		
		@StructMember(5)
		protected int iv1;
		
		@StructMember(6)
		protected double iv2;
		
	}
	
	@Struct
	public class Inner2 {
		
		@StructMember(5)
		protected int iv1;
		
		@StructMember(6)
		protected double iv2;
		
	}
	
	@Struct
	public static final class Inner3 {
		
		@StructMember(0)
		protected int iv1;
		
		@StructMember(1)
		public double iv2;
		
		@StructMember(2)
		@ArraySizeConstant(10)
		public int[] iv3;
		
		@StructMember(3)
		@ArraySizeByMethod("aaa")
		protected byte[] iv4;
		
		@StructMember(4)
		protected Test1 iv5;
		
		protected final int aaa(ReadableStore s) {
//			((Test1) this).v1 = 10;
			return 4;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		final SerializeAdapter<Test1.Inner3> adapter = PetitSerializer.getSerializer(Test1.Inner3.class);
//		System.out.println(adapter.getTargetClass());
		MockReadableStore.STDOUT = false;
		MockWritableStore.STDOUT = true;
		final Inner3 ao = new Test1.Inner3();
		ao.iv2 = 1.234;
		adapter.read(ao, new MockReadableStore());
		System.out.println(ao.iv2);
		System.out.println("---------------------");
		ao.iv1 = 100;
		for (int i = 0; i < ao.iv3.length; i++)
			ao.iv3[i] = i + 1;
		ao.iv5.v1 = 1;
		ao.iv5.v2 = 2;
		ao.iv5.v3 = 3;
		ao.iv5.v4 = -2;
//		adapter.write(ao, new MockWritableStore());
		final ByteBuffer bb = ByteBuffer.allocate(10000);
		final SimpleByteBufferStore sb = new SimpleByteBufferStore(bb);
		adapter.write(ao, sb);
		bb.flip();
		System.out.println(AbstractExample.dumpData(bb));
		System.out.println(adapter.getClass());
	}
	
}
