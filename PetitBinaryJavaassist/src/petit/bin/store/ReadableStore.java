package petit.bin.store;

import java.io.IOException;

/**
 * 読み込み可能なストアを表す
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public interface ReadableStore extends Store {
	
	// read data
	/**
	 * 8ビット符号付整数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 1 バイトを読み込み，それを 8ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract byte readInt8() throws IOException;
	
	/**
	 * 16ビット符号付整数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 2 バイトを読み込み，それを現在のバイトオーダーで 16ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract short readInt16() throws IOException;
	
	/**
	 * 32ビット符号付整数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 4 バイトを読み込み，それを現在のバイトオーダーで 32ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract int readInt32() throws IOException;
	
	/**
	 * 64ビット符号付整数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 8 バイトを読み込み，それを現在のバイトオーダーで 64ビット符号付整数値として解釈した値
	 * @throws IOException
	 */
	public abstract long readInt64() throws IOException;
	
	/**
	 * 単精度浮動少数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 4 バイトを読み込み単精度浮動少数値として解釈した値
	 * @throws IOException
	 */
	public abstract float readFloat() throws IOException;
	
	/**
	 * 倍精度浮動少数値として解釈した値を得る<br />
	 * この操作は {@link #packSize()} によって自動的にパディング分を読み捨てられる可能性がある
	 * 
	 * @return 8 バイトを読み込み倍精度浮動少数値として解釈した値
	 * @throws IOException
	 */
	public abstract double readDouble() throws IOException;
	
}
