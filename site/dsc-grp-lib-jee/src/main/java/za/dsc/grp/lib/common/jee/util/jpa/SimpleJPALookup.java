package za.dsc.grp.lib.common.jee.util.jpa;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;


@SuppressWarnings("unchecked")
public class SimpleJPALookup<T>
{
	private Query query;

	public SimpleJPALookup(Query query) {

		super();
		this.query = query;
	}

	public List<T> list() {

		return (List<T>) this.query.getResultList();
	}

	public List<T> list(int firstResult, int maxResults) {

		this.query.setFirstResult(firstResult);
		this.query.setMaxResults(maxResults);
		return (List<T>) this.query.getResultList();
	}

	public T singleResult() {

		T result = null;

		try {
			return result = (T) this.query.getSingleResult();
		} catch (NoResultException consume) {}

		return result;
	}

}
