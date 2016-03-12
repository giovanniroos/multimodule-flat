package za.dsc.grp.lib.common.jee.util.repo;

import java.lang.reflect.InvocationHandler;

import javax.persistence.EntityManager;

public abstract class BaseInvocationHandler implements InvocationHandler
{

	private Class<?> type;
	private EntityManager entityManager;

	BaseInvocationHandler(Class<?> type, EntityManager entityManager) {

		super();
		this.type = type;
		this.entityManager = entityManager;
	}

	protected Class<?> getType() {
	
		return type;
	}

	protected EntityManager getEntityManager() {
	
		return entityManager;
	}
	
	
	
	

}
