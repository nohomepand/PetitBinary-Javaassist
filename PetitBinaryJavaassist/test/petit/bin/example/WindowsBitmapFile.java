package petit.bin.example;

import java.io.File;

import petit.bin.PetitSerializer;
import petit.bin.SerializeAdapter;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.array.ArraySizeByField;
import petit.bin.anno.array.ArraySizeByMethod;
import petit.bin.anno.field.ExternStruct;
import petit.bin.anno.field.UInt16;
import petit.bin.store.ReadableStore;
import petit.bin.store.Store.SerializationByteOrder;
import petit.bin.store.impl.FileStore;

@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
public final class WindowsBitmapFile {
	
	@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
	public static final class BITMAPFILEHEADER {
		// typedef struct tagBITMAPFILEHEADER {
		// unsigned short bfType;
		// unsigned long bfSize;
		// unsigned short bfReserved1;
		// unsigned short bfReserved2;
		// unsigned long bfOffBits;
		// } BITMAPFILEHEADER
		
		@StructMember(0)
		@UInt16
		protected int bfType;
		
		@StructMember(1)
		protected int bfSize;
		
		@StructMember(2)
		protected short bfReserved1;
		
		@StructMember(3)
		protected short bfReserved2;
		
		@StructMember(4)
		protected int bfOffBits;
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("bfType=").append(Integer.toHexString(bfType))
					.append(", bfSize=").append(bfSize)
					.append(", bfOffBits=").append(bfOffBits)
					.toString();
		}
		
	}
	
	@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
	public static final class BITMAPINFOHEADER {
		// typedef struct tagBITMAPINFOHEADER{
		// unsigned long biSize;
		// long biWidth;
		// long biHeight;
		// unsigned short biPlanes;
		// unsigned short biBitCount;
		// unsigned long biCompression;
		// unsigned long biSizeImage;
		// long biXPixPerMeter;
		// long biYPixPerMeter;
		// unsigned long biClrUsed;
		// unsigned long biClrImporant;
		// } BITMAPINFOHEADER;
		@StructMember(0)
		protected int biSize;
		
		@StructMember(1)
		protected int biWidth;
		
		@StructMember(2)
		protected int biHeight;
		
		@StructMember(3)
		protected short biPlanes;
		
		@StructMember(4)
		protected short biBitCount;
		
		@StructMember(5)
		protected int biCompression;
		
		@StructMember(6)
		protected int biSizeImage;
		
		@StructMember(7)
		protected int biXPixelPerMeter;
		
		@StructMember(8)
		protected int biYPixelPerMeter;
		
		@StructMember(9)
		protected int biColorUsed;
		
		@StructMember(10)
		protected int biColorImportant;
		
		@Override
		public String toString() {
			return new StringBuilder()
					.append("biSize=").append(biSize)
					.append(", biWidth=").append(biWidth)
					.append(", biHeight=").append(biHeight)
					.append(", biPlanes=").append(biPlanes)
					.append(", biBitCount=").append(biBitCount)
					.append(", biCompression=").append(biCompression)
					.append(", biSizeImage=").append(biSizeImage)
					.append(", biXPixelPerMeter=").append(biXPixelPerMeter)
					.append(", biYPixelPerMeter=").append(biYPixelPerMeter)
					.append(", biColorUsed=").append(biColorUsed)
					.append(", biColorImportant=").append(biColorImportant)
					.toString();
		}
		
	}
	
	public static interface ColorPalette {
		public abstract int paletteSize();
	}
	
	@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
	public final class NullColorPalette implements ColorPalette {
		@Override
		public int paletteSize() {
			return 0;
		}
		
		@Override
		public String toString() {
			return "NoColorPalette";
		}
	}
	
	@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
	public static final class RGBQUAD {
		
		@StructMember(0)
//		@Int8Array
//		@ArraySizeConstant(4)
//		protected byte[] color;
		protected int color;
		
		@Override
		public String toString() {
			return Integer.toHexString(color);
		}
		
	}
	
	@Struct(byteOrder = SerializationByteOrder.LITTLE_ENDIAN)
	public static final class IndexedColorPalette implements ColorPalette {
		
		protected int _palette_size;
		
		@StructMember(0)
		@ArraySizeByField("_palette_size")
		protected RGBQUAD[] _palette;
		
		public IndexedColorPalette(final int palette_size) {
			_palette_size = palette_size;
			_palette = new RGBQUAD[palette_size];
		}
		
		@Override
		public int paletteSize() {
			return _palette_size;
		}
		
		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append("Palette[");
			for (int i = 0; i < _palette_size; i++) {
				sb.append(_palette[i]);
				if (i != _palette.length - 1)
					sb.append(", ");
			}
			sb.append("]");
			return sb.toString();
		}
		
	}
	
	@StructMember(0)
	protected BITMAPFILEHEADER bfHeader;
	
	@StructMember(1)
	protected BITMAPINFOHEADER bfInfo;
	
	@StructMember(2)
	@ExternStruct("resolveColorPalette")
	protected ColorPalette bfColorPalette;
	
	protected ColorPalette resolveColorPalette() {
		if (bfInfo.biBitCount > 8)
			return new NullColorPalette();
		else
			return new IndexedColorPalette(1 << bfInfo.biBitCount);
	}
	
	@StructMember(3)
	@ArraySizeByMethod("resolveDataSize")
	protected byte[] bfData;
	
	protected int resolveDataSize(final ReadableStore s) {
		int line_length = (bfInfo.biWidth * bfInfo.biBitCount) / 8;
		if (line_length % 4 != 0) {
			System.out.println("WIDTH = " + bfInfo.biWidth + " PADDING = " + (((line_length / 4) + 1) * 4 - line_length));
			line_length = ((line_length / 4) + 1) * 4;
		}
		s.setPosition(bfHeader.bfOffBits);
		return line_length * (bfInfo.biHeight < 0 ? -bfInfo.biHeight : bfInfo.biHeight);
	}
	
	@Override
	public String toString() {
		return "Header=" + bfHeader + "\nInfo=" + bfInfo + "\nPalette=" + bfColorPalette + "\nData=" + bfData.length;
	}
	
	public static final class Main extends AbstractExample {
		public static void main(String[] args) throws Exception {
			final SerializeAdapter<WindowsBitmapFile> serwbf = PetitSerializer.getSerializer(WindowsBitmapFile.class);
			final File srcf = new File("z:/test.bmp");
			final FileStore fs = FileStore.openRead(srcf);
			final WindowsBitmapFile bmp = serwbf.read(fs);
			System.out.println(fs.buffer().position() + " | " + srcf.length());
			fs.close();
			
			System.out.println(bmp);
			
			final File dstf = new File("z:/test_out.bmp");
			final FileStore fs2 = FileStore.openWrite(dstf, (int) srcf.length());
			serwbf.write(bmp, fs2);
			fs2.close();
		}
	}
	
}
