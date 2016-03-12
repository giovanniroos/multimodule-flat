package za.dsc.grp.lib.common.jee.util.jpa;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Query;

import za.dsc.grp.lib.common.util.collection.LightWeightMap;



public class NamedQueryLookup<T> extends AbstractJPALookup<T>
{

	String queryName;

	public NamedQueryLookup(EntityManager entityManager, String queryName) {

		super(entityManager);
		this.queryName = queryName;
		resolveQuery();
	}

	public NamedQueryLookup(String queryName) {

		super();
		this.queryName = queryName;
	}

	@Override
	protected Query resolveQuery() {
		
		return getEntityManager().createNamedQuery(queryName);
	}

	public static <K> Map<String, NamedQueryLookup<K>> getEntityNamedQueries(Class<K> entity) {

		if (entity == null) {
			return null;
		}
		NamedQueries queries = entity.getAnnotation(NamedQueries.class);
		if (queries == null) {
			return null;
		}
		Map<String, NamedQueryLookup<K>> lookups = new LightWeightMap<String, NamedQueryLookup<K>>(queries.value().length);
		for (NamedQuery namedQuery : queries.value()) {
			lookups.put(namedQuery.name(), new NamedQueryLookup<K>(namedQuery.name()));
		}
		return lookups;
	}

}
