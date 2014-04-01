package petit.bin.util;

import javassist.CtClass;
import petit.bin.SerializeAdapter;
import petit.bin.PetitSerializer;
import petit.bin.anno.Struct;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;
import petit.bin.util.instor.Instantiator;

/**
 * よく知られた {@link CtClass} の定数
 * 
 * @author 俺用
 * @since 2014/03/31 PetitBinaryJavaassist
 *
 */
public enum KnownCtClass {
	
	/**
	 * {@link Class} に対応するもの
	 */
	ACLASS(Class.class),
	
	/**
	 * {@link Struct} に対応するもの
	 */
	STRUCT(Struct.class),
	
	/**
	 * {@link ReadableStore} に対応するもの
	 */
	READABLE_STORE(ReadableStore.class),
	
	/**
	 * {@link WritableStore} に対応するもの
	 */
	WRITABLE_STORE(WritableStore.class),
	
	/**
	 * {@link Instantiator} に対応するもの
	 */
	INSTANTIATOR(Instantiator.class),
	
	/**
	 * {@link ReflectionUtil} に対応するもの
	 */
	REFLECTIONUTIL(ReflectionUtil.class),

	/**
	 * {@link PetitSerializer} のcanonical name
	 */
	SERIALIZE_ADAPTER_FACTORY(PetitSerializer.class),
	
	/**
	 * {@link SerializeAdapter} のfqn
	 */
	ISERIALIZE_ADAPTER(SerializeAdapter.class),
	;
	
	/**
	 * この値が対応する {@link Class}
	 */
	public final Class<?> CLAZZ;

	/**
	 * この値に対する {@link Class#getName()}
	 */
	public final String BINARYNAME;
	
	/**
	 * この値に対する {@link Class#getCanonicalName()}
	 */
	public final String CANONICALNAME;
	
	/**
	 * この値に対する {@link CtClass}
	 */
	public final CtClass CT_CLAZZ;
	
	private KnownCtClass(final Class<?> clazz) {
		CLAZZ = clazz;
		BINARYNAME = clazz.getName();
		CANONICALNAME = clazz.getCanonicalName();
		CT_CLAZZ = DefaultClassPool.CP.getOrNull(clazz.getName());
	}
	
}
