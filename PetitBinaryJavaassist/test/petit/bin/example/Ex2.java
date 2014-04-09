package petit.bin.example;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.ExternStruct;
import petit.bin.store.Store.SerializationByteOrder;

/**
 * 例2
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
public class Ex2 extends AbstractExample {
	
	@StructMember(0)
	protected boolean _flag;
	
	@StructMember(1)
	@ExternStruct("resolveStringObject")
	protected BinaryString _str;
	
//	protected final BinaryString resolveStringObject() {
//		if (_flag)
//			return new BinaryString.NullTerminatedString(null);
//		else
//			return new BinaryString.BString(null);
//	}
	
	public final void set(final BinaryString bs) {
		_flag = bs instanceof BinaryString.NullTerminatedString;
		_str = bs;
	}
	
	protected final Class<? extends BinaryString> resolveStringObject() {
		if (_flag)
			return BinaryString.NullTerminatedString.class;
		else
			return BinaryString.BString.class;
	}
	
	public static void main(String[] args) throws Exception {
		final Ex2 ao = new Ex2();
		ao.set(new BinaryString.NullTerminatedString("foo"));
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		
		ao.set(new BinaryString.BString("foo"));
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		/*
			petit.bin.example.Ex2#_flag:ok
			petit.bin.example.Ex2#_str:ok
			petit.bin.example.AbstractExample#HEX_TABLE:skip (private or not present StructMember annotation)
			      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +ObjectStocker +B  +C +D +E +F
			     0| 01 66 6F 6F  00                                    |  foo            |
			petit.bin.example.Ex2#_flag:ok
			petit.bin.example.Ex2#_str:ok
			petit.bin.example.AbstractExample#HEX_TABLE:skip (private or not present StructMember annotation)
			      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +ObjectStocker +B  +C +D +E +F
			     0| 00 03 00 00  00 66 6F 6F                           |      foo        |
		 */
	}
	
}
