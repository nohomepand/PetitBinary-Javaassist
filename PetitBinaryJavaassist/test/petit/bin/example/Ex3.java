package petit.bin.example;

import java.util.Arrays;

import petit.bin.anno.Struct;
import petit.bin.anno.StructMember;
import petit.bin.anno.field.TypeSafeValue;

@Struct
public class Ex3 extends AbstractExample {
	
	public static enum Foo {
		
		E1(0),
		E2(1),
		E3(100);
		
		public final int N;
		
		private Foo(final int n) {
			N = n;
		}
		
		public int toNumber() {
			return N;
		}
		
		public static final Foo fromNumber(final int n) {
			for (final Foo elm : Foo.values())
				if (elm.N == n)
					return elm;
			throw new IllegalArgumentException("Value " + n + " is not a enum value");
		}
		
	}
	public static enum Bar {
		E1("abcde"),
		E2("defgh"),
		E3("ijklm");
		
		private final byte[] ID;
		
		private Bar(final String id) {
			ID = id.getBytes();
		}
		
		public byte[] toStore() {
			return ID;
		}
		
		public static final Bar fromStored(final byte[] ba) {
			for (final Bar e : Bar.values())
				if (Arrays.equals(e.ID, ba))
					return e;
			return null;
		}
		
	}
	
	@StructMember(0)
	@TypeSafeValue(storeType = byte.class, fromStored = "fromNumber", toStore = "toNumber")
	protected Foo v1;
	
	@StructMember(1)
	@TypeSafeValue(storeType = byte[].class, arraySize = 5, fromStored = "fromStored", toStore = "toStore")
	protected Bar v2;
	
	public static void main(String[] args) throws Exception {
		final Ex3 ao = new Ex3();
		ao.v1 = Foo.E3;
		ao.v2 = Bar.E1;
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		System.out.println(ao.v1);
		System.out.println(ao.v2);
	}
	
}
