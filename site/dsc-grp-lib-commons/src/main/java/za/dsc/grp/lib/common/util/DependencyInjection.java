package za.dsc.grp.lib.common.util;

import za.dsc.grp.lib.common.util.reflection.ReflectionUtil;


public class DependencyInjection
{

	Object ejb;

	public DependencyInjection(Object ejb) {

		super();
		this.ejb = ejb;
	}

	public Object getFieldValue(String name) {

		return ReflectionUtil.getFieldValue(ejb, name);
	}

	public DependencyInjection inject(String name, Object value) {

		ReflectionUtil.setFieldValue(ejb, name, value);
		return this;
	}

	public DependencyInjection injectEntityManager(Object value) {

		return inject("entityManager", value);
	}

	public DependencyInjection injectDirService(Object value) {

		return inject("directoryService", value);
	}

	public Object getEjb() {

		return ejb;
	}

}
