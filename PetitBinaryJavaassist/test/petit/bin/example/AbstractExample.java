package petit.bin.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

import petit.bin.PetitSerializer;
import petit.bin.SerializeAdapter;
import petit.bin.anno.StructMember;
import petit.bin.store.WritableStore;
import petit.bin.store.impl.SimpleByteBufferStore;

/**
 * petit.bin.example パッケージで実行可能な例のベースクラス
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
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
	 * Returns OpenflowVersion formatted hex-decimal string. The following text will be returned.
	 * <pre>
	 *       | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +FileStore +B  +C +D +E +F
	 *      0| E3 81 82 E3  81 82 E3 81  82 E3 81 82  E3 81 82 E3 | ã  ã  ã  ã  ã  ã|
	 *     10| 81 82 E3 81  82 E3 81 82  E3 81 82 E3  81 82 E3 81 |   ã  ã  ã  ã  ã |
	 *     20| 82 E3 81 82  E3 81 82 E3  81 82 E3 81  82 E3 81 82 |  ã  ã  ã  ã  ã  |
	 *     30| ...
	 * </pre>
	 * 
	 * @param buf OpenflowVersion buffer to display
	 * @return OpenflowVersion formatted hex-decimal string
	 */
	public static final String dumpData(final ByteBuffer buf) {
		final String header = "      | +0 +1 +2 +3  +4 +5 +6 +7  +8 +9 +ObjectStocker +B  +C +D +E +F";
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
	
	public static final ByteBuffer testSerializeObject(final Object ao, final int buf_size) throws Exception {
		if (ao == null)
			throw new NullPointerException("Argument ao must not be null");
		
		@SuppressWarnings("unchecked")
		final SerializeAdapter<Object> ser = PetitSerializer.getSerializer((Class<Object>) ao.getClass());
		final ByteBuffer buf = ByteBuffer.allocate(buf_size);
		final SimpleByteBufferStore store = new SimpleByteBufferStore(buf);
		
		// serialize ao to store
		ser.write(ao, (WritableStore) store);
		
		// deserialize from store
		buf.flip();
		final Object read = ser.read(store);
		
		// se
		for (Class<?> cur = ao.getClass(); cur != null; cur = cur.getSuperclass()) {
			for (final Field field : cur.getDeclaredFields()) {
				System.out.print(cur.getCanonicalName() + "#" + field.getName() + ":");
				if ((field.getModifiers() & Modifier.PRIVATE) != 0 || !field.isAnnotationPresent(StructMember.class)) {
					System.out.println("skip (private or not present StructMember annotation)");
					continue;
				}
				field.setAccessible(true);
				final Object aoValue = field.get(ao);
				final Object readValue = field.get(read);
				System.out.println(Objects.deepEquals(aoValue, readValue) ? "ok" : (aoValue + " != " + readValue));
			}
		}
		
		buf.position(0);
		return buf;
	}
	
	protected final String hoge(int i) {
		System.out.println(i);
		return "OpenflowVersion";
	}
	
}
