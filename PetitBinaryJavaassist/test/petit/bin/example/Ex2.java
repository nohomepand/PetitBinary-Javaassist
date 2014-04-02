package petit.bin.example;

import java.lang.reflect.Method;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.ExternStruct;
import petit.bin.store.Store.SerializationByteOrder;
import petit.bin.util.Util;

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
	
	protected final BinaryString resolveStringObject() {
		if (_flag)
			return new BinaryString.NullTerminatedString(null);
		else
			return new BinaryString.BString(null);
	}
	
	public static void main(String[] args) throws Exception {
		final Ex2 ao = new Ex2();
		ao._flag = true;
		ao._str = new BinaryString.NullTerminatedString("foo");
		System.out.println(dumpData(testSerializeObject(ao, 100)));
	}
	
}
