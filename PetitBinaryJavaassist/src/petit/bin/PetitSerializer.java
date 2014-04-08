package petit.bin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.util.DefaultClassPool;
import petit.bin.util.KnownCtClass;
import petit.bin.util.Util;

/**
 * シリアライズクラスに対する実際の {@link SerializeAdapter} を得るためのファクトリ
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public final class PetitSerializer {
	
	/**
	 * 生成される内部クラスのための名前
	 */
	public static final String CONCRETE_SERIALIZER_CLASS_NAME = "$$psa__$";
//	public static final String CONCRETE_SERIALIZER_CLASS_NAME = "$__PetitSerializerAdapter__";
	
	/**
	 * シリアライズクラスに対する実際の {@link SerializeAdapter} のマップ
	 */
	private static final Map<Class<?>, SerializeAdapter<?>> _clazz_serializers = new HashMap<Class<?>, SerializeAdapter<?>>();
	
	/**
	 * シリアライズクラスに対する実際の {@link SerializeAdapter} を得る
	 * 
	 * @param clazz 対象のクラス
	 * @return 対象のクラスに対する {@link SerializeAdapter}
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	@SuppressWarnings("unchecked")
	public static final <T> SerializeAdapter<T> getSerializer(final Class<T> clazz) throws CannotCompileException, NotFoundException {
		SerializeAdapter<?> maybe_null = _clazz_serializers.get(clazz);
		if (maybe_null == null) {
			maybe_null = createSerializeAdapter(clazz);
			_clazz_serializers.put(clazz, maybe_null);
		}
		return (SerializeAdapter<T>) maybe_null;
	}
	
	private static final void checkStructClassModifier(final Class<?> clazz) throws CannotCompileException {
		final int mod = clazz.getModifiers();
//		if ((mod & java.lang.reflect.Modifier.FINAL) != 0)
//			throw new CannotCompileException(clazz.getCanonicalName() +  ": final class is not supported");
		if ((mod & java.lang.reflect.Modifier.INTERFACE) != 0)
			throw new CannotCompileException(clazz.getCanonicalName() +  ": interface type is not supported");
		if (clazz.isMemberClass() && (mod & java.lang.reflect.Modifier.STATIC) == 0)
			throw new CannotCompileException(clazz.getCanonicalName() +  ": non-static member class is not supported");
	}
	
	@SuppressWarnings("unchecked")
	private static final SerializeAdapter<?> createSerializeAdapter(final Class<?> clazz) throws CannotCompileException, NotFoundException {
		if (KnownCtClass.SERIALIZE_ADAPTER.CT_CLAZZ == null)
			throw new NotFoundException("Cannot locate SerializeAdapter class");
		checkStructClassModifier(clazz);
		
		try {
			final CodeGenerator cg = new CodeGenerator();
			final CtClass target_clazz = DefaultClassPool.CP.get(clazz.getName());
			final CtClass adapter_clazz = target_clazz.makeNestedClass(CONCRETE_SERIALIZER_CLASS_NAME, true);
			final List<CtField> managed_fields = Util.getManagedFields(DefaultClassPool.CP, clazz);
			
			if (managed_fields.isEmpty()) {
				return new SerializeAdapter.NullFieldSerializeAdapter<>(clazz);
			} else {
				cg.map("TypeTargetClass", clazz.getCanonicalName());
				
				// add interface SerializeAdapter to adapter_clazz
				adapter_clazz.setSuperclass(KnownCtClass.SERIALIZE_ADAPTER.CT_CLAZZ);
				
				// create constructor <init>(Class) of adapter_clazz
				final CtConstructor adapter_ctor = new CtConstructor(new CtClass[] {KnownCtClass.ACLASS.CT_CLAZZ}, adapter_clazz);
				adapter_ctor.setModifiers(Modifier.PUBLIC);
				adapter_ctor.setBody(cg.replaceAll(
							"{\n" +
							"	super($$$1);\n" +
							"}"));
				adapter_clazz.addConstructor(adapter_ctor);
				
				// add serialization methods
				
				// for reader
				adapter_clazz.addMethod(makeMethod(cg.replaceAll(
						"public final Object read(Object ao, $typeReader$ $varReader$) throws Exception {\n" +
							"$varReader$.pushByteOrder($varTargetStructAnnotation$.byteOrder());\n" +
							"$varReader$.pushType($varTargetClass$);\n" +
							
							makeReadFields(adapter_clazz, managed_fields, cg) +
							
							"$varReader$.popType();\n" +
							"$varReader$.popByteOrder();\n" +
							"return ao;\n" +
						"}"), adapter_clazz));
				
				// for writer
				adapter_clazz.addMethod(makeMethod(cg.replaceAll(
						"public final void write(Object ao, $TypeWriter$ $varWriter$) throws Exception {\n" +
							"$varWriter$.pushByteOrder($varTargetStructAnnotation$.byteOrder());\n" +
							"$varWriter$.pushType($varTargetClass$);\n" +
							
							makeWriteFields(adapter_clazz, managed_fields, cg) +
							
							"$varWriter$.popType();\n" +
							"$varWriter$.popByteOrder();\n" +
						"}"), adapter_clazz));
				
				return (SerializeAdapter<?>) adapter_clazz.toClass()
						.getConstructor(Class.class)
						.newInstance(clazz);
			}
		} catch (CannotCompileException e) {
			throw e;
		} catch (Exception e) {
			throw new CannotCompileException(e);
		}
		
	}
	
	private static final CtMethod makeMethod(final String code, final CtClass decl) throws CannotCompileException {
		try {
			return CtMethod.make(code, decl);
		} catch (CannotCompileException e) {
			System.err.println(code);
			e.printStackTrace();
			throw e;
		}
	}
	
	private static final String makeReadFields(final CtClass adapter_clazz, final List<CtField> managed_fields, final CodeGenerator cg) throws CannotCompileException {
		final StringBuilder sb = new StringBuilder();
		
		/*
		 * <target_class> <ACCESS_INSTANCE.ID> = (<target_class>) ao;
		 * <read elements>
		 */
		sb.append(cg.replaceAll("$typeTargetClass$ $varTarget$ = ($typeTargetClass$) ao;")).append("\n");
		for (final CtField field : managed_fields) {
			final MemberAnnotationMetaAgent ma = MetaAgentFactory.getMetaAgent(field);
			if (ma == null)
				throw new UnsupportedOperationException("Cannot find meta-agent for " + field + " (MAY BE BUG)");
			ma.checkField(field);
			cg.attachField(field);
			sb.append(ma.makeReaderSource(adapter_clazz, field, cg)).append("\n");
		}
		cg.detachField();
		return sb.toString();
	}
	
	private static final String makeWriteFields(final CtClass adapter_clazz, final List<CtField> managed_fields, final CodeGenerator cg) throws CannotCompileException {
		final StringBuilder sb = new StringBuilder();
		
		/*
		 * <target_class> <ACCESS_INSTANCE.ID> = (<target_class>) ao;
		 * <write elements>
		 */
		sb.append(cg.replaceAll("$typeTargetClass$ $varTarget$ = ($typeTargetClass$) ao;")).append("\n");
		for (final CtField field : managed_fields) {
			final MemberAnnotationMetaAgent ma = MetaAgentFactory.getMetaAgent(field);
			if (ma == null)
				throw new UnsupportedOperationException("Cannot find meta-agent for " + field + " (MAY BE BUG)");
			ma.checkField(field);
			cg.attachField(field);
			sb.append(ma.makeWriterSource(adapter_clazz, field, cg)).append("\n");
		}
		cg.detachField();
		return sb.toString();
	}
	
}
