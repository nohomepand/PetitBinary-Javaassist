package petit.bin.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.util.Arrays;

public abstract class AbstractExample {
	
	private static final char[][] HEX_TABLE;
	
	static {
		HEX_TABLE = new char[0x100][2];
		for (int i = 0; i < 0x100; i++) {
			HEX_TABLE[i][0] = "0123456789ABCDEF".charAt(i / 0x10);
			HEX_TABLE[i][1] = "0123456789ABCDEF".charAt(i % 0x10);
		}
	}
	
	/**
	 * Returns a formatted hex-decimal string. The following text will be returned.
	 * <pre>
	 *       | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +A +B  +C +D +E +F
	 *      0| E3 81 82 E3  81 82 E3 81  82 E3 81 82  E3 81 82 E3 | ã  ã  ã  ã  ã  ã|
	 *     10| 81 82 E3 81  82 E3 81 82  E3 81 82 E3  81 82 E3 81 |   ã  ã  ã  ã  ã |
	 *     20| 82 E3 81 82  E3 81 82 E3  81 82 E3 81  82 E3 81 82 |  ã  ã  ã  ã  ã  |
	 *     30| ...
	 * </pre>
	 * 
	 * @param buf a buffer to display
	 * @return a formatted hex-decimal string
	 */
	public static final String dumpData(final ByteBuffer buf) {
		final String header = "      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +A +B  +C +D +E +F";
		final char[] line_hex = new char[3 * 0x10/* hex string */ + 1 * 4 /* and paddings ' ' */];
		final char[] line_chr = new char[1 * 0x10/* character  */];
		final StringWriter sw = new StringWriter();
		final PrintWriter ps = new PrintWriter(sw);
		int ptr = 0;
		
		while (buf.remaining() > 0) {
			if ((ptr % 0x10) == 0)
				ps.println(header);
			ps.printf("%6X|", ptr * 0x10L);
			
			Arrays.fill(line_hex, ' ');
			Arrays.fill(line_chr, ' ');
			for (int i = 0, hex_idx = 0, chr_idx = 0; buf.remaining() > 0 && i <= 0xf; i++) {
				final int v = buf.get() & 0xff;
				if ((i % 4) == 0)
					hex_idx++;
				line_hex[hex_idx++] = HEX_TABLE[v][0];
				line_hex[hex_idx++] = HEX_TABLE[v][1];
				hex_idx++;
				
				if (Character.isDigit(v) || Character.isLetter(v))
					line_chr[chr_idx++] = (char) v;
				else
					chr_idx++;
			}
			
			ps.printf("%s| %s|", new String(line_hex), new String(line_chr));
			if (buf.remaining() > 0)
				ps.println();
			ptr++;
		}
		
		return sw.toString();
	}
//	
//	/**
//	 * Tests serialization.
//	 * This method serialize "ao" to a "buffer",
//	 * deserialize the "buffer" to "obj",
//	 * and then checks whether all fields of "ao" and "obj" are equal to another or not.
//	 * 
//	 * @param ao an object to check
//	 * @return serialized data
//	 * @throws RuntimeException
//	 */
//	public static final ByteBuffer checkSerializedObject(final Object ao) throws RuntimeException {
//		if (ao == null)
//			throw new NullPointerException("Argument ao must not be null");
//		
//		try {
//			final BinaryAccessor<Object> ba = FACTORY.getBinaryAccessor(ao.getClass());
//			final InOutByteBuffer buf = new InOutByteBuffer();
//			
//			// serialize to buf
//			ba.writeTo(null, ao, buf);
//			
//			// deserialize to obj
//			final Object obj = ba.readFrom(null, new InOutByteBuffer(buf.getFlippedShallowCopy()));
//			
//			// check all of the ao's fields are equal to obj
//			for (final Field field : ReflectionUtil.getVisibleFields(ao.getClass(), VisibilityConstraint.INHERITED_CLASS_VIEWPOINT, null, null)) {
//				if (!field.isAnnotationPresent(StructMember.class)) {
//					System.out.println(field.getDeclaringClass().getCanonicalName() + "#" + field.getName() + " : skip");
//					continue;
//				}
//				field.setAccessible(true);
//				final Object ao_field = field.get(ao);
//				final Object obj_field = field.get(obj);
//				
//				System.out.println(field.getDeclaringClass().getCanonicalName() + "#" + field.getName() +
//						" : " + (objectEquals(ao_field, obj_field) ? "ok" : (ao_field + " != " + (obj_field.getClass().isArray() ? Arrays.toString((Object[])obj_field) : obj_field.toString()))));
//			}
//			return buf.getFlippedShallowCopy();
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new RuntimeException("Exception caused while checking read/write object", e);
//		}
//	}
//	
//	/**
//	 * Java7ならObjectsにありそうなオブジェクト同士の比較
//	 * 
//	 * @param x オブジェクト１
//	 * @param y オブジェクト２
//	 * @return オブジェクト１と オブジェクト２が実効的に同値なら true
//	 */
//	public static final boolean objectEquals(final Object x, final Object y) {
//		if (x == null) {
//			return y == null;
//		} else if (x.getClass().isArray()) {
//			final int size;
//			if (!y.getClass().isArray() || (size = Array.getLength(x)) != Array.getLength(y))
//				return false;
//			for (int i = 0; i < size; i++)
//				if (!objectEquals(Array.get(x, i), Array.get(y, i)))
//					return false;
//			return true;
//		} else
//			return x.equals(y);
//	}
	
}
