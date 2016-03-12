package za.dsc.grp.lib.common.jee.util.ejb;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import za.dsc.grp.lib.common.exception.SystemException;


public class ServiceLocator
{
	private static final Log log = LogFactory.getLog(ServiceLocator.class);

	private static Map<String, ServiceLocator> serviceLocatorRegistry = null;
	static {
		serviceLocatorRegistry = new HashMap<String, ServiceLocator>(1);
	}

	private final String app;

	public ServiceLocator(String app) {

		super();
		this.app = app;
	}

	public static ServiceLocator getInstance(String app) {

		ServiceLocator loc = serviceLocatorRegistry.get(app);
		if (loc == null)
			synchronized (serviceLocatorRegistry) {
				loc = new ServiceLocator(app);
				serviceLocatorRegistry.put(app, loc);
			}
		return loc;
	}

	public static ServiceLocator getNewInstance(String app) {

		return new ServiceLocator(app);
	}

	public <T> T getService(Class<T> service) {

		T ejb = null;

		// This is the id for the service as known group risk. Once we get a hit, the service will be cached using this name.
		String id = new StringBuilder(app).append("/").append(service.getSimpleName()).toString();
		String path = null;

		// first get a local bean lookup...
		if (ejb == null) {
			path = "java:comp/env/" + service.getName();
			ejb = getService(service, id, path);
		}

		if (ejb == null) {
			path = "java:comp/env/" + service.getSimpleName();
			ejb = getService(service, id, path);
		}

		// according to the spec for a remote call...
		if (ejb == null) {
			path = service.getName();
			ejb = getService(service, id, path);
		}

		// try the simple name...
		if (ejb == null) {
			path = service.getSimpleName();
			ejb = getService(service, id, path);
		}

		// according to JBoss...
		if (ejb == null) {
			path = new StringBuilder(app).append("/").append(service.getSimpleName()).append("/local").toString();
			ejb = getService(service, id, path);
		}

		// according to WAS
		if (ejb == null) {
			path = "ejblocal:" + service.getName();
			ejb = getService(service, id, path);
		}
    log.info("ServiceLocator:getService:ejb: " + ejb);
    if (ejb != null) {
      log.info("ServiceLocator:getService:ejb: " + ejb.getClass().getSimpleName());
    }
		if (ejb == null) {
			throw new SystemException("failed to lookup service '" + id + "'");
		}

		return ejb;
	}

	@SuppressWarnings("unchecked")
	private <T> T getService(Class<T> service, String id, String path) {

		final ResourceRepository repo = ResourceRepository.get();
		T ejb = null;
		try {
			ejb = (T) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.debug("failed to lookup service '" + id + "' on path '" + path + "', will retry...");
		}
		return ejb;
	}

	public DataSource getDataSource(String dataSourceName) {

		DataSource ds = null;

		ResourceRepository repo = ResourceRepository.get();

		String id = dataSourceName;
		String path = null;

		try {
		  path = "java:jdbc/" + dataSourceName;
			ds = (DataSource) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.debug("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
		}

		try {
			path = "jdbc/" + dataSourceName;
			ds = (DataSource) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.debug("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
		}

		if (ds == null) {
		  path = dataSourceName;
			try {
				ds = (DataSource) repo.lookup(id, path);
			} catch (RepositoryLookupException e) {
				log.debug("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
			}
		}
		
		if (ds == null) {
      path = "java:/comp/env/jdbc/" + dataSourceName;
      try {
        ds = (DataSource) repo.lookup(id, path);
      } catch (RepositoryLookupException e) {
        log.debug("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
      }
    }

		if (ds == null) {
			throw new SystemException("failed to lookup datasource '" + id + "'");
		}

		return ds;
	}

	public ConnectionFactory getConnectionFactory(String factoryName) {

		ConnectionFactory factory = null;

		ResourceRepository repo = ResourceRepository.get();

		String id = factoryName;
		String path = null;

		try {
			path = "jms/" + factoryName;
			factory = (ConnectionFactory) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.warn("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
		}

		if (factory == null) {
			path = "java:/" + factoryName;
			try {
				factory = (ConnectionFactory) repo.lookup(id, path);
			} catch (RepositoryLookupException e) {
				log.warn("failed to lookup datasource '" + id + "' on path '" + path + "', will retry...");
			}
		}

		if (factory == null) {
			throw new SystemException("failed to lookup factory '" + id + "'");
		}

		return factory;
	}

	@SuppressWarnings("unchecked")
	public <T> T getResource(Class<T> service, String serviceName) {

		T object = null;

		ResourceRepository repo = ResourceRepository.get();

		String id = serviceName;
		String path = null;

		try {
			path = serviceName;
			object = (T) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.warn("failed to lookup resource '" + id + "' on path '" + path + "', will retry...");
		}

		try {
			path = "jms/" + serviceName;
			object = (T) repo.lookup(id, path);
		} catch (RepositoryLookupException e) {
			log.warn("failed to lookup resource '" + id + "' on path '" + path + "', will retry...");
		}

		if (object == null) {
			path = "java:/" + serviceName;
			try {
				object = (T) repo.lookup(id, path);
			} catch (RepositoryLookupException e) {
				log.warn("failed to lookup resource '" + id + "' on path '" + path + "', will retry...");
			}
		}


		if (object == null) {
			throw new SystemException("failed to lookup resource '" + id + "'");
		}

		return object;
	}

	@SuppressWarnings("unchecked")
	public <T> T getLocalService(Class<T> serviceClass) {

		T service = null;
		try {
			ResourceRepository repo = ResourceRepository.get();
			service = (T) repo.lookup("java:comp/env/" + serviceClass.getSimpleName());
			return service;
		} catch (Exception consume) {

		}

		service = getService(serviceClass);
		return service;
	}
}
