package za.dsc.grp.lib.common.jee.util.ejb;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import za.dsc.grp.lib.common.exception.SystemException;


public class ResourceRepository
{

	private static final Log log = LogFactory.getLog(ResourceRepository.class);

	private Map<String, Object> references;
	private Context naming;

	private static ResourceRepository instance;

	public static ResourceRepository get() {

		if (instance == null) {
			instance = new ResourceRepository();
		}
		return instance;
	}

	private ResourceRepository() {

		references = Collections.synchronizedMap(new HashMap<String, Object>());
		try {
			naming = new InitialContext();
      Hashtable<?, ?> environment = naming.getEnvironment();
      for (Object o : environment.keySet()) {
        System.out.println("o = " + o + ":" + environment.get(o));
      }
    } catch (NamingException e) {
			throw new SystemException(e);
		}
	}

	public ResourceRepository(Hashtable<String, String> properties) {

		references = Collections.synchronizedMap(new HashMap<String, Object>());
		try {
			naming = new InitialContext(properties);
		} catch (NamingException e) {
			throw new SystemException(e);
		}
		instance = this;
	}

	public Object lookup(String path) throws RepositoryLookupException {

		return lookup(path, path, false);
	}

	public Object lookup(String id, String path) throws RepositoryLookupException {

		return lookup(id, path, false);
	}

	public Object lookup(String id, String path, boolean forceReload) throws RepositoryLookupException {

		if (log.isDebugEnabled())
			log.debug("lookup: id= '" + id + "', path= '" + path + "', forceReload= " + forceReload + ";");

		if (!forceReload && references.containsKey(id)) {
			return references.get(id);
		}
		try {
			Object instance = naming.lookup(path);
			log.info("found resource '" + id + "' on path '" + path + "';");
			references.put(id, instance);
			return instance;
		} catch (NamingException e) {
			log.error(e);
			throw new RepositoryLookupException(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {

		close();
	}

	private void close() throws Throwable, NamingException {

		super.finalize();
		if (naming != null) {
			naming.close();
		}
	}

}
