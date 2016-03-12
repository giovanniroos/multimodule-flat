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

public class PagingInfo {
  public static int PAGE_SIZE = 23;

  private long rowCount;
  private int pageNo;
  private long pageCount;
  private PageSelectionType pageSelectionType;

  public PagingInfo() {
    pageNo = 1;
    rowCount = 0;
    pageCount = 0;
    pageSelectionType = PageSelectionType.SINGLE;
  }

  public long calcPageCount() {
    long pageCount = (rowCount % PAGE_SIZE > 0) ? (rowCount / PAGE_SIZE) + 1 : rowCount / PAGE_SIZE;
    return pageCount;
  }

  public long calcPageCount(int pageSize) {
    long pageCount = (rowCount % pageSize > 0) ? (rowCount / pageSize) + 1 : rowCount / pageSize;
    return pageCount;
  }

  public int nextPage() {
    return ++pageNo;
  }

  public int previousPage() {
    return --pageNo;
  }

  public long getRowCount() {
    return rowCount;
  }

  public void setRowCount(long rowCount) {
    this.rowCount = rowCount;
  }

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public long getPageCount() {
    return pageCount;
  }

  public void setPageCount(long pageCount) {
    this.pageCount = pageCount;
  }

  public PageSelectionType getPageSelectionType() {
    return pageSelectionType;
  }

  public void setPageSelectionType(PageSelectionType pageSelectionType) {
    this.pageSelectionType = pageSelectionType;
  }
}
