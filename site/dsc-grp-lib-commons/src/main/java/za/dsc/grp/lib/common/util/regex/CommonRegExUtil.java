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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonRegExUtil {

  private static final String EMAIL_ADDRESS_REG_EXP =  "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


  public static boolean validateEmailAddress(String address) {
    Pattern pattern = Pattern.compile(EMAIL_ADDRESS_REG_EXP);
    Matcher matcher = pattern.matcher(address);
    if (matcher.matches()) {
      return true;
    }
    return false;
  }

}
