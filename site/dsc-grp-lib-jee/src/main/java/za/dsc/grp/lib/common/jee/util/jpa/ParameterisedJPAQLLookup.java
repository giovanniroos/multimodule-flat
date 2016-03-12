package za.dsc.grp.lib.common.jee.util.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import za.dsc.grp.lib.common.util.collection.KeyValue;



public class ParameterisedJPAQLLookup<T> extends JPAQLLookup<T>
{

	Map<Integer, Object> defaultParameters;

	public ParameterisedJPAQLLookup() {

		super();
	}

	protected ParameterisedJPAQLLookup(EntityManager entityManager, String query, KeyValue<Integer, ?>... defaultParameters) {

		super(entityManager, query);
		populateDefaultParameters(defaultParameters);
	}

	public ParameterisedJPAQLLookup(String query, KeyValue<Integer, ?>... defaultParameters) {

		super(query);
		populateDefaultParameters(defaultParameters);
	}

	@Override
	public Query createQuery(Object... parameters) {

		Query query = super.createQuery(parameters);
		Set<Entry<Integer, Object>> entrySet = getDefaultParameters().entrySet();
		for (Entry<Integer, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	private Map<Integer, Object> getDefaultParameters() {

		if (defaultParameters == null) {
			defaultParameters = new HashMap<Integer, Object>();
		}
		return defaultParameters;
	}

	private void populateDefaultParameters(KeyValue<Integer, ?>... defaultParameters) {

		for (KeyValue<Integer, ?> keyValue : defaultParameters) {
			getDefaultParameters().put(keyValue.getKey(), keyValue.getValue());
		}
	}

}
