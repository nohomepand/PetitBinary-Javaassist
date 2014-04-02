package petit.bin.example;

import java.io.IOException;
import java.nio.charset.Charset;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.store.ReadableStore;
import petit.bin.store.Store.SerializationByteOrder;

/**
 * 文字列を表す
 * 
 * @author 俺用
 * @since 2014/04/01 PetitBinaryJavaassist
 *
 */
@Struct(byteOrder = SerializationByteOrder.NEUTRAL)
public abstract class BinaryString {
	
	private static final Charset internal_cs = Charset.forName("utf-8");
	
	/**
	 * 文字列を得る
	 * 
	 * @return 文字列
	 */
	public abstract String get();
	
	@Override
	public String toString() {
		return get();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof BinaryString && ((BinaryString) obj).get().equals(get());
	}
	
	/**
	 * ヌル文字で終端する文字列を表す<br />
	 * 内部クラスをシリアライズクラスとする場合は static メンバにしなければならない
	 * 
	 * @author 俺用
	 * @since 2014/04/01 PetitBinaryJavaassist
	 *
	 */
	public static final class NullTerminatedString extends BinaryString {
		
		/**
		 * 文字列
		 */
		@StructMember(0)
		@ArraySizeByMethod("getStringSize")
		protected byte[] _data;
		
		/**
		 * {@link #_data} の配列サイズを決定するメソッド<br />
		 * シグネチャは次のいずれかで無ければならない<br />
		 * <code>[byte, short, int or long] [method name](ReadableStore src)</code>
		 * 
		 * @param src 入力元
		 * @return 終端のヌル文字を含まない文字列長
		 * @throws IOException 
		 */
		protected final int getStringSize(final ReadableStore src) throws IOException {
			final int mark = src.position();
			int size;
			for (size = 0; src.readInt8() != 0; size++);
			src.setPosition(mark);
			return size;
		}
		
		/**
		 * 文字列の終端のヌル文字
		 */
		@StructMember(1)
		protected byte _null_char;
		
		/**
		 * 初期化
		 * 
		 * @param str 文字列
		 */
		public NullTerminatedString(final String str) {
			_null_char = 0;
			
			if (str == null)
				return;
			_data = str.getBytes(internal_cs);
			
		}
		
		@Override
		public String get() {
			return new String(_data, internal_cs);
		}
		
	}
	
	/**
	 * よくある文字列長+文字列からなる文字列を表す
	 * 
	 * @author 俺用
	 * @since 2014/04/01 PetitBinaryJavaassist
	 *
	 */
	public static final class BString extends BinaryString {
		
		/**
		 * 文字列長
		 */
		@StructMember(0)
		protected int _length;
		
		/**
		 * 文字列
		 */
		@StructMember(1)
		@ArraySizeByField("_length")
		protected byte[] _data;
		
		/**
		 * 初期化
		 * 
		 * @param str 文字列
		 */
		public BString(final String str) {
			if (str == null)
				return;
			_data = str.getBytes(internal_cs);
			_length = _data.length;
		}
		
		@Override
		public String get() {
			return new String(_data, internal_cs);
		}
		
	}
	
}
