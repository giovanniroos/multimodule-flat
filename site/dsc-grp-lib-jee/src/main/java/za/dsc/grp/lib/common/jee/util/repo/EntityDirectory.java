package za.dsc.grp.lib.common.jee.util.repo;

import java.util.HashMap;
import java.util.Map;

import za.dsc.grp.lib.common.repo.Lookup;
import za.dsc.grp.lib.common.util.generics.GenericsUtils;

public abstract class EntityDirectory<T>
{

	private static Map<Class<?>, EntityDirectory<?>> ENTITY_LISTS;
	
    Map<String, Lookup<T>> lookups;
	
	static {
		ENTITY_LISTS = new HashMap<Class<?>, EntityDirectory<?>>(20, 0.9f);
	}
	
	public EntityDirectory() {
		super();
		lookups = new HashMap<String, Lookup<T>>();
		registerLookups();
	}
	
	public static Map<Class<?>, EntityDirectory<?>> getAllLookups() {
		return ENTITY_LISTS;
	}
	
	public Map<String, Lookup<T>> getLookups() {
		return lookups;
	}

	@SuppressWarnings("unchecked")
	public static <K> EntityDirectory<K> get(Class<K> entity) {
		return (EntityDirectory<K>) ENTITY_LISTS.get(entity);
	}
	
	public static void registerEntityDirectory(EntityDirectory<?> entityList) {
		if (entityList == null) {
			throw new IllegalArgumentException("entityList cannot be null");
		}
		synchronized (ENTITY_LISTS) {
		   Class<?> type = GenericsUtils.getGenericClass(entityList.getClass(), 0); 	
		   ENTITY_LISTS.put(type, entityList);
		}
	}
	
	public Lookup<T> get(String lookupIdentifier) {
		return lookups.get(lookupIdentifier);
	}

	public Lookup<T> get(Enum<?> lookupIdentifier) {
		return get(lookupIdentifier.name());
	}
	
	
	public void registerLookup(String identifier, Lookup<T> lookup) {
		lookups.put(identifier, lookup);
	}
	
	
	public abstract void registerLookups();
	
	
}
