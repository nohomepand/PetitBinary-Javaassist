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

/**
 * 広義の型安全な列挙型を表す<br />
 * ある列挙型 Eがあるとして，あるフィールド F の型が E の場合，F の書き込みは Eから整数値へ変換され，
 * 読み込みは整数値から Eへ変換される．<br />
 * <br />
 * 読み込み時は， {@link #storeType()} で示される型に基づいて整数が読み取られ，
 * {@link #fromStored()} で示される，シグネチャとして (I)Ljava/lang/Object; を持つメソッドへ渡され，
 * フィールドの値としてその戻り値が割り当てられる<br />
 * <br />
 * 書き込み字は， {@link #toStore()} で示される，シグネチャとして (Ljava/lang/Object;)I を持つメソッドへ渡され，
 * その戻り値が書き込まれる<br />
 * {@link #toStore()} はデフォルト値として， {@link Enum#ordinal()} を呼び出すための "ordinal" が指定されている
 * <br />
 * 以下に C風の enumと，Java風の enumおよびフィールド上の表現の例を示す
 * <pre>
 * C:
 * enum FOO {
 *     COLOR1 = 0xc0c0c0,
 *     COLOR2 = 0xff00ff,
 *     COLOR3 = 0xffc0ff
 * }
 * 
 * Java translation of FOO:
 * public enum FOO {
 *     COLOR1(0xc0c0c0),
 *     COLOR2(0xff00ff),
 *     COLOR3(0xffc0ff);
 *     
 *     private int v;
 *     
 *     private FOO(int v) {
 *         this.v = v;
 *     }
 *     
 *     public final int toInt() {
 *         return v;
 *     }
 *     
 *     public static FOO fromInt(int i) {
 *         <i>{@literal<returns FOO instance which has i>}</i>
 *     }
 *     
 * }
 * 
 * Java translation of FOO(another way):
 * public final class FOO {
 *     public FOO COLOR1 = new FOO(0xc0c0c0);
 *     public FOO COLOR2 = new FOO(0xff00ff);
 *     public FOO COLOR3 = new FOO(0xffc0ff);
 *     
 *     private int v;
 *     
 *     private FOO(int v) {
 *         this.v = v;
 *     }
 *     
 *     public final int toInt() {
 *         return v;
 *     }
 *     
 *     public static FOO fromInt(int i) {
 *         <i>{@literal<returns FOO instance which has i>}</i>
 *     }
 * }
 * 
 * Using FOO in java:
 * @Struct
 * public class ExampleFOO {
 *     {@literal @StructMember(0)}
 *     {@literal @TypeSafeValue(storeType = int.class, fromStored = "fromInt", toStore = "toInt")}
 *     protected FOO foo;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TypeSafeValue {
	
	/**
	 * 読み書き時の整数値表現のための型の指定<br />
	 * デフォルト値として int を持つ
	 * 
	 * @return 読み書き時の整数値表現のための型
	 */
	public abstract Class<?> storeType() default int.class;
	
	/**
	 * 読み込み時に整数値表現からこのフィールドの型の値へ変換するメソッド名の指定<br />
	 * このメソッドは，引数として int型へキャスト可能な 1つの引数を受け取り，
	 * このフィールドの型へキャスト可能な戻り値を持たなければならない<br />
	 * 典型的には次のシグネチャを持つべきである
	 * <pre>
	 * // should be defined in the [field's type] class
	 * public static [field's type] [method name](int raw_value)
	 * </pre>
	 * 
	 * @return 読み込み時に整数値表現からこのフィールドの型の値へ変換するメソッド名
	 */
	public abstract String fromStored();
	
	/**
	 * 書き込み時にこのフィールドの値から整数値表現へ変換するメソッド名の指定<br />
	 * このメソッドは，フィールドの値が持つメソッドとして呼び出せる様にアクセス修飾がなされ，
	 * {@link #storeType()} 型へキャスト可能な戻り値を持たなければならない<br />
	 * 典型的には次のシグネチャを持つべきである
	 * <pre>
	 * // should be defined in the [field's type] class
	 * public [can be convert to {@link #storeType()}] [method name]()
	 * </pre>
	 * 
	 * @return
	 */
	public abstract String toStore() default "ordinal";
	
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
				final TypeSafeValue anno = (TypeSafeValue) field.getAnnotation(TypeSafeValue.class);
				if (!store_access_suffix.containsKey(anno.storeType()))
					throw new CannotCompileException(field + ": " + anno.storeType() + " must be one of " + store_access_suffix.keySet());
				
				return;
			} catch (Exception e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeReaderSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final TypeSafeValue anno = (TypeSafeValue) field.getAnnotation(TypeSafeValue.class);
				cg.map("strStoreAccessSuffix", store_access_suffix.get(anno.storeType()));
				cg.map("strFromStoredMethod", anno.fromStored());
				return cg.replaceAll(
						"$varField$ = ($typeField$) $typeField$.$strFromStoredMethod$((int) $varReader$.read$strStoreAccessSuffix$());"
						);
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
		@Override
		public String makeWriterSource(CtField field, CodeGenerator cg) throws CannotCompileException {
			try {
				final TypeSafeValue anno = (TypeSafeValue) field.getAnnotation(TypeSafeValue.class);
				cg.map("typeStore", anno.storeType().getSimpleName());
				cg.map("strStoreAccessSuffix", store_access_suffix.get(anno.storeType()));
				cg.map("strToStoreMethod", anno.toStore());
				return cg.replaceAll(
						"if ($varField$ != null) \n" +
						"	$varWriter$.write$strStoreAccessSuffix$(($typeStore$) $varField$.$strToStoreMethod$());");
			} catch (ClassNotFoundException e) {
				throw new CannotCompileException(e);
			}
		}
		
		
	}
	
}
