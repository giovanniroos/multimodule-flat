package za.dsc.grp.lib.common.jee.util.repo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

import javax.persistence.EntityManager;

import za.dsc.grp.lib.common.util.reflection.ReflectionUtil;


public class HomeFactory
{

	@SuppressWarnings("unchecked")
	private static final Class[] CONSTRUCTOR_SIGNATURE = new Class[] { Class.class, EntityManager.class };
	public static Class<? extends BaseInvocationHandler> INVOCATION_HANDLER = FindInvocationHandler.class;


	@SuppressWarnings("unchecked")
	public static <T extends Home<?>> T getEntityHome(Class<T> type, EntityManager entityManager) {

		if (type == null || !type.isInterface()) {
			throw new IllegalArgumentException("type must be specified and must be an interface");
		}
		ParameterizedType parameterisedType = (ParameterizedType) type.getGenericInterfaces()[0];
		Class entityType = (Class) parameterisedType.getActualTypeArguments()[0];
		BaseInvocationHandler newInstance = ReflectionUtil.newInstance(INVOCATION_HANDLER, CONSTRUCTOR_SIGNATURE, entityType, entityManager);
		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { type }, newInstance);
	}
}
