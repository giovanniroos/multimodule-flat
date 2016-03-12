package za.dsc.grp.lib.common.util.conversion;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

public class TextHtmlConverter {

  public String toHTML(String text) {
    StringBuilder sb = new StringBuilder();
    text = text.toString().replace("'", "&apos;");
    text.replace("&", "&amp;");
    text = text.replace("<", "&lt;");
    text = text.replace(">", "&gt;");
    text = text.replace("\"", "&quot;");
    String[] chunks = text.split("\n\n");
    for (String chunk : chunks) {
      chunk = "<p>" + chunk + "</p>";
      sb.append(chunk);
    }
    chunks = sb.toString().split("\n");
    sb = new StringBuilder();
    for (String chunk : chunks) {
      chunk = chunk + "</br>";
      sb.append(chunk);
    }
    StringBuilder sbOut = new StringBuilder();
    for (char c : sb.toString().toCharArray()) {
      if (c < 128) {
        sbOut.append(c);
      }
      else {
        sbOut.append("&#").append((int) c).append(";");
      }
    }
    return sbOut.toString();
  }


}
