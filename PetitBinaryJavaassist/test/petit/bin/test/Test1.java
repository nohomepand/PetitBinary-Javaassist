package petit.bin.test;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.array.ArraySizeConstant;
import petit.bin.store.ReadableStore;
import petit.bin.store.Store.SerializationByteOrder;

@Struct(byteOrder = SerializationByteOrder.NEUTRAL)
public abstract class Test1 {
	
	@StructMember(0)
	protected int v1;
	
	@StructMember(1)
	protected int v2;
	
//	@StructMember(2)
	protected int v3;
	
	@StructMember(3)
	protected int v4;
	
	@StructMember(4)
	protected Inner1 v5;
	
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
	public static class Inner3 extends Test1 {
		
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
		
		protected final int aaa(ReadableStore s) {
			((Test1) this).v1 = 10;
			return 5;
		}
		
	}
	
}
