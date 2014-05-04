package petit.bin.example;

import petit.bin.anno.ReadValidator;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.store.ReadableStore;
import petit.bin.store.Store.SerializationByteOrder;

@Struct(byteOrder = SerializationByteOrder.NEUTRAL)
public class Ex5 extends AbstractExample {
	
	@StructMember(0)
	@ReadValidator("v1Validator")
	protected int v1;
	
	protected final void v1Validator(ReadableStore rs) {
		if (v1 < 0)
			throw new IllegalStateException("v1 < 0");
	}
	
	@StructMember(value = 1, paddingAfter = 4)
	@ReadValidator("v2Validator")
	protected int v2;
	
	protected final void v2Validator(ReadableStore rs) {
		if (v2 > 0)
			throw new IllegalStateException("v2 > 0");
	}
	
	@StructMember(2)
	protected int v3;
	
	public static void main(String[] args) throws Exception {
		final Ex5 ao = new Ex5();
		ao.v1 = 1;
		ao.v2 = -1;
		ao.v3 = 10;
		System.out.println(dumpData(testSerializeObject(ao, 100)));
	}
	
}
