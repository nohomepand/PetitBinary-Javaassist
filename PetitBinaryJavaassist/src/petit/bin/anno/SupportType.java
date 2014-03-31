package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO ドキュメント
 * フィールドアノテーションの対応する型を指示する
 * 
 * @author 俺用
 * @since 2014/03/14 PetitBinarySerialization
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SupportType {
	
	/**
	 * TODO ドキュメント
	 * このフィールドアノテーションが対応する型
	 * 
	 * @return このフィールドアノテーションが対応する型
	 */
	public abstract Class<?>[] value() default {};
	
}
