package petit.bin.anno.field;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;

/**
 * プリミティブ型を読み書きするコードを生成する {@link MemberAnnotationMetaAgent}
 */
public abstract class PrimitiveTypeMetaAgent extends MemberAnnotationMetaAgent {
	
	/**
	 * "read[STORE_METHOD_SUFFIX]();" などの接尾辞
	 */
	public final String STORE_METHOD_SUFFIX;
	
	/**
	 * "write*(([WRITTING_CAST]) field);" 時のキャスト
	 */
	public final String WRITTING_CAST;
	
	/**
	 * read/write 時の型変換用の変換式(0xffff等)<br />
	 * この値が null の場合は変換式は生成されない
	 */
	public final String FOR_WIDENING;
	
	/**
	 * 初期化
	 * 
	 * @param store_method_suffix "read[READER_SUFFIX]();" などの接尾辞
	 * @param writting_cast "write*(([WRITTING_CAST]) field);" 時のキャストの型
	 * @param for_widening read/write 時の型変換用の変換式(0xffff等)
	 */
	public PrimitiveTypeMetaAgent(final String store_method_suffix, final String writting_cast, final String for_widening) {
		STORE_METHOD_SUFFIX = store_method_suffix;
		WRITTING_CAST = writting_cast;
		FOR_WIDENING = for_widening;
	}
	
	@Override
	public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
		if (FOR_WIDENING != null) {
			return cg.replaceAll("$varField$ = (" + WRITTING_CAST + ") ($varReader$.read" + STORE_METHOD_SUFFIX + "() & " + FOR_WIDENING + ");");
		} else {
			return cg.replaceAll("$varField$ = $varReader$.read" + STORE_METHOD_SUFFIX + "();");
		}
		
	}
	
	@Override
	public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
		if (FOR_WIDENING != null) {
			return cg.replaceAll("$varWriter$.write" + STORE_METHOD_SUFFIX + "((" + WRITTING_CAST + ") ($varField$ & " + FOR_WIDENING + "));");
		} else {
			return cg.replaceAll("$varWriter$.write" + STORE_METHOD_SUFFIX + "((" + WRITTING_CAST + ") $varField$);");
		}
		
	}
	
}
