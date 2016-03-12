package za.dsc.grp.lib.common.util.regex;

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

public class CommonRegExUtilTestCase {

  @Test
  public void testValidateEmailAddress() {
    boolean valid = CommonRegExUtil.validateEmailAddress("gio123@gmail.com");
    Assert.assertEquals(true, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio_123@gmail.com");
    Assert.assertEquals(true, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio123gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio123@.gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio123@gmail.c");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio123@.com.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress(".gio123@gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio*123@gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio.123@gmail.com");
    Assert.assertEquals(true, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio..123@gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio123.@gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio@123@gmail.com");
    Assert.assertEquals(false, valid);
    valid = CommonRegExUtil.validateEmailAddress("gio@123@gmail.co1");
    Assert.assertEquals(false, valid);
  }


}
