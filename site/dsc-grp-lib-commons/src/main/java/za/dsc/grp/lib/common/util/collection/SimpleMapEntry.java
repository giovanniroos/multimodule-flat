package za.dsc.grp.lib.common.util.collection;

import java.util.Map.Entry;


public class SimpleMapEntry<T, K> implements Entry<T, K>
{

	private T key;
	private K value;

	public SimpleMapEntry(T key, K value) {

		super();
		this.key = key;
		this.value = value;
	}

	public K setValue(K value) {

		K old = this.value;
		this.value = value;
		return old;
	}

	public K getValue() {

		return value;
	}

	public T getKey() {

		return key;
	}

	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SimpleMapEntry other = (SimpleMapEntry) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		}
		else if (!key.equals(other.key))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		}
		else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String toString() {

		StringBuilder builder = new StringBuilder();
		return builder.append(key).append("=").append(value).toString();
	}

}
