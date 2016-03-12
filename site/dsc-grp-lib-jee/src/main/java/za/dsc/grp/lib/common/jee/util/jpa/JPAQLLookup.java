package za.dsc.grp.lib.common.jee.util.jpa;

import javax.persistence.EntityManager;
import javax.persistence.Query;


public class JPAQLLookup<T> extends AbstractJPALookup<T>
{

	String query;

	public JPAQLLookup() {

		super();
	}

	public JPAQLLookup(EntityManager entityManager, String query) {

		super(entityManager);
		this.query = query;
	}

	public JPAQLLookup(String query) {

		super();
		this.query = query;
	}

	@Override
	protected Query resolveQuery() {

		return getEntityManager().createQuery(query);
	}
}
