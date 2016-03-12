package za.dsc.grp.lib.common.jee.util.repo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;

import za.dsc.grp.lib.common.jee.util.jpa.JPAQLLookup;
import za.dsc.grp.lib.common.jee.util.jpa.NamedQueryLookup;
import za.dsc.grp.lib.common.repo.Lookup;
import za.dsc.grp.lib.common.util.collection.Functions;
import za.dsc.grp.lib.common.util.collection.LightWeightMap;


public class FindInvocationHandler extends BaseInvocationHandler
{

	private static final String FIND_BY = "findBy";
	protected static final String OR_OPERATOR = "Or";
	protected static final String AND_OPERATOR = "And";
	protected static final Map<String, Object> QUERY_CACHE;

	static {
		QUERY_CACHE = new HashMap<String, Object>();
	}


	protected FindInvocationHandler(Class<?> type, EntityManager entityManager) {

		super(type, entityManager);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		String name = method.getName();
		Class<?> returnType = method.getReturnType();

		Map<String, Object> parms = null;
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (parameterAnnotations != null && parameterAnnotations.length > 0) {
			int cnt = 0;
			for (Annotation[] annotations : parameterAnnotations) {
				for (Annotation nested : annotations) {
					if (nested instanceof Parm) {
						if (parms == null) {
							parms = new LightWeightMap<String, Object>(parameterAnnotations.length);
						}
						parms.put(((Parm) nested).value(), args[cnt]);
					}
				}
				cnt++;
			}
		}

		return runQuery(name, args, returnType, parms);
	}

	@SuppressWarnings("unchecked")
	protected Object runQuery(String name, Object[] args, Class<?> returnType, Map<String, Object> namedParameterAnnotations) {

		Lookup lookup = null;
		String queryName = getType().getSimpleName() + "." + name;
		boolean generated = false;
		try {
			lookup = new NamedQueryLookup(getEntityManager(), queryName);
		} catch (IllegalArgumentException e) {
			lookup = generateQuery(name, queryName);
			generated = true;
		}
		if (Collection.class.isAssignableFrom(returnType)) {
			if (namedParameterAnnotations != null && !generated) {
				return lookup.list(namedParameterAnnotations);
			}
			else {
				return lookup.list(args);
			}
		}
		else {
			if (namedParameterAnnotations != null && !generated) {
				return lookup.singleResult(namedParameterAnnotations);
			}
			else {
				return lookup.singleResult(args);
			}

		}
	}

	public Lookup<?> generateQuery(String name, String queryName) {

		// query does not exist
		if (!QUERY_CACHE.containsKey(queryName)) {
			Collection<String> fields = buildFieldList(name);
			String queryString = buildQuery(fields, getType());
			QUERY_CACHE.put(queryName, queryString);
		}
		return new JPAQLLookup<Object>(getEntityManager(), QUERY_CACHE.get(queryName).toString());
	}

	protected String buildQuery(Collection<String> fields, Class<?> type) {

		StringBuffer query = new StringBuffer("select x from ").append(type.getName()).append(" x ");
		if (fields != null) {
			if (fields.size() > 0) {
				query = query.append(" where ");
			}
			for (String string : fields) {
				if (Functions.matches(string, AND_OPERATOR.toLowerCase(), OR_OPERATOR.toLowerCase())) {
					query = query.append(" ").append(string).append(" ");
				}
				else {
					query = query.append("x.").append(string).append(" = ? ");
				}
			}
		}
		return query.toString();
	}

	protected Collection<String> buildFieldList(String name) {

		int indexOf = name.indexOf(FIND_BY);
		Collection<String> fields = new ArrayList<String>();
		if (indexOf > -1) {
			String fieldList = name.substring(FIND_BY.length());
			int andOperator = fieldList.indexOf(AND_OPERATOR);
			int orOperator = fieldList.indexOf(OR_OPERATOR);
			if (andOperator == -1 && orOperator == -1) {
				fields.add(processField(fieldList));
				fieldList = "";
			}
			while (fieldList.length() > 0) {
				andOperator = fieldList.indexOf(AND_OPERATOR);
				orOperator = fieldList.indexOf(OR_OPERATOR);
				if ((andOperator != -1 && orOperator == -1) || (orOperator != -1 && andOperator != -1 && andOperator < orOperator)) {
					fieldList = extractFieldWithOperator(fields, fieldList, andOperator, AND_OPERATOR);
				}
				else if (orOperator > -1) {
					fieldList = extractFieldWithOperator(fields, fieldList, orOperator, OR_OPERATOR);
				}
				else {
					fields.add(processField(fieldList));
					fieldList = "";
				}

			}
		}
		else {
			return null;
		}
		return fields;
	}

	private String processField(String fieldList) {

		String field = StringUtils.uncapitalize(fieldList);
		field = field.replace('_', '.');
		return field;
	}

	private String extractFieldWithOperator(Collection<String> fields, String fieldList, int operatorIndex, String operator) {

		fields.add(processField(fieldList.substring(0, operatorIndex)));
		fields.add(operator.toLowerCase());
		fieldList = fieldList.substring(operatorIndex + operator.length());
		return fieldList;
	}

}
