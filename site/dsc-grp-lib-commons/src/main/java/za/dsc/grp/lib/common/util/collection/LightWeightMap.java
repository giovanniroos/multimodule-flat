package za.dsc.grp.lib.common.util.collection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * this is a lightweight implementation of a {@link java.util.Map}. this class
 * is designed for small map applications where many will be created which have
 * a small amount of data. An example would be an XML parser which needs to
 * store attributes for an element in a DOM.<br/>
 * <br/>
 * This class does not perform hashing and uses native Java arrays as the
 * underlying data source.<br/>
 * <br/>
 * <u>Users need to be aware of the following limitations</u> <li>It is not
 * thread safe since the use case dictates performance.</li> <li>This class will
 * perform ideally when the size is known before hand.</li> <li>
 * {@link java.util.Map#get(Object)} and
 * {@link java.util.Map#put(Object, Object)} operation performance will degrade
 * as the collection get's bigger</li> <li>when the size of the map exceeds the
 * size of passed in the constructor, the severe penalty hit will be incurred as
 * {@link System#arraycopy(Object, int, Object, int, int) will need to be
 * invoked}</li> <br/>
 * <br/>
 * 
 * @author juliane
 * @param <T> the data type assigned to the key
 * @param <K> the type data stored against the key
 */
public class LightWeightMap<T, K> implements Map<T, K>, Serializable
{

  private static final long serialVersionUID = 1L;
  private Object[][] mapArray;
  private int size = 0;

  /**
   * The <b>only</b> constructor available for this collection
   * 
   * @param noElements the number of elements
   */
  public LightWeightMap(int noElements)
  {

    super();
    mapArray = new Object[noElements][2];
  }

  public void clear()
  {

    for (Object[] o : mapArray)
    {
      o[0] = null;
      o[1] = null;
    }
    size = 0;

  }

  public boolean containsKey(Object key)
  {

    return doesArrayContainElement(key, 0);
  }

  private boolean doesArrayContainElement(Object key, int index)
  {

    boolean found = false;
    for (Object[] o : mapArray)
    {
      found = (o != null && o[index] != null && o[index].equals(key));
      if (found)
      {
        break;
      }
    }
    return found;
  }

  public boolean containsValue(Object value)
  {

    return doesArrayContainElement(value, 1);
  }

  @SuppressWarnings("unchecked")
  public Set<java.util.Map.Entry<T, K>> entrySet()
  {

    Set<java.util.Map.Entry<T, K>> entries = new HashSet<java.util.Map.Entry<T, K>>(mapArray.length + 1, 1.0f);
    for (Object[] o : mapArray)
    {
      if (o[0] != null)
      {
        entries.add((Entry<T, K>) new SimpleMapEntry<K, K>((K) o[0], (K) o[1]));
      }
    }
    return entries;
  }

  @SuppressWarnings("unchecked")
  public K get(Object key)
  {

    for (Object[] o : mapArray)
    {
      if (o[0] != null && o[0].equals(key))
      {
        return (K) o[1];
      }
    }
    return null;
  }

  public boolean isEmpty()
  {

    return (size <= 0);
  }

  @SuppressWarnings("unchecked")
  public Set<T> keySet()
  {

    Set<T> entries = new HashSet<T>(mapArray.length + 1, 1.0f);
    for (Object[] o : mapArray)
    {
      if (o[0] != null)
      {
        entries.add((T) o[0]);
      }
    }
    return entries;
  }

  @SuppressWarnings("unchecked")
  public K put(T key, K value)
  {

    if (key == null)
    {
      return null;
    }
    // two phase, find existing
    for (Object[] o : mapArray)
    {
      if (key != null && o[0] != null && o[0].equals(key))
      {
        size++;
        o[1] = value;
        return (K) o[1];
      }
    }
    for (Object[] o : mapArray)
    {
      if (o[0] == null)
      {
        o[0] = key;
        o[1] = value;
        size++;
        return null;
      }
    }
    // if we are here then it's a pain, because we have to resize the array
    Object[][] tempArray = new Object[this.mapArray.length + 1][2];
    System.arraycopy(this.mapArray, 0, tempArray, 0, this.mapArray.length);
    tempArray[tempArray.length - 1][0] = key;
    tempArray[tempArray.length - 1][1] = value;
    return null;

  }

  @SuppressWarnings("unchecked")
  public void putAll(Map m)
  {

    Set<Entry<T, K>> entries = this.entrySet();
    entries.addAll((Collection<? extends Entry<T, K>>) m.entrySet());
    // now we whack the existing array
    this.mapArray = new Object[entries.size()][2];
    int cnt = 0;
    for (Object o : entries)
    {
      Entry entry = (Entry) o;
      this.mapArray[cnt][0] = entry.getKey();
      this.mapArray[cnt][1] = entry.getValue();
      cnt++;
    }
    size = entries.size();

  }

  @SuppressWarnings("unchecked")
  public K remove(Object key)
  {

    for (Object[] o : this.mapArray)
    {
      if (o[0] != null && o[0].equals(key))
      {
        o[0] = null;
        K value = (K) o[1];
        o[1] = null;
        size--;
        return value;
      }
    }
    return null;
  }

  public int size()
  {

    return size;
  }

  @SuppressWarnings("unchecked")
  public Collection<K> values()
  {

    List<K> values = new ArrayList<K>(size());
    for (Object[] o : this.mapArray)
    {
      if (o[0] != null)
      {
        values.add((K) o[1]);
      }
    }
    return values;
  }

}
