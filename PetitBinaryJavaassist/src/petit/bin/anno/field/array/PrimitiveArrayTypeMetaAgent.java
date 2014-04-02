package petit.bin.anno.field.array;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;

/**
 * プリミティブ型の配列を読み書きするコードを生成する {@link MemberAnnotationMetaAgent}
 * 
 * @author ito
 *
 */
public abstract class PrimitiveArrayTypeMetaAgent extends MemberAnnotationMetaAgent {
	
	/**
	 * "read[STORE_METHOD_SUFFIX]();" などの接尾辞
	 */
	public final String STORE_METHOD_SUFFIX;
	
	public PrimitiveArrayTypeMetaAgent(final String store_method_suffix) {
		STORE_METHOD_SUFFIX = store_method_suffix;
	}
	
	@Override
	public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
		return cg.replaceAll(
				"{" +
				"	int size = $exprFieldSizeGetter$;" +
				"	if ($varField$ == null || $varField$.length != size)" +
				"		$varField$ = new $typeFieldComponent$[size];" +
				"	for (int i = 0; i < $varField$.length; i++)" +
				"		$varField$[i] = $varReader$.read" + STORE_METHOD_SUFFIX + "();" +
				"}");
	}
	
	@Override
	public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
		return cg.replaceAll(
				"if ($varField$ != null) {" +
				"	for (int i = 0; i < $varField$.length; i++)" +
				"		$varWriter$.write" + STORE_METHOD_SUFFIX + "($varField$[i]);" +
				"}");
	}
	
}
