package za.dsc.grp.lib.common.util.collection;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

import junit.framework.Assert;
import org.junit.Test;

public class SequenceTestCase {

  @Test
  public void testNext() {
    Sequence sequence = new Sequence();
    Assert.assertEquals("A", sequence.next());
    Assert.assertEquals("B", sequence.next());
  }
}
