package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 型またはフィールドに対して，書き込み前の検証を行うためのアノテーション<br />
 * {@link #value()} は (Lpetit/bin/store/WritableStore;)V をシグネチャに持つメソッド名
 * 
 * @author 俺用
 * @since 2014/04/26 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface WriteValidator {
	
	/**
	 * 型またはフィールドに対して，書き込み前の検証を行うためのメソッド名
	 * 
	 * @return 型またはフィールドに対して，書き込み前の検証を行うためのメソッド名
	 */
	public abstract String value() default "";
	
}
