package za.dsc.grp.lib.common.util.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import za.dsc.grp.lib.common.util.Closure;
import za.dsc.grp.lib.common.util.DualParamClosure;
import za.dsc.grp.lib.common.util.ReturningBiClosure;
import za.dsc.grp.lib.common.util.ReturningClosure;


/**
 * A set of static methods which can be used to simplify basic tasks
 * 
 * @author juliane
 * 
 */
public class Functions
{

	/**
	 * this iterates through a collection and calls the {@link Closure#yield(Object)} for each item in the collection
	 * 
	 * @param <T>
	 *            the type for the collection you are passing
	 * @param collection
	 *            the collection for which you want to map this function to.
	 * @param closure
	 *            the closure instance you want to called
	 */
	public static <T> void map(Collection<T> collection, Closure<T> closure) {

		if (collection == null) {
			return;
		}
		for (T item : collection) {
			closure.yield(item);
		}
	}

	/**
	 * this iterates through a Map and calls the {@link Closure#yield(Object)} for each item in the collection
	 * 
	 * @param <T>
	 *            the type for the collection you are passing
	 * @param collection
	 *            the collection for which you want to map this function to.
	 * @param closure
	 *            the closure instance you want to called
	 */
	public static <K, T> void map(Map<K, T> collection, DualParamClosure<K, T> closure) {

		if (collection == null) {
			return;
		}
		Set<Entry<K, T>> entrySet = collection.entrySet();
		for (Entry<K, T> entry : entrySet) {
			closure.yield(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * this iterates through a collection and calls the {@link ReturningBiClosure#yield(Object, Object)} for each item in the collection
	 * 
	 * @param <T>
	 *            the type for the collection you are passing
	 * @param collection
	 *            the collection for which you want to map this function to.
	 * @param closure
	 *            the BiClosure instance you want to called, passing the instance in the collection and the current count in the collection
	 */

	public static <T> void map(Collection<T> collection, ReturningBiClosure<T, Integer, Void> closure) {

		if (collection == null) {
			return;
		}
		int cnt = 0;
		for (T item : collection) {
			closure.yield(item, cnt);
			cnt++;
		}
	}

	/**
	 * checks if a set of values possible matches a given value
	 * 
	 * @param testValue
	 *            the given value against which to test
	 * @param values
	 *            the values to test against the value
	 * @return
	 */
	public static boolean matches(Object testValue, Object... values) {

		boolean isTrue = false;
		for (Object object : values) {
			isTrue |= testValue.equals(object);
			// short circuit
			if (isTrue)
				return isTrue;
		}
		return isTrue;
	}

	/**
	 * ORs together a set of boolean values
	 * 
	 * @param values
	 *            a set of boolean values
	 * @return the ORed set of boolean values
	 */
	public static boolean OR(boolean... values) {

		boolean isTrue = false;
		for (boolean value : values) {
			isTrue |= value;
		}
		return isTrue;
	}

	/**
	 * Determines if a testValue matches all the values in a passed var-arg array of values
	 * 
	 * @param testValue
	 *            the test value to the values against
	 * @param values
	 *            the var-arg array of values to test against the testValue
	 * @return true if testValue equals all the values
	 */
	public static boolean matchesAll(Object testValue, Object... values) {

		boolean isTrue = true;
		for (Object object : values) {
			isTrue &= testValue.equals(object);
		}
		return isTrue;
	}

	/**
	 * ANDS together a set of boolean values
	 * 
	 * @param values
	 *            a var-arg array of boolean values to AND together
	 * @return the result of the AND operation
	 */
	public static boolean AND(boolean... values) {

		boolean isTrue = true;
		for (boolean value : values) {
			isTrue &= value;
		}
		return isTrue;
	}

	/**
	 * this determines the intersection between two collections which contain different types. This is useful for cases where we have to use the values in one collection to build a collection
	 * of one type based on the values in a collection of another type.<br/>
	 * An example would be if you are working with a database and you have a list of primary key values for a table and a list of Objects representing rows for that same table and you wish to
	 * deliver a collection of objects which represent the table corresponding to the list of primary keys.<br/>
	 * The equalityFunction can then be used to determine if the value of the collection a corresponds to the value of the collection b
	 * 
	 * @param <T>
	 *            the datatype of collection b
	 * @param <K>
	 *            the datatype of collection a and the return type of the function
	 * @param a
	 *            the first collection
	 * @param b
	 *            the second collection
	 * @param equalityFunction
	 *            the function used to evaluate the equality of object, if an instance from a == an instance from b it will be added
	 * @return the collection of elements from collection b wich intersect with collection a
	 */
	public static <K, T> Collection<K> intersection(Collection<T> a, Collection<K> b, ReturningBiClosure<K, T, Boolean> equalityFunction) {

		Collection<K> newValues = new ArrayList<K>();
		// if a or b is empty, there is no intersection
		if (a == null || a.size() == 0 || b == null || b.size() == 0) {
			return newValues;
		}
		for (K sourceInstance : b) {
			if (b == null) {
				continue;
			}
			if (contains(sourceInstance, a, equalityFunction)) {
				newValues.add(sourceInstance);
			}
		}
		return newValues;
	}

	/**
	 * this subtracts one collection from another when the collections contain two different data types. This is useful for cases where we have to use the values in one collection to build a
	 * collection of one type based on the values in a collection of another type.<br/>
	 * An example would be if you are working with a database and you have a list of primary key values for a table and a list of Objects representing rows for that same table and you wish to
	 * deliver a collection of objects which represent the table corresponding to the list of primary keys.<br/>
	 * The equalityFunction can then be used to determine if the value of the collection a corresponds to the value of the collection b
	 * 
	 * @param a
	 *            the first collection
	 * @param b
	 *            the second collection
	 * @param equalityFunction
	 *            the function used to evaluate the equality of object, if an instance from a != an instance from b it will be added
	 * @return the collection of elements from collection of values of type K which are not in collection b
	 */
	public static <K, T> Collection<K> subtraction(Collection<K> a, Collection<T> b, ReturningBiClosure<K, T, Boolean> equalityFunction) {

		Collection<K> newValues = new ArrayList<K>();
		if (b == null || b.size() == 0) {
			if (a != null) {
				newValues.addAll(a);
			}
			return newValues;
		}
		for (K sourceInstance : a) {
			if (sourceInstance == null) {
				continue;
			}
			if (!contains(sourceInstance, b, equalityFunction)) {
				newValues.add(sourceInstance);
			}
		}
		return newValues;
	}

	/**
	 * Allows you to apply a function to an value in a collection
	 * 
	 * @param <T>
	 * @param <K>
	 * @param instance
	 * @param collection
	 * @param equalityFunction
	 * @return
	 */
	public static <T, K> boolean contains(T instance, Collection<K> collection, ReturningBiClosure<T, K, Boolean> equalityFunction) {

		for (K target : collection) {
			if (equalityFunction.yield(instance, target)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Subtracts the value of one map from another
	 * 
	 * @param map1
	 *            source
	 * @param map2
	 *            subtraction
	 * @return the map of subtracted values
	 */
	@SuppressWarnings("unchecked")
	public static Map subtraction(Map map1, Map map2) {

		Map res = new HashMap<Object, Object>(map1);
		for (Iterator it = map2.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			res.remove(key);
		}
		return res;
	}
	
	/**
	 *removes values in the map, by checking if the key in the map exist in the targetKeys
	 * 
	 * @param map1
	 *            source
	 * @param map2
	 *            subtraction
	 * @return the map of subtracted values
	 */
	@SuppressWarnings("unchecked")
	public static Map removeByKeys(Map sourceMap, Set targetKeys, Collection removedEntities) {
		
		Set keys = sourceMap.keySet();
		for (Object object : keys) {
			if (!targetKeys.contains(object)) {
				removedEntities.add(sourceMap.remove(object));
			}
		}
		return sourceMap;

	}
	
	

	public  static <T> Collection<T> filter(T[] values, ReturningClosure<Boolean, T> returningClosure) {
		List<T> list = Arrays.asList(values);
		Collection<T> returnValues = filter(returningClosure, list);
		return returnValues;
	}

	private static <T> Collection<T> filter(ReturningClosure<Boolean, T> returningClosure, List<T> list) {

		Collection<T> returnValues = new ArrayList<T>(list.size());
		for (T t : list) {
			if (returningClosure.yield(t)) {
				returnValues.add(t);
			}
		}
		return returnValues;
	}
	

}
