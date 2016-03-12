package za.dsc.grp.lib.common.util.collection;

import com.google.common.collect.AbstractIterator;
/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

public  class Sequence extends AbstractIterator<String> {
  private int now;
  private static char[] vs;
  static {
    vs = new char['Z' - 'A' + 1];
    for(char i='A'; i<='Z';i++) vs[i - 'A'] = i;
  }

  private StringBuilder alpha(int i){
    assert i > 0;
    char r = vs[--i % vs.length];
    int n = i / vs.length;
    return n == 0 ? new StringBuilder().append(r) : alpha(n).append(r);
  }

  @Override
  protected String computeNext() {
    return alpha(++now).toString();
  }
}
