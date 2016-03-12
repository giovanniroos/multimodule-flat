package za.dsc.grp.lib.common.util.generics;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public class GenericsUtils {

	@SuppressWarnings("unchecked")
	public static Class getGenericClass(Class clazz) {
		return getGenericClass(clazz, 0);
	}

	@SuppressWarnings("unchecked")
	public static Class getGenericClass(Class clazz, int index) {
		return getGenericClass(clazz, index, null);
	}

	@SuppressWarnings("unchecked")
	public static Class getGenericClass(Class clazz, Class parentClass) {
		return getGenericClass(clazz, 0, parentClass);
	}

	@SuppressWarnings("unchecked")
	public static Class getGenericClass(Class clazz, int index,
			Class parentClass) {
		Type genType = clazz.getGenericSuperclass();

		if (genType instanceof ParameterizedType) {
			Type[] params = ((ParameterizedType) genType)
					.getActualTypeArguments();

			if ((params != null) && (params.length >= (index + 1))) {
				if (parentClass != null && params.length > (index + 1)) {
					Class preClazz = (Class) params[index];
					if (isSub(preClazz, parentClass)) {
						return preClazz;
					} else {
						index++;
						return getGenericClass(clazz, index, parentClass);
					}
				} else if (params[index] instanceof Class) {
					return (Class) params[index];
				}
			}
		} else if (!clazz.equals(Object.class)) {
			return getGenericClass(clazz.getSuperclass(), index, parentClass);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static boolean isSub(Class subClass, Class supClass) {
		do {
			if (subClass.getName().equals(supClass.getName())) {
				return true;
			}
			Class[] classes = subClass.getInterfaces();
			if (classes != null) {
				for (int i = 0; i < classes.length; i++) {
					if (isSub(classes[i], supClass))
						return true;
				}
			}
			subClass = subClass.getSuperclass();
		} while (subClass != null
				&& !(subClass.getClass().equals(Object.class)));
		return false;
	}

}
