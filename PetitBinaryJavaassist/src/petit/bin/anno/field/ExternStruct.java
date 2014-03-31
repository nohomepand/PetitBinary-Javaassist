package petit.bin.anno.field;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;

import petit.bin.MetaAgentFactory.CodeFragments;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.util.Pair;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStruct {
	
	/**
	 * Specify a method which is used for resolving concrete class instance of the field.<br />
	 * If the value is null the concrete class is treated as the field's type.<br />
	 * The method must be defined as the following signature.
	 * <pre>
	 * [Object which extends this field's type] [method name]({@link Object}, {@link Field})
	 * </pre>
	 * 
	 * @return name of concrete class resolver method
	 */
	public abstract String value();
	
	
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(CtField field) throws CannotCompileException {
			field.getDeclaringClass().getf
			/* 俺用 at 2014/03/31 17:31:02 */
			return null;
		}
		
		@Override
		public String makeWriterSource(CtField field) throws CannotCompileException {
			/* 俺用 at 2014/03/31 17:31:03 */
			return null;
		}
		
	}
	
}
