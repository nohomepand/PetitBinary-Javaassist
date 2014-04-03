package petit.bin.anno.field;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.CtField;
import petit.bin.CodeGenerator;
import petit.bin.MetaAgentFactory.MemberAnnotationMetaAgent;
import petit.bin.store.ReadableStore;
import petit.bin.store.WritableStore;
import petit.bin.util.Pair;
import petit.bin.util.Util;

/**
 * 数値または順序に対応付けられた列挙型を表わす<br />
 * <br />
 * {@link #storeType()} はフィールド読み書き時の値の型を表わし，それぞれ固有のサイズで読み書きされる．
 * 例えば，{@link #storeType()} = byte.class の場合，
 * {@link ReadableStore#readInt8()} で読み込まれ，その値が {@link #enumResolver()} へ渡される．
 * また列挙値の書き込み時は {@link NumberedEnum#toNumber()} の結果が {@link WritableStore#writeInt8(byte)} へ渡される．
 * <br />
 * {@link #enumResolver()} は {@link ReadableStore} から読み取られた整数値を受け取り， このフィールドの列挙型の列挙値に変換するメソッドのメソッド名を表わす<br />
 * 典型的には列挙型内の public static なメソッドであり， <code>(I)Ljava.lang.Enum;</code> で表わされるシグネチャで定義されていなければならない． 
 * 
 * @author ito
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface EnumItem {
	
	/**
	 * {@link EnumItem} が対象とする列挙型が実装しなければならないインタフェース
	 * 
	 * @author ito
	 *
	 */
	public static interface NumberedEnum {
		
		/**
		 * この列挙値を整数値に変換する
		 * 
		 * @return この列挙値を整数値に変換した値
		 */
		public abstract int toNumber();
		
	}
	
	/**
	 * 列挙値の読み込み，および書き込み時の値の型を表わす<br />
	 * この値は byte, short, int のいずれかでなければならない
	 * 
	 * @return 列挙値の読み込み，および書き込み時の値の型
	 */
	public abstract Class<?> storeType() default int.class;
	
	/**
	 * 整数値から列挙値を解決するメソッドのメソッド名<br />
	 * メソッドは典型的には列挙型内の public static なメソッドであり， <code>(I)Ljava.lang.Enum;</code> で表わされるシグネチャで定義されていなければならない．
	 * 
	 * @return 整数値から列挙値を解決するメソッドのメソッド名
	 */
	public abstract String enumResolver();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		private static final Map<Class<?>, String> store_access_suffix;
		
		static {
			store_access_suffix = new HashMap<>();
			store_access_suffix.put(byte.class, "Int8");
			store_access_suffix.put(short.class, "Int16");
			store_access_suffix.put(int.class, "Int32");
		}
		
		@Override
		public void checkField(CtField field) throws CannotCompileException {
			try {
				final Pair<Class<?>, Boolean> typeField = Util.toClass(field.getType());
				if (!Enum.class.isAssignableFrom(typeField.FIRST) || !NumberedEnum.class.isAssignableFrom(typeField.FIRST))
					throw new CannotCompileException("Field type must be a sub-class of Enum and implemented " + NumberedEnum.class.getCanonicalName() + ": " + field);
				
				final EnumItem anno = (EnumItem) field.getAnnotation(EnumItem.class);
				if (!store_access_suffix.containsKey(anno.storeType()))
					throw new CannotCompileException(anno.storeType() + " is one of " + store_access_suffix.keySet());
				
				return;
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final EnumItem anno = (EnumItem) field.getAnnotation(EnumItem.class);
				cg.map("strStoreAccessSuffix", store_access_suffix.get(anno.storeType()));
				cg.map("strEnumItemResolver", anno.enumResolver());			
				return cg.replaceAll(
						"$varField$ = ($typeField$) $typeField$.$strEnumItemResolver$((int) $varReader$.read$strStoreAccessSuffix$());"
						);
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final EnumItem anno = (EnumItem) field.getAnnotation(EnumItem.class);
				cg.map("typeNumberedEnum", NumberedEnum.class.getCanonicalName());
				cg.map("typeStore", anno.storeType().getSimpleName());
				cg.map("strStoreAccessSuffix", store_access_suffix.get(anno.storeType()));
				cg.map("strEnumItemResolver", anno.enumResolver());
				return cg.replaceAll(
						"if ($varField$ != null) \n" +
						"	$varWriter$.write$strStoreAccessSuffix$(($typeStore$) (($typeNumberedEnum$) $varField$).toNumber());");
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
	}
	
}
