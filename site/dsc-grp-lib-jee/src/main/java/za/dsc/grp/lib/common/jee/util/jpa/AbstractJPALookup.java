package za.dsc.grp.lib.common.jee.util.jpa;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;


public abstract class AbstractJPALookup<T> implements JPALookup<T>
{

	EntityManager entityManager;

	public AbstractJPALookup() {

		super();
	}

	public AbstractJPALookup(EntityManager entityManager) {

		super();
		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public List<T> list(Object... parameters) {

		return createQuery(parameters).getResultList();
	}

	public Query createQuery(Object... parameters) {

		return JPAHelper.mapParameters(resolveQuery(), parameters);
	}

	protected Query createQuery(Map<String, Object> parameters) {

		return JPAHelper.mapParameters(resolveQuery(), parameters);
	}

	@SuppressWarnings("unchecked")
	public T singleResult(Object... parameters) {

		return (T) JPAHelper.getSingleResult(this.createQuery(parameters));
	}

	//@Override
	@SuppressWarnings("unchecked")
	public T singleResult(Map<String, Object> parameters) {

		return (T) JPAHelper.getSingleResult(this.createQuery(parameters));
	}

	@SuppressWarnings("unchecked")
	public Iterable<T> forEach(Object... parameters) {

		return (Iterable<T>) createQuery(parameters).getResultList().iterator();
	}
	
	//@Override
	public int update(Object... parameters) {
		return createQuery(parameters).executeUpdate();
	}
	
	@SuppressWarnings("unchecked")
	//@Override
	public Iterable<T> nextPage(int size, int offset, Object... parameters) {
		Query query = createQuery(parameters);
		query.setFirstResult(offset);
		query.setMaxResults(size);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public Iterable<T> nextPage(int size, int offset,Map<String, Object> parameters) {
		Query query = createQuery(parameters);
		query.setFirstResult(offset);
		query.setMaxResults(size);
 		return query.getResultList();
//	@Override
	}

	@SuppressWarnings("unchecked")
	//@Override
	public List<T> list(Map<String, Object> parameters, Integer maxResults, Integer startPos) {

		Query query = createQuery(parameters);
		if (maxResults != null) {
			query.setMaxResults(maxResults);
		}
		if (startPos != null) {
			query.setFirstResult(startPos);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	//@Override
	public List<T> list(Map<String, Object> parameters) {

		return createQuery(parameters).getResultList();
	}

	protected abstract Query resolveQuery();

	EntityManager getEntityManager() {

		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {

		this.entityManager = entityManager;
	}

}
