package za.dsc.grp.lib.common.util.obj;

import com.thoughtworks.xstream.XStream;


public class ObjectCompareUtil
{

  public static boolean compareByValue(Object objA, Object objB)
  {
    XStream xstream = new XStream();
    String xmlObjA = xstream.toXML(objA);
    String xmlObjB = xstream.toXML(objB);
    return xmlObjA.equals(xmlObjB) ? true : false;
  }

  // TODO compare list by value
}
