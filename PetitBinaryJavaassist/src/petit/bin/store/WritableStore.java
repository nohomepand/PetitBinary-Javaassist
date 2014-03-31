package petit.bin.store;

import java.io.IOException;

/**
 * 書き込み可能なストアを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public interface WritableStore extends Store {
	
	/**
	 * 8ビット符号付整数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt8(final byte v) throws IOException;
	
	/**
	 * 16ビット符号付整数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt16(final short v) throws IOException;
	
	/**
	 * 32ビット符号付整数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt32(final int v) throws IOException;
	
	/**
	 * 64ビット符号付整数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeInt64(final long v) throws IOException;
	
	/**
	 * 単制度浮動少数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeFloat(final float v) throws IOException;
	
	/**
	 * 倍制度浮動少数値を書き込む<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を進められる可能性がある
	 * 
	 * @param v 値
	 * @throws IOException
	 */
	public abstract void writeDouble(final double v) throws IOException;
	
}
