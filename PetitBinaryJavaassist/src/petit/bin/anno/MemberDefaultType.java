package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TODO ドキュメント
 * 
 * @author 俺用
 * @since 2014/03/30 PetitBinaryJavaassist
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface MemberDefaultType {
	
	/**
	 * TODO ドキュメント
	 * フィールドアノテーションが明示的に指示されていないフィールドが，この型の場合，元のフィールドアノテーションが自動的に選択される
	 * 
	 * @return このフィールドアノテーションがデフォルトのフィールドアノテーションとなるような型
	 */
	public abstract Class<?>[] value();
	
}
