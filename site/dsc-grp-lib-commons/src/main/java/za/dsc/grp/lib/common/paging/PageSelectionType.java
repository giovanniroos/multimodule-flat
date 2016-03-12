package za.dsc.grp.lib.common.paging;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

public enum PageSelectionType {
  NONE("none"), SINGLE("single"), MULTI("multi");

  private String code;

  PageSelectionType(String code) {
    this.code = code;
  }

  public String getCode() {
    return code;
  }

}