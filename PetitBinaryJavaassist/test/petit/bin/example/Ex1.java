package petit.bin.example;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.store.Store.SerializationByteOrder;

/**
 * 例1
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
public class Ex1 extends AbstractExample {
	
	/**
	 * public, protected, default なアクセスのフィールドのみが {@link StructMember} を与えられる
	 */
	@StructMember(0)
	protected int int1;
	
	public static void main(String[] args) throws Exception {
		final Ex1 ao = new Ex1();
		ao.int1 = 10;
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		
		/*
		 * outputs:
			petit.bin.example.Ex1#int1:ok
			petit.bin.example.AbstractExample#HEX_TABLE:skip (private or not present a StructMember annotation)
			      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +FileStore +B  +C +D +E +F
			     0| 0A 00 00 00                                        |                 |
		 */
	}
	
}
