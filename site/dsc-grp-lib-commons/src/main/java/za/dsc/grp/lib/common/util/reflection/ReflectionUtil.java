package za.dsc.grp.lib.common.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import za.dsc.grp.lib.common.exception.SystemException;


public class ReflectionUtil {

  public static void setFieldValue(Object instance, String fieldName, Object fieldValue) {

    try {
      Field f = getField(instance, fieldName);
      f.setAccessible(true);
      f.set(instance, fieldValue);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Field getField(Object instance, String name) {

    if (instance == null) {
      return null;
    }
    Field f = null;
    Class target = instance.getClass();
    do {
      try {
        f = target.getDeclaredField(name);
      } catch (NoSuchFieldException e) {
        target = target.getSuperclass();
      }
    } while (f == null && target != null && !target.equals(Object.class));
    return f;
  }

  public static Object getFieldValue(Object instance, String name) {

    Field f = getField(instance, name);
    f.setAccessible(true);
    try {
      return f.get(instance);
    } catch (Exception e) {
      throw new SystemException(e);
    }
  }

  public static Collection<Field> getFields(Class<?> type) {

    Set<Field> fields = new LinkedHashSet<Field>();
    fields.addAll(Arrays.asList(type.getDeclaredFields()));
    if (type.getSuperclass() != null && !type.equals(Object.class)) {
      fields.addAll(getFields(type.getSuperclass()));
    }
    return fields;
  }

  public static Object invoke(Object target, String methodName) {

    return invoke(target, methodName, null, null);
  }

  public static Object invoke(Object target, String methodName, Class<?>[] parameterClasses, Object[] parameters) {

    Class<?> targetClass = target.getClass();
    return invoke(target, methodName, parameterClasses, parameters, targetClass);
  }

  public static Object invoke(Object target, String methodName, Class<?>[] parameterClasses, Object[] parameters, Class<?> targetClass) {

    try {


      Method method = targetClass.getDeclaredMethod(methodName, parameterClasses);
      method.setAccessible(true);

      return method.invoke(target, parameters);
    } catch (NoSuchMethodException e) {

      if (targetClass.equals(Object.class)) {

        throw new RuntimeException(e);
      }

      return invoke(target, methodName, parameterClasses, parameters, targetClass.getSuperclass());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T newInstance(Class<T> class1, Class<?>[] paramTypes, Object... params) {

    Constructor<T> constructor;
    try {
      constructor = class1.getDeclaredConstructor(paramTypes);
      constructor.setAccessible(true);

      return constructor.newInstance(params);
    } catch (Exception e) {

      throw new RuntimeException(e);
    }
  }

  public static Object createInstance(Class instanceClass, Class[] parameterTypes, Object[] parameterValues) {
    try {
      Constructor constructor = instanceClass.getDeclaredConstructor(parameterTypes);
      constructor.setAccessible(true);
      return constructor.newInstance(parameterValues);
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    throw new RuntimeException("Could not Instantiate class");
  }

  public static Object createInstance(Class instanceClass) {
    return createInstance(instanceClass, null, null);
  }

  public static Object getWrapperFromString(Class<?> type, String value) {
    if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Boolean(value) : false;
    }
    else if (Byte.TYPE.equals(type) || Byte.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Byte(value) : 0;
    }
    else if (Short.TYPE.equals(type) || Short.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Short(value) : 0;
    }
    else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Long(value) : 0;
    }
    else if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Integer(value) : 0;
    }
    else if (Float.TYPE.equals(type) || Float.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Float(value) : 0.0f;
    }
    else if (Double.TYPE.equals(type) || Double.class.equals(type)) {
      return Double.parseDouble(value);
    }
    else if (Character.TYPE.equals(type) || Character.class.equals(type)) {
      return (!StringUtils.isEmpty(value)) ? new Character((char) 0) : null;
    }
    else {
      return value;
    }
  }

  public static Object getEmptyPrimitive(Class<?> type) {
    if (Boolean.TYPE.equals(type) || Boolean.class.equals(type)) {
      return false;
    }
    else if (Byte.TYPE.equals(type) || Byte.class.equals(type)) {
      return (byte) 0;
    }
    else if (Short.TYPE.equals(type) || Short.class.equals(type)) {
      return (short) 0;
    }
    else if (Long.TYPE.equals(type) || Long.class.equals(type)) {
      return 0L;
    }
    else if (Integer.TYPE.equals(type) || Integer.class.equals(type)) {
      return 0;
    }
    else if (Float.TYPE.equals(type) || Float.class.equals(type)) {
      return 0.0;
    }
    else if (Double.TYPE.equals(type) || Double.class.equals(type)) {
      return 0.0d;
    }
    else if (Character.TYPE.equals(type) || Character.class.equals(type)) {
      return '\0';
    }
    else {
      return null;
    }
  }

  public static <T> T newInstance(Class<T> type) {
    assert (type != null);
    try {
      return type.newInstance();
    } catch (InstantiationException e) {
      throw new SystemException(e);
    } catch (IllegalAccessException e) {
      throw new SystemException(e);
    }
  }

}
