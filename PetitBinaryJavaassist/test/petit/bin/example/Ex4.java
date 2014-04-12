package petit.bin.example;

import java.nio.ByteBuffer;

import petit.bin.PetitSerializer;
import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.TypeSafeIndirectValue;
import petit.bin.store.impl.SimpleByteBufferStore;

@Struct
public class Ex4 extends AbstractExample {
	
	/**
	 * enum に列挙されていない値でも許容可能にするためのプレースホルダ
	 * 
	 * @author 俺用
	 * @since 2014/04/12 PetitBinaryJavaassist
	 *
	 */
	public static interface UnexpectValueAcceptableEnum {
		
		public abstract int toInt();
		
	}
	
	public enum Foo implements UnexpectValueAcceptableEnum {
		E1(10), E2(20);
		
		private final int v;
		
		private Foo(final int v) {
			this.v = v;
		}
		
		@Override
		public int toInt() {
			return v;
		}
		
	}
	
	public static final class UnexpectedFoo implements UnexpectValueAcceptableEnum {
		
		private final int v;
		
		public UnexpectedFoo(final int v) {
			this.v = v;
		}
		
		@Override
		public int toInt() {
			return v;
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof UnexpectValueAcceptableEnum && ((UnexpectValueAcceptableEnum) obj).toInt() == v;
		}
		
		@Override
		public String toString() {
			return "UnexpectedFoo[" + v + "]";
		}
		
	}
	
	
	@StructMember(0)
	@TypeSafeIndirectValue(storeType = byte.class, fromStored = "fooFromStored", toStore = "fooToStored")
	protected UnexpectValueAcceptableEnum v1;
	
	protected final UnexpectValueAcceptableEnum fooFromStored(final int i) {
		for (final Foo elm : Foo.values())
			if (elm.toInt() == i)
				return elm;
		return new UnexpectedFoo(i);
	}
	
	protected final int fooToStored(final UnexpectValueAcceptableEnum foo) {
		return foo.toInt();
	}
	
	public static void main(String[] args) throws Exception {
		final Ex4 ao = new Ex4();
		ao.v1 = Foo.E1;
		System.out.println("ao.v1 = " +ao.v1);
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		System.out.println("---------");
		ao.v1 = new UnexpectedFoo(10);
		System.out.println("ao.v1 = " +ao.v1);
		final ByteBuffer bb = testSerializeObject(ao, 100);
		System.out.println(dumpData(bb));
		bb.rewind();
		final Ex4 des = PetitSerializer.getSerializer(Ex4.class).read(new SimpleByteBufferStore(bb));
		System.out.println("des.v1 = " +des.v1);
		System.out.println("---------");
	}
	
}
