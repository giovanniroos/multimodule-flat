package za.dsc.grp.lib.common.util.zip;


import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;

public class ZipUtility
{

  public static int BUFFER_SIZE = 10240;
  private String parentFolderPath;

  public static void main(String[] args)
  {
    String tmpFP = "C:/Users/giovanni1/Downloads/archiveTest/zipThis/";
    String targetFP = "C:/Users/giovanni1/Downloads/archiveTest/myArchiveFile.zip";
    try
    {
      new ZipUtility().createArchive(new File(tmpFP), new File(targetFP));
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  public void createArchive(File sourceFolder, File targetFile) throws IOException
  {
    JarOutputStream target = null;
    target = new JarOutputStream(new FileOutputStream(targetFile));
    parentFolderPath = sourceFolder.getAbsolutePath();
    if (sourceFolder.isDirectory())
    {
      try
      {
        for (File nestedFile : sourceFolder.listFiles())
          add(nestedFile, target);
        return;
      }
      finally
      {
        target.close();
      }
    }
  }

  private void add(File source, JarOutputStream target) throws IOException
  {

    if (source.isDirectory())
    {
      String name = source.getPath().replace("\\", "/");
      if (!StringUtils.isEmpty(name))
      {
        if (!name.endsWith("/"))
          name += "/";
        String entryName = StringUtils.substring(name, parentFolderPath.length() + 1);
        JarEntry entry = new JarEntry(entryName);
        entry.setTime(source.lastModified());
        target.putNextEntry(entry);
        target.closeEntry();
      }
      for (File nestedFile : source.listFiles())
        add(nestedFile, target);
      return;
    }
    else
    {
      BufferedInputStream in = null;
      try
      {
        String name = source.getPath().replace("\\", "/");
        String entryName = StringUtils.substring(name, parentFolderPath.length() + 1);
        JarEntry entry = new JarEntry(entryName);
        entry.setTime(source.lastModified());
        target.putNextEntry(entry);
        in = new BufferedInputStream(new FileInputStream(source));
        byte[] buffer = new byte[1024];
        while (true)
        {
          int count = in.read(buffer);
          if (count == -1)
            break;
          target.write(buffer, 0, count);
        }
        target.closeEntry();
      }
      finally
      {
        if (in != null)
          in.close();
      }
    }
  }

  public void createArchive(File archiveFile, File[] tobeJared)
  {
    try
    {
      byte buffer[] = new byte[BUFFER_SIZE];
      // Open archive file
      FileOutputStream stream = new FileOutputStream(archiveFile);
      JarOutputStream out = new JarOutputStream(stream, new Manifest());

      for (int i = 0; i < tobeJared.length; i++)
      {
        if (tobeJared[i] == null || !tobeJared[i].exists() || tobeJared[i].isDirectory())
          continue; // Just in case...
        System.out.println("Adding " + tobeJared[i].getName());

        // Add archive entry
        JarEntry jarAdd = new JarEntry(tobeJared[i].getName());
        jarAdd.setTime(tobeJared[i].lastModified());
        out.putNextEntry(jarAdd);

        // Write file to archive
        FileInputStream in = new FileInputStream(tobeJared[i]);
        while (true)
        {
          int nRead = in.read(buffer, 0, buffer.length);
          if (nRead <= 0)
            break;
          out.write(buffer, 0, nRead);
        }
        in.close();
      }
      out.close();
      stream.close();
      System.out.println("Adding completed OK");
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      System.out.println("Error: " + ex.getMessage());
    }
  }
}