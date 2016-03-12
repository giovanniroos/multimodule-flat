package za.dsc.grp.lib.common.jee.ejb.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;


public class MockNamingContext implements Context, InitialContextFactory
{

	Map<String, Object> map = new HashMap<String, Object>();

	public static final Hashtable<String, String> properties;
	static {
		properties = new Hashtable<String, String>();
		properties.put(Context.INITIAL_CONTEXT_FACTORY, MockNamingContext.class.getName());
	}

	static MockNamingContext instance;

	public MockNamingContext() {

		super();
	}

	public static void reset() {

		instance = null;
	}

	public static Context get() {

		if (instance == null) {
			instance = new MockNamingContext();
		}
		return instance;
	}

	public Context getInitialContext(Hashtable<?, ?> environment) {

		return MockNamingContext.get();
	}

	public Object addToEnvironment(String propName, Object propVal) throws NamingException {

		return null;
	}

	public void bind(Name name, Object obj) throws NamingException {

	}

	public void bind(String name, Object obj) throws NamingException {

		rebind(name, obj);
	}

	public void close() throws NamingException {

	}

	public Name composeName(Name name, Name prefix) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public String composeName(String name, String prefix) {

		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public Context createSubcontext(String name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void destroySubcontext(String name) throws NamingException {

		throw new UnsupportedOperationException();

	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {

		return null;
	}

	public String getNameInNamespace() throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NameParser getNameParser(String name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<NameClassPair> list(String name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public NamingEnumeration<Binding> listBindings(String name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public Object lookup(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public Object lookup(String name) throws NamingException {

		if (!map.containsKey(name)) {
			throw new NameNotFoundException(name + " does not exist");
		}
		return map.get(name);
	}

	public Object lookupLink(Name name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public Object lookupLink(String name) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void rebind(Name name, Object obj) throws NamingException {

		throw new UnsupportedOperationException();

	}

	public void rebind(String name, Object obj) throws NamingException {

		map.put(name, obj);

	}

	public Object removeFromEnvironment(String propName) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void rename(Name oldName, Name newName) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void rename(String oldName, String newName) throws NamingException {

		throw new UnsupportedOperationException();
	}

	public void unbind(Name name) throws NamingException {

		throw new UnsupportedOperationException();

	}

	public void unbind(String name) throws NamingException {

		throw new UnsupportedOperationException();

	}

}
