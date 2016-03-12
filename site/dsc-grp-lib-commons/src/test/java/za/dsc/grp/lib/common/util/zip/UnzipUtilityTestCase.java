package za.dsc.grp.lib.common.util.zip;

import java.net.URL;

import junit.framework.Assert;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import za.dsc.grp.lib.common.util.zip.UnzipUtility;


/**
 * A console application that tests the UnzipUtility class
 */

public class UnzipUtilityTestCase
{
  
  @Rule
  public TemporaryFolder folder = new TemporaryFolder();

//  @Test
  public void testUnzip()
  {
    URL url = getClass().getResource("/za/dsc/grp/common/util/zip/jar-scan-test.zip");
    String filePath = url.getFile();
    UnzipUtility unzipper = new UnzipUtility();
    try
    {
      unzipper.unzip(filePath, folder.getRoot().getAbsolutePath());
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      Assert.fail();
    }
  }

}
