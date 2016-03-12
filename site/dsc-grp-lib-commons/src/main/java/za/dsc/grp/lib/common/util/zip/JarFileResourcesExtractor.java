package za.dsc.grp.lib.common.util.zip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;

/**
 * @author giovanni1
 */

public class JarFileResourcesExtractor
{

  public static int IO_BUFFER_SIZE = 8192;
  private String resourcePathPattern;
  private String jarPath;
  private String jarFileName;
  private String root;
  private JarFile jarFile;

  /**
   * Creates a new instance of the JarFileResourcesExtractor
   * 
   */
  public JarFileResourcesExtractor(String resourcePathPattern, String jarPath, String jarFileName, String destination)
  {
    this.resourcePathPattern = resourcePathPattern;
    this.jarPath = jarPath;
    this.jarFileName = jarFileName;
    this.root = destination;
  }

  /**
   * Extracts the resource files found in the specified jar file into the
   * destination path
   * 
   * @throws IOException If an IO error occurs when reading the jar file
   * @throws FileNotFoundException If the jar file cannot be found
   */
  //TODO IMPOROVE FILTERING
  public void extractFiles() throws IOException
  {
    try
    {
      String path = jarPath + jarFileName;
      Enumeration<JarEntry> entries = getFileEntries(path);
      Map<String, JarEntry> resourceEntryMap = getFilteredResourceEntries(entries);
      List<String> entryKeys = new ArrayList<String>();
      entryKeys.addAll(resourceEntryMap.keySet());
      Collections.sort(entryKeys);
      Set<String> dirInventory = new HashSet<String>();
      for (String entryKey : entryKeys)
      {
        System.out.println(entryKey);
        JarEntry currEntry = resourceEntryMap.get(entryKey);
        String fileNamePath = currEntry.getName().replaceFirst(".*\\/", "");
        File parentFolder = new File(root);
        // folder?
        if (StringUtils.contains(entryKey, "/"))
        {
          String[] folderHier = entryKey.split("/");
          String folderHierAccumulated = "";
          // create folder hierachy for curr file
          for (int i = 0; i < folderHier.length - 1; i++)
          {
            String currFolderName = folderHier[i];
            folderHierAccumulated += "/" + currFolderName;
            if (!dirInventory.contains(folderHierAccumulated))
            {
              dirInventory.add(folderHierAccumulated);
              File newFolder = new File(parentFolder, currFolderName);
              newFolder.mkdir();
              parentFolder = newFolder;
            }
            else
            {
              parentFolder = new File(root + currFolderName);
            }
          }
        }
        InputStream inputStream = jarFile.getInputStream(currEntry);
        File materializedJsp = new File(parentFolder, fileNamePath);
        FileOutputStream outputStream = new FileOutputStream(materializedJsp);
        copyAndClose(inputStream, outputStream);
      }
    }
    catch (MalformedURLException e)
    {
      throw new FileNotFoundException("Cannot find jar file in libs: " + jarFileName);
    }
  }

  protected void spool()
  {

  }

  protected Map<String, JarEntry> getFilteredResourceEntries(Enumeration<JarEntry> entries) throws IOException
  {
    Map<String, JarEntry> resourceEntryMap = new HashMap<String, JarEntry>();
    while (entries.hasMoreElements())
    {
      JarEntry entry = entries.nextElement();
      if (entry.getName().endsWith(resourcePathPattern))
      {
        resourceEntryMap.put(entry.getName(), entry);
      }
    }
    return resourceEntryMap;
  }

  protected Enumeration<JarEntry> getFileEntries(String path) throws IOException
  {
    Enumeration<JarEntry> entries = null;
    jarFile = new JarFile(path);
    entries = jarFile.entries();
    return entries;
  }

  protected void copyAndClose(InputStream in, OutputStream out) throws IOException
  {
    try
    {
      byte[] b = new byte[IO_BUFFER_SIZE];
      int read;
      while ((read = in.read(b)) != -1)
      {
        out.write(b, 0, read);
      }
    }
    finally
    {
      in.close();
      out.close();
    }
  }

}
