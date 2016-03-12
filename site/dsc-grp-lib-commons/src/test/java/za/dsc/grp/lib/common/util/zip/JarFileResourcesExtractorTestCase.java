package za.dsc.grp.lib.common.util.zip;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;

import junit.framework.Assert;

import org.junit.Test;

import za.dsc.grp.lib.common.util.zip.JarFileResourcesExtractor;


public class JarFileResourcesExtractorTestCase
{

  @Test
  public void testGetFilteredResourceEntries()
  {
    URL url = getClass().getResource("/za/dsc/grp/common/util/zip/jar-scan-test.zip");
    String filePath = url.getFile();
    JarFileResourcesExtractor extractor = new JarFileResourcesExtractor(".txt", filePath, "jar-scan-test.zip", filePath + "target/");
    Enumeration<JarEntry> entries = null;
    try
    {
      List<String> entryList = new ArrayList<String>();
      entries = extractor.getFileEntries(filePath);
      Map<String, JarEntry> resourceEntryMap = extractor.getFilteredResourceEntries(entries);
      List<String> entryKeys = new ArrayList<String>();
      entryKeys.addAll(resourceEntryMap.keySet());
      Collections.sort(entryKeys);
      for (String entryKey : entryKeys)
      {
        JarEntry currEntry = resourceEntryMap.get(entryKey);
        String fileNamePath = currEntry.getName(); //.replaceFirst(".*\\/", "");
        entryList.add(fileNamePath);
      }
      Assert.assertTrue(entryList.size() > 0);
      Assert.assertEquals("B1/A1/111.txt", entryList.get(0));
      Assert.assertEquals("B1/A1/aaa.txt", entryList.get(1));
      Assert.assertEquals("B1/A1/zzz.txt", entryList.get(2));
      Assert.assertEquals("B1/bbb.txt", entryList.get(3));
      Assert.assertEquals("C1/text1.txt", entryList.get(4));
      Assert.assertEquals("eee.txt", entryList.get(5));
    }
    catch (IOException e)
    {
      e.printStackTrace();
      Assert.fail();
    }

  }

  @Test
  public void testExtractFiles()
  {
    URL urlFilePath = getClass().getResource("/za/dsc/grp/common/util/zip/");
    String filePath = urlFilePath.getFile();
    System.out.println(filePath);
    String fileName = "jar-scan-test.zip";
    JarFileResourcesExtractor extractor = new JarFileResourcesExtractor(".jsp", filePath, fileName, filePath);
    try
    {
      extractor.extractFiles();
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

}
