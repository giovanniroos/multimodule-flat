package za.dsc.grp.lib.common.jee.util.jpa;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.ArrayUtils;


public class JPAHelper
{

	public static Object getSingleResult(Query query) {

		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public static Query mapParameters(Query query, Map<String, Object> parameters) {

		Set<Entry<String, Object>> entrySet = parameters.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		return query;
	}

	public static Query mapParameters(Query query, Object... parameters) {

		if (!ArrayUtils.isEmpty(parameters)) {
			for (int i = 0; i < parameters.length; i++) {
				query.setParameter(i + 1, parameters[i]);
			}
		}
		return query;
	}

	public static Query createGenericQuery(EntityManager entityManager, Object entity) {

		return JPAHelper.createGenericQuery(entityManager, entity, null, (String[]) null);
	}

	public static Query createGenericQuery(EntityManager entityManager, Object entity, Map<String, QueryObject> queryList) {

		return JPAHelper.createGenericQuery(entityManager, entity, queryList, (String[]) null);
	}

	public static Query createGenericQuery(EntityManager entityManager, Object entity, String... orderByList) {

		return JPAHelper.createGenericQuery(entityManager, entity, null, orderByList);
	}

	public static String createGenericQueryString(Object entity, String additionalWhereSql, Map<String, QueryObject> queryList, String... orderByList) {

		if (entity == null) {
			return null;
		}

		StringBuilder builder = new StringBuilder("SELECT o FROM ");
		builder.append(entity.getClass().getSimpleName()).append(" o");
		// get all getters into map @TODO process non simple members.
		Map<String, Object> getterMap = JPAHelper.processGetterMethods(entity, queryList);
		if (getterMap != null && getterMap.size() > 0) {
			Iterator<String> itr = getterMap.keySet().iterator();
			for (int i = 0; i < getterMap.keySet().size(); i++) {
				String key = itr.next();
				QueryObject rowObject = null;

				if (queryList != null) {
					rowObject = queryList.get(key);
				}
				if (rowObject == null) {
					rowObject = QueryObject.DEFAULT;
				}

				if (i == 0) {
					builder.append(" WHERE");
				}
				else {
					builder.append(rowObject.getType());
				}

				builder.append(" ").append(rowObject.getLeftPrefix()).append("o.").append(key).append(rowObject.getLeftSuffix()).append(" ").append(rowObject.getOperator().getOperator())
						.append(" ").append(rowObject.getRightPrefix()).append(":").append(key).append(rowObject.getRightSuffix()).append(" ");
			}
			builder.deleteCharAt(builder.length() - 1);

		}
		if (additionalWhereSql != null) {
			builder.append(additionalWhereSql);
		}
		if (orderByList != null) {
			builder.append(" ORDER BY");
			for (String orderBy : orderByList) {
				builder.append(" o.").append(orderBy).append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static Query createGenericQuery(EntityManager entityManager, Object entity, Map<String, QueryObject> queryList, String... orderByList) {

		if (entityManager == null || entity == null) {
			return null;
		}
		Query q = null;
		String queryString = createGenericQueryString(entity, null, queryList, orderByList);
		Map<String, Object> getterMap = JPAHelper.processGetterMethods(entity, queryList);
		q = entityManager.createQuery(queryString.toString());
		if (getterMap != null && getterMap.size() > 0) {
			JPAHelper.mapParameters(q, getterMap);
		}

		return q;
	}

	public static Map<String, Object> processGetterMethods(Object task, Map<String, QueryObject> queryList) {

		Map<String, Object> returnMap = new TreeMap<String, Object>();
		Method[] methods = task.getClass().getMethods();
		for (Method method : methods) {
			if (Modifier.isPublic(method.getModifiers()) && !Modifier.isNative(method.getModifiers()) && method.getName().startsWith("get") && method.getParameterTypes().length < 1) {
				try {
					Object returnValue = method.invoke(task, (Object[]) null);
					String simpleName = getSimpleName(method.getName());
					QueryObject queryObject = (queryList == null) ? null : queryList.get(simpleName);
					boolean isIgnored = (queryObject != null && queryObject.isIgnore());
					if (returnValue != null && !isIgnored) {
						returnMap.put(simpleName, returnValue);
					}
				} catch (Throwable throwable) {
					// do nothing
				}

			}
		}
		return returnMap;
	}

	private static String getSimpleName(String name) {

		String returnName = name.substring(4, name.length());
		returnName = name.substring(3, 4).toLowerCase() + returnName;
		return returnName;
	}
}
