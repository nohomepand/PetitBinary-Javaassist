package petit.bin.example;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.store.Store.SerializationByteOrder;

/**
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
@Struct(byteOrder = SerializationByteOrder.NEUTRAL)
public final class Point extends AbstractExample {
	
	/**
	 * 初期化
	 * 
	 * @param x x
	 * @param y y
	 */
	public Point(final int x, final int y) {
		_x = x;
		_y = y;
	}
	
	@StructMember(0)
	protected int _x;
	
	@StructMember(1)
	protected int _y;
	
	public static void main(String[] args) throws Exception {
		final Point ao = new Point(1, 2);
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		
		/* 
		 * outputs:
			petit.bin.example.Point#_x:ok
			petit.bin.example.Point#_y:ok
			petit.bin.example.AbstractExample#HEX_TABLE:skip (private or not present a StructMember annotation)
			      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +A +B  +C +D +E +F
			     0| 00 00 00 01  00 00 00 02                           |                 |
		 */
	}
	
}
