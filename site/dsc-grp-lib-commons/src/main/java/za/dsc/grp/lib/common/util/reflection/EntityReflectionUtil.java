package za.dsc.grp.lib.common.util.reflection;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;

public class EntityReflectionUtil {
  private final Map<String, Method[]> fieldToMethodsMap = new LinkedHashMap<String, Method[]>();

  public EntityReflectionUtil(final Class<?> objGraphClass, final String[] fields) {

    this.init(objGraphClass, fields);
  }

  public EntityReflectionUtil(final Class<?> objGraphClass, final String field) {

    this.init(objGraphClass, new String[]{field});
  }

  /**
   * Creates Object that contains the Field values for a specific object graph (record).
   */
  public Object getFirstRecordValue(final Object objGraph) {

    Object recordValue = null;
    if (this.fieldToMethodsMap != null && !this.fieldToMethodsMap.isEmpty()) {
      Method[] methodArray = this.fieldToMethodsMap.values().iterator().next();
      recordValue = this.processGraphForEndObject(objGraph, methodArray);
    }
    return recordValue;
  }

  /**
   * Creates Object array that contains the Field values for a specific object graph (record).
   */
  public Object[] getRecordValues(final Object objGraph) {

    List<Object> recordValues = new ArrayList<Object>();
    for (Method[] methodArray : this.fieldToMethodsMap.values()) {
      Object fieldValue = this.processGraphForEndObject(objGraph, methodArray);
      recordValues.add(fieldValue != null ? fieldValue : "");
    }
    return recordValues.toArray();
  }

  /**
   * Initialises this utility by caching the methods to retrieve a Field's value.
   */
  private void init(final Class<?> objGraphClass, final String[] fields) {

    for (String field : fields) {
      String reducedField = this.getReducedFieldName(field);
      Method[] methods = this.processFieldForMethods(objGraphClass, reducedField);
      this.fieldToMethodsMap.put(field, methods);
    }
  }

  /**
   * Retrieving the value of a Field for a specific object graph by invoking a sequence of cached methods.
   */
  private Object processGraphForEndObject(final Object objGraph, final Method[] methods) {

    Object endObject = "";
    int i = 0;
    for (Method method : methods) {
      try {
        if (objGraph == null || endObject == null) {
          return "";
        }
        if (i == 0) {
          endObject = this.getFieldValue(objGraph, method);
        }
        else {
          endObject = this.getFieldValue(endObject, method);
        }
      } catch (Exception e) {
        LogFactory.getLog("EntityReflectionUtil").fatal("EntityReflectionUtil - processGraphForEndObject(): objGraph=" + objGraph + "\nmethod=" + method);
        endObject = "<<ERROR>>";
      }
      i++;
    }
    return endObject;
  }

  /**
   * Invoke a method on a object for a specific field and return the resulting value.
   */
  private Object getFieldValue(final Object currObj, final Method method) {

    Object endObject = "";
    if (currObj != null) {
      boolean isEnum = method.getReturnType().isEnum();
      try {
        if (isEnum) {
          Object enumObject = method.invoke(currObj);
          if (enumObject == null) {
            return "";
          }
          else {
            Method enumMethod = this.createEnumMethod(enumObject.getClass());
            endObject = enumMethod.invoke(enumObject);
          }
        }
        else {
          endObject = method.invoke(currObj);
        }
      } catch (Exception e) {
        LogFactory.getLog("EntityReflectionUtil").fatal("EntityReflectionUtil - getFieldValue(): currObj=" + currObj + "\nmethod=" + method);
        endObject = "<<ERROR>>";
      }
    }
    return endObject;
  }

  /**
   * Creates the method array needed to retrieve a Field value from a object graph.
   */
  private Method[] processFieldForMethods(final Class<?> objGraphClass, final String field) {

    String[] fieldPathEntries = field.split("\\.");
    Method[] methods = new Method[fieldPathEntries.length];
    Method method = null;
    Class<?> currClass = null;
    int i = 0;
    try {
      for (String fieldPathEntry : fieldPathEntries) {
        if (fieldPathEntries.length == 1) {// is first one AND is last one: thus the only one in array
          method = this.createMethod(objGraphClass, fieldPathEntry);
        }
        else if (fieldPathEntries.length > 1 && i == 0) {// is the first one but there is more to come
          String methodName = "get" + StringUtils.capitalize(fieldPathEntry);
          method = objGraphClass.getMethod(methodName);
          currClass = method.getReturnType();
        }
        else if (i < fieldPathEntries.length - 1) {// operate on objects
          String methodName = "get" + StringUtils.capitalize(fieldPathEntry);
          method = currClass.getMethod(methodName);
          currClass = method.getReturnType();
        }
        else if (i == fieldPathEntries.length - 1) {// get value of last method
          method = this.createMethod(currClass, fieldPathEntry);
        }
        methods[i] = method;
        i++;
      }
    } catch (Exception e) {
      LogFactory.getLog("EntityReflectionUtil")
          .fatal("EntityReflectionUtil - processFieldForMethod(): \n Class: " + objGraphClass.getName() + "\nField: " + field + " (check meta-data)");
    }
    return methods;
  }

  /**
   * Creates a method to retrieve a field value.
   */
  private Method createMethod(final Class<?> clazz, final String field) {

    Method method = null;
    try {
      String methodName = "get" + StringUtils.capitalize(field);
      method = clazz.getMethod(methodName);
    } catch (Exception e) {
      try {
        String methodName = "is" + StringUtils.capitalize(field);
        method = clazz.getMethod(methodName);
      } catch (Exception e1) {
        LogFactory.getLog("EntityReflectionUtil").fatal("EntityReflectionUtil - createMethod(): \nClass: " + clazz.getName() + "\nField: " + field + " (check meta-data)");
      }
    }
    return method;
  }

  /**
   * Creates a method for fields that return an enumeration.
   */
  private Method createEnumMethod(final Class<?> clazz) {

    Method method = null;
    try {
      method = clazz.getMethod("name");
    } catch (Exception e) {
      LogFactory.getLog("EntityReflectionUtil").fatal("BeanReflectionUtils - createEnumMethod(): \nClass: " + clazz.getName());
    }
    return method;
  }

  /**
   * This method takes care of the convention that the first part of a Field refers to the object graph eg. 'obj' in 'obj.asset.category.code'
   */
  private String getReducedFieldName(final String fieldName) {

    int sepPos = StringUtils.indexOf(fieldName, '.');
    String redusedFieldName = StringUtils.substring(fieldName, sepPos + 1);
    return redusedFieldName;
  }

  /**
   * Returning the cached method array for a specific Field.
   */
  public Method[] getFieldMethods(final String field) {

    return this.fieldToMethodsMap.get(field);
  }


}