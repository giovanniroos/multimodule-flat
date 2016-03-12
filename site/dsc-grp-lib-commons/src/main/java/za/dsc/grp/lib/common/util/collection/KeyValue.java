package za.dsc.grp.lib.common.util.collection;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;


@SuppressWarnings("serial")
public class KeyValue<T, K> implements Serializable
{

	T key;
	K value;

	public KeyValue(T key, K value) {

		super();
		if (key == null) {
			throw new IllegalArgumentException("the key cannot be null");
		}
		this.key = key;
		this.value = value;
	}

	public KeyValue() {

		super();
	}

	public T getKey() {

		return key;
	}

	public K getValue() {

		return value;
	}

	public void setKey(T key) {

		if (key == null) {
			throw new IllegalArgumentException("the key cannot be null");
		}
		this.key = key;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final KeyValue<?, ?> that = (KeyValue<?, ?>) obj;
		return (ObjectUtils.equals(this.key, that.key) && ObjectUtils.equals(this.value, that.value));
	}

	public void setValue(K value) {

		this.value = value;
	}

	@Override
	public String toString() {

		return new StringBuilder(getClass().getName()).append("[Key=").append(key.toString()).append(",Value=").append(value).append("]").toString();
	}

	public Map<T, K> getMap() {
		Map<T, K> newMap = new HashMap<T, K>();
		newMap.put(getKey(), getValue());
		return Collections.unmodifiableMap(newMap);
	}
	
	public static KeyValue<String, String> fromString(String propertyRepresentation) {
		propertyRepresentation = propertyRepresentation.trim();
		int keyEnd = propertyRepresentation.indexOf('=');
		String key = propertyRepresentation.substring(0, keyEnd);
		String valueString = (!propertyRepresentation.endsWith("=")) ?  propertyRepresentation.substring(keyEnd+1) : null;
		return new KeyValue<String, String>(key, valueString) ;
	}
	

	


}
