package petit.bin.example;

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
	
	@StructMember(0)
	@TypeSafeValue(storeType = byte.class, fromStored = "fromNumber", toStore = "toNumber")
	protected Foo v1;
	
	public static void main(String[] args) throws Exception {
		final Ex3 ao = new Ex3();
		ao.v1 = Foo.E3;
		System.out.println(dumpData(testSerializeObject(ao, 100)));
		System.out.println(ao.v1);
	}
	
}
