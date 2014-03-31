package petit.bin.anno.field.array;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Field;

import javassist.CtClass;
import javassist.CtField;

import petit.bin.SerializeMember.MemberAnnotationMetaAgent;
import petit.bin.util.Pair;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExternStructArray {
	
	/**
	 * Specifies a component type resolver method which is used to resolve a concrete type of this field's component type.
	 * 
	 * @return name of the component type resolver method
	 */
	public abstract String value();
	
	public static final class _MA extends MemberAnnotationMetaAgent {
		
		@Override
		public String makeReaderSource(Pair<Class<?>, CtClass> access_base, String access_inst, Pair<Class<?>, CtField> field) {
			/* 俺用 at 2014/03/31 11:24:09 */
			return null;
		}
		
		@Override
		public String makeWriterSource(Pair<Class<?>, CtClass> access_base, String access_inst, Pair<Class<?>, CtField> field) {
			/* 俺用 at 2014/03/31 11:24:12 */
			return null;
		}
		
	}
	
	public static final class _MA extends MemberAccessor {
		
		private final ArraySizeIndicator _size_ind;
		
		private final BinaryAccessorFactory _ba_fac;
		
		private final Class<?> _component_type;
		
		@SuppressWarnings("rawtypes")
		private final BinaryAccessor _component_type_ba;
		
		private final FieldObjectInstantiator _component_instor;
		
		public _MA(final BinaryAccessorFactory ba_fac, final Field f) throws Exception {
			super(f);
			_size_ind = ArraySizeIndicator.getArraySizeIndicator(f);
			_ba_fac = ba_fac;
			_component_type = f.getType().getComponentType();
			
			final ExternStructArray esa = f.getAnnotation(ExternStructArray.class);
			if (esa != null && esa.value() != null) {
				_component_instor = FieldObjectInstantiator.getResolver(_component_type, f.getDeclaringClass(), esa.value());
				_component_type_ba = null;
			} else {
				_component_instor = FieldObjectInstantiator.getResolver(_component_type, null, null);
				_component_type_ba = ba_fac.getBinaryAccessor(_component_type);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _readFrom(SerializationContext ctx, Object inst, BinaryInput src) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final int size = _size_ind.getArraySize(src, inst, _field);
				Object ar = _field.get(inst);
				if (ar == null || Array.getLength(ar) != size)
					ar = Array.newInstance(_component_type, size);
				
				for (int i = 0; i < size; i++) {
					Object component_inst = _component_instor.getConcreteClassInstance(inst, inst, _field);
					if (component_inst == null) {
						Array.set(ar, i, null);
					} else if (_component_type_ba != null) {
						Array.set(ar, i, _component_type_ba.readFrom(ctx, component_inst, src));
					} else {
						Array.set(ar, i, _ba_fac.getBinaryAccessor(component_inst.getClass()).readFrom(ctx, component_inst, src));
					}
					
				}
				_field.set(inst, ar);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void _writeTo(SerializationContext ctx, Object inst, BinaryOutput dst) throws IOException, IllegalArgumentException, IllegalAccessException {
			try {
				final Object[] ar = (Object[]) _field.get(inst);
				if (ar == null)
					return;
				
				for (int i = 0; i < ar.length; i++) {
					if (ar[i] == null)
						continue;
					else if (_component_type_ba != null) {
						_component_type_ba.writeTo(ctx, ar[i], dst);
					} else {
						_ba_fac.getBinaryAccessor(ar[i].getClass()).writeTo(ctx, ar[i], dst);
					}
				}
//				if (_fields_type_ba == null) {
//					final BinaryAccessor ba = _ba_fac.getBinaryAccessor(obj.getClass());
//					ba.writeTo(ctx, obj, dst);
//				} else {
//					_fields_type_ba.writeTo(ctx, obj, dst);
//				}
//				for (int i = 0; i < ar.length; _component_type_ba.writeTo(ctx, ar[i++], dst));
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
		
	}
	
}
