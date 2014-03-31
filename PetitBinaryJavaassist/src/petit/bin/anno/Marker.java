package petit.bin.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * read/write marker annotation
 * 
 * @author 俺用
 * @since 2014/03/21 PetitBinary
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Marker {
	
	/**
	 * {@link StructMember#marker()} が有効な場合に
	 * 
	 * @author 俺用
	 * @since 2014/03/20 PetitBinary
	 *
	 */
	public static enum MarkAction {
		/**
		 * mark a "reading" before reading this field.
		 */
		BEFORE_READING(".reading"),
		
		/**
		 * mark a "read" after reading this field.
		 */
		AFTER_READ(".read"),
		
		/**
		 * mark a "writing" before writing this field.
		 */
		BEFORE_WRITING(".writing"),
		
		/**
		 * mark a "written" after writing this field.
		 */
		AFTER_WRITTEN(".written");
		
		public final String MARK_OPT;
		
		private MarkAction(final String mark_opt) {
			MARK_OPT = mark_opt;
		}
		
		/**
		 * マーカを生成する
		 * 
		 * @param marker マーカの元の文字列
		 * @return マーカ
		 */
		public final String makeMarker(final String marker) {
			return marker + MARK_OPT;
		}
	}
	
	/**
	 * フィールドの位置マーカ名
	 * 
	 * @return フィールドの位置マーカ名
	 */
	public abstract String value();
	
	/**
	 * フィールドの位置マーカを書き込むタイミング
	 * 
	 * @return フィールドの位置マーカを書き込むタイミング
	 */
	public abstract MarkAction[] markAction() default {MarkAction.BEFORE_READING, MarkAction.BEFORE_WRITING};
	
}
