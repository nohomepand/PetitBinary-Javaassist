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
	
	public abstract Class<?> storeType() default int.class;
	
	public abstract String fromStored();
	
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
