package za.dsc.grp.lib.common.util.string;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import za.dsc.grp.lib.common.exception.SystemException;
import za.dsc.grp.lib.common.util.collection.Functions;
import za.dsc.grp.lib.common.util.collection.KeyValue;
import za.dsc.grp.lib.common.util.reflection.ReflectionUtil;


public class ToStringBuilder
{

	private static final String BOUNDARY_DELIMITER = "|";

	public static final String DEFAULT_DELIMITER = ";";

	StringBuilder builder;
	boolean ended;
	String delimiter = DEFAULT_DELIMITER;

	public ToStringBuilder(String str, String delimiter) {

		super();
		this.delimiter = delimiter;
		this.builder = new StringBuilder(str).append(BOUNDARY_DELIMITER);
		ended = false;
	}

	public ToStringBuilder(String str) {

		this(str, DEFAULT_DELIMITER);
	}

	public ToStringBuilder(Class<?> type) {

		super();
		this.builder = new StringBuilder(type.getName()).append(BOUNDARY_DELIMITER);
	}

	public ToStringBuilder append(String fieldName, Object value) {

		if (ended) {
			throw new IllegalStateException("the end() method has already been called, you cannot append new values");
		}
		this.builder.append(fieldName).append("=").append(value).append(delimiter);
		return this;
	}

	public ToStringBuilder end() {

		if (!ended) {
			if (builder.charAt(builder.length() - 1) == delimiter.charAt(0)) {
				this.builder = this.builder.deleteCharAt(builder.length() - 1);
			}
			this.builder = this.builder.append(BOUNDARY_DELIMITER);
			ended = true;
		}
		return this;
	}

	@Override
	public String toString() {

		end();
		return builder.toString();
	}

	
	public static Object unMarshal(String value) {
		return unMarshal(value, DEFAULT_DELIMITER);
	}
	
	public static Object unMarshal(String value, String delimiter) {

		int topField = value.trim().indexOf(BOUNDARY_DELIMITER);
		String propertyList = value.substring(topField + 1, value.length() - 1);
		String[] properties = propertyList.split(delimiter);
		try {
			Object instance = Thread.currentThread().getContextClassLoader().loadClass(value.substring(0, topField)).newInstance();
			for (String propertyRepresentation : properties) {
				KeyValue<String, String> keyValue = KeyValue.fromString(propertyRepresentation);
				Object targetValue = processProperty(keyValue.getValue(), delimiter);
				try {
					Field field = ReflectionUtil.getField(instance, keyValue.getKey());
					field.setAccessible(true);
					targetValue = processProperty(keyValue.getValue(), delimiter);
					if (targetValue instanceof String) {
						targetValue =  ReflectionUtil.getWrapperFromString(field.getType(), keyValue.getValue());
					}
					field.set(instance, targetValue);
				} catch (Exception e) {
					throw new SystemException("failed to map field " + keyValue.getKey(), e);
				}

			}
			return instance;
		} catch (Exception e) {
			throw new SystemException(e);
		}

	}

	private static Object processProperty(String value, String delimiter) {

		if (StringUtils.isEmpty(value) || value.equals("null")) {
			return null;
		}
		else if (isToStringString(delimiter, value)) {
			return unMarshal(value, delimiter);
		}
		else if (value.startsWith("{") && value.endsWith("}")) {
			return unMarshalMap(value, delimiter);
		}
		else if (value.startsWith("[") && value.endsWith("]")) {
			return unMarshalList(value, delimiter);
		}
		else {
			return value;
		}
	}

	public static boolean isToStringString(String delimiter, String valueString) {

		return Functions.AND((valueString.indexOf('.') > -1), (valueString.indexOf('|') > -1),  valueString.indexOf('=') > -1, valueString
				.endsWith(BOUNDARY_DELIMITER));
	}

	static Map<String, Object> unMarshalMap(String valueString, String delimiter) {

		String fieldValueList = valueString.substring(1, valueString.length() - 1);
		String[] properties = fieldValueList.split(",");
		Map<String, Object> returnValues = new HashMap<String, Object>(properties.length + 1, 1.0f);
		try {
			for (String propertyRepresentation : properties) {
				KeyValue<String, String> keyValue = KeyValue.fromString(propertyRepresentation);
				Object targetValue = processProperty(keyValue.getValue(), delimiter);
				returnValues.put(keyValue.getKey(), targetValue);
			}
			return returnValues;
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

	private static List<Object> unMarshalList(String valueString, String delimiter) {

		String fieldValueList = valueString.trim().substring(1, valueString.length() - 1);
		String[] properties = fieldValueList.split(",");
		List<Object> returnValues = new ArrayList<Object>(properties.length);
		try {
			for (String propertyRepresentation : properties) {

				returnValues.add((propertyRepresentation != null) ? processProperty(propertyRepresentation.trim(), delimiter) : null);
			}
			return returnValues;
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}

}
