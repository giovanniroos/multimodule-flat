package za.dsc.grp.lib.common.repo;

import java.util.List;
import java.util.Map;


public interface Lookup<T>
{

	/**
	 * returns a list of T everything in represented by this lookup
	 * 
	 * @return
	 */
	List<T> list(Object... parameters);
	
	/**
	 * returns a list of T using named parameters as opposed to ordinal parameters
	 * @param parameters
	 * @return
	 */
	List<T> list(Map<String, Object> parameters);

	/**
	 * returns a list of T using named parameters as opposed to ordinal parameters
	 * @param parameters
	 * @return
	 */
	List<T> list(Map<String, Object> parameters, Integer maxResults, Integer startPos);

	/**
	 * forces a single result to be returned by the Lookup
	 * 
	 * @return
	 */
	T singleResult(Object... parameters);

	/**
	 * returns a list of T using named parameters as opposed to ordinal parameters
	 * @param parameters
	 * @return
	 */
	T singleResult(Map<String, Object> parameters);

	/**
	 * returns an iterable in order to support a forEach style syntax
	 * 
	 * @param parameters
	 * @return
	 */
	Iterable<T> forEach(Object... parameters);
	
	/**
	 * runs an update Lookup statement with the passed parameters
	 * @param parameters the parameters to pass to the underlying lookup 
	 * @return the number of affected objects
	 */
	int update(Object... parameters);
	
	/**
	 * returns a page of date from a size and offset
	 * @param size the size of the data page
	 * @param offset the offset
	 * @return and iterable of the returned data page
	 */
	Iterable<T> nextPage(int size, int offset, Object... parameters);
	

	/**
	 * returns a page of data from a size and offset
	 * @param size the amount of records to return to the page
	 * @param offset the starting point into the returned query results
	 * @param parameters, a map of parameters to map to the lookup
	 * @return
	 */
	Iterable<T> nextPage(int size, int offset, Map<String, Object> parameters);
	

}
