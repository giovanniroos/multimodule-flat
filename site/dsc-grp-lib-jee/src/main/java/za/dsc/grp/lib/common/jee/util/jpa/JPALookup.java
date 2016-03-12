package za.dsc.grp.lib.common.jee.util.jpa;

import javax.persistence.EntityManager;

import za.dsc.grp.lib.common.repo.Lookup;

public interface JPALookup<T> extends Lookup<T>
{
	
	void setEntityManager(EntityManager entityManager);

}
