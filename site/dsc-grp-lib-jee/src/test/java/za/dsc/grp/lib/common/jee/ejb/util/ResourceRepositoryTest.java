package za.dsc.grp.lib.common.jee.ejb.util;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import za.dsc.grp.lib.common.jee.util.ejb.ResourceRepository;


public class ResourceRepositoryTest
{

	@Test
	public void testLookup() throws Exception {

		ResourceRepository registry = new ResourceRepository(MockNamingContext.properties);
		MockNamingContext.get().rebind("a value", "hello world");
		String aValue = (String) registry.lookup("a value");
		assertEquals("hello world", aValue);
	}

}
