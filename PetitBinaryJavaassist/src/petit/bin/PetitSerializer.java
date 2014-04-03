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
import petit.bin.CodeGenerator.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.anno.Struct;
import petit.bin.util.DefaultClassPool;
import petit.bin.util.KnownCtClass;
import petit.bin.util.Util;

/**
 * シリアライズクラスに対する実際の {@link Skeleton_SerializeAdapter} を得るためのファクトリ
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
public final class PetitSerializer {
	
	public static final String CONCRETE_SERIALIZER_CLASS_NAME = "$__PetitSerializerAdapter__";
	
	/**
	 * シリアライズクラスに対する実際の {@link SerializeAdapter} のマップ
	 */
	private static final Map<Class<?>, SerializeAdapter<?>> _clazz_serializers = new HashMap<Class<?>, SerializeAdapter<?>>();
	
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
		if (KnownCtClass.ISERIALIZE_ADAPTER.CT_CLAZZ == null)
			throw new NotFoundException("Cannot locate SerializeAdapter class");
		checkStructClassModifier(clazz);
		
		try {
			final CodeGenerator cg = new CodeGenerator();
			final CtClass target_clazz = DefaultClassPool.CP.get(clazz.getName());
			final CtClass adapter_clazz = target_clazz.makeNestedClass(CONCRETE_SERIALIZER_CLASS_NAME, true);
			
			cg.map("TypeTargetClass", clazz.getCanonicalName());
			
			// add interface SerializeAdapter to adapter_clazz
			adapter_clazz.addInterface(KnownCtClass.ISERIALIZE_ADAPTER.CT_CLAZZ);
			
			// add fields to adapter_clazz
			adapter_clazz.addField(Util.createPrivateFinalField(KnownCtClass.ACLASS.CT_CLAZZ, CodeFragments.VarTargetClass.ID, adapter_clazz));
			adapter_clazz.addField(Util.createPrivateFinalField(KnownCtClass.INSTANTIATOR.CT_CLAZZ, CodeFragments.VarTargetInstor.ID, adapter_clazz));
			adapter_clazz.addField(Util.createPrivateFinalField(KnownCtClass.STRUCT.CT_CLAZZ, CodeFragments.VarTargetStructAnnotation.ID, adapter_clazz));
			
			// create constructor <init>(Class) of adapter_clazz
			final CtConstructor adapter_ctor = new CtConstructor(new CtClass[] {KnownCtClass.ACLASS.CT_CLAZZ}, adapter_clazz);
			adapter_ctor.setModifiers(Modifier.PUBLIC);
			adapter_ctor.setBody(cg.replaceAll(
						"{\n" +
						"	$varTargetClass$ = $$$1;\n" +
						"	$varTargetInstor$ = $typeUtil$.getInstantiator($varTargetClass$);\n" +
						"	$varTargetStructAnnotation$ = " + clazz.getCanonicalName() + ".class.getAnnotation(" + Struct.class.getCanonicalName() + ".class);\n" +
						
						"	if ($varTargetStructAnnotation$ == null)\n" +
						"		throw new IllegalArgumentException(\"" + Struct.class.getCanonicalName() + " annotation is not present\");\n" +
						"}\n"));
			adapter_clazz.addConstructor(adapter_ctor);
			
			// add general method(s)
			adapter_clazz.addMethod(makeMethod(
					"public final Class getTargetClass() { return _clazz; }",
					adapter_clazz));
			
			// add serialization methods
			final List<CtField> managed_fields = Util.getManagedFields(DefaultClassPool.CP, clazz);
			
			// for reader
			adapter_clazz.addMethod(makeMethod(cg.replaceAll(
					"public final Object read(Object ao, $typeReader$ $varReader$) throws Exception {\n" +
						"$varReader$.pushByteOrder($varTargetStructAnnotation$.byteOrder());\n" +
						"$varReader$.pushType($varTargetClass$);\n" +
						
						makeReadFields(managed_fields, cg) +
						
						"$varReader$.popType();\n" +
						"$varReader$.popByteOrder();\n" +
						"return ao;\n" +
					"}"), adapter_clazz));
			
			adapter_clazz.addMethod(makeMethod(cg.replaceAll(
					"public final Object read($typeReader$ $varReader$) throws Exception {\n" +
							"return read($varTargetInstor$.newInstance(), $varReader$);\n" +
					"}"), adapter_clazz));
			
			// for writer
			adapter_clazz.addMethod(makeMethod(cg.replaceAll(
					"public final void write(Object ao, $typeWriter$ $varWriter$) throws Exception {\n" +
						"$varWriter$.pushByteOrder($varTargetStructAnnotation$.byteOrder());\n" +
						"$varWriter$.pushType($varTargetClass$);\n" +
						
						makeWriteFields(managed_fields, cg) +
						
						"$varWriter$.popType();\n" +
						"$varWriter$.popByteOrder();\n" +
					"}"), adapter_clazz));
			return (SerializeAdapter<?>) adapter_clazz.toClass()
					.getConstructor(Class.class)
					.newInstance(clazz);
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
	
	private static final String makeReadFields(final List<CtField> managed_fields, final CodeGenerator cg) throws CannotCompileException {
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
			sb.append(ma.makeReaderSource(field, cg)).append("\n");
		}
		cg.detachField();
		return sb.toString();
	}
	
	private static final String makeWriteFields(final List<CtField> managed_fields, final CodeGenerator cg) throws CannotCompileException {
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
			sb.append(ma.makeWriterSource(field, cg)).append("\n");
		}
		cg.detachField();
		return sb.toString();
	}
	
}
