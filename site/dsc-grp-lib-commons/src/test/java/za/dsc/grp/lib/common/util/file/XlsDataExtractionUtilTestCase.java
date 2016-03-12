package za.dsc.grp.lib.common.util.file;

/*
* Copyright (c) Discovery Holdings Ltd. All Rights Reserved.
*
* This software is the confidential and proprietary information of
* Discovery Holdings Ltd ("Confidential Information").
* It may not be copied or reproduced in any manner without the express
* written permission of Discovery Holdings Ltd.
*
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import za.dsc.grp.lib.common.types.CsvFieldDataType;
import za.dsc.grp.lib.common.types.FileType;

public class XlsDataExtractionUtilTestCase {

  public static final String COLUMN_COUNT_MISMATCH_INPUT_01 = "/column-count-mismatch-input01.xls";
  public static final String COLUMN_COUNT_MISMATCH_INPUT_02 = "/column-count-mismatch-input02.xlsx";
  public static final String COLUMN_NAME_ORDER_MISMATCH_INPUT_01 = "/column-order-mismatch-input01.xlsx";
  public static final String COLUMN_EMPTY_AT_END_01 = "/column-empty-end-input01.xlsx";
  public static final String ROWS_EMPTY_AT_END_INPUT01 = "/rows-empty-end-input01.xlsx";
  public static final String ROWS_EMPTY_AT_END_OUTPUT01 = "/rows-empty-end-output01.csv";
  public static final String ROWS_EMPTY_AT_END_OUTPUT_EXPECTED01 = "/rows-empty-end-output-expected01.csv";
  public static final String SCHEME_2421_INPUT01 = "/scheme-2421-input01.xlsx";
  public static final String SAMPLE_DATA_1 = "/sample-data-1.xlsx";
  public static final String SCHEME_2421_OUTPUT01 = "/scheme-2421-output01.csv";
  public static final String SCHEME_2421_OUTPUT_EXPECTED01 = "/scheme-2421-output-expected01.csv";
  public static final String HEADERS = "/headers.xlsx";
  //  public static final String SCHEME_X_INPUT02 = "/scheme-x-input02.xlsx";
//  public static final String SCHEME_X_OUTPUT02 = "/scheme-x-output02.csv";
  public static final String DATA_RANGE_INPUT01 = "/csv-data-rangeinput.csv";
  public static final String DATA_RANGE_INPUT_XLS_01 = "/6600004090-delta.xls";
  public static final String CELL_VALUE_INPUT_XLS_01 = "/cell-value-test.xls";
  public static final String EMPTY_SS_INPUT01 = "/empty-ss01-input01.xlsx";
  public static final String HEADER_DATA = "/headers-data.xls";
  // SUPPORT1 CSV to XLS
  public static final String SUPPORT_IN1 = "/6600001657-July-2012.csv";
  public static final String SUPPORT_OUT1 = "/6600001657-July-2012.xls";
  // SUPPORT2 XLS to CSV
  public static final String SUPPORT_IN2 = "/6600001657-July-2012-INPUT.xls";
  public static final String SUPPORT_OUT2 = "/6600001657-July-2012-OUTPUT.csv";
  public static final String SUPPORT_IN3 = "/6600002135-April-2012-INPUT.xls";
  public static final String SUPPORT_OUT3 = "/6600002135-April-2012-OUTPUT.csv";
  public static final String SUPPORT_IN4 = "/6600003474-OCT-2012.csv";
  public static final String SUPPORT_OUT4 = "/6600003474-OCT-2012.xls";
  // SUPPORT2 XLS to CSV
  public static final String SUPPORT_IN5 = "/Lizelle-L_DGR_AIB_System Extract Dec.xlsx";
  public static final String SUPPORT_OUT5 = "/Lizelle-L_DGR_AIB_System Extract Dec.csv";
  // ===1158
  public static final String SUPPORT_IN1158 = "/6600001158-input.xls";
  public static final String SUPPORT_OUT1158 = "/6600001158-output.csv";
  // HMS.xls
  public static final String SUPPORT_IN6 = "/HMS.xls";
  public static final String SUPPORT_OUT6 = "/HMS.csv";
  public static final String DISTINCT1 = "/6600003765-member-August-2013.xls";
  public static final String EMPTY_1ST_ROW = "/6600002610-July-2013.xlsx";
  public static final String EMPTY_1ST_ROW_CSV_GENERATED = "/6600002610-July-2013-Generated.csv";
  public static final String EMPTY_1ST_ROW_CSV_EXPECTED = "/6600002610-July-2013-Expected.csv";
  private static final String DECIMAL_PAYROLL_NUMBER_INPUT = "/1064-04-2013_28-08-2013_input.xls";
  private static final String DECIMAL_PAYROLL_NUMBER_GENERATED_OUTPUT = "/1064-04-2013_28-08-2013_generated_output.csv";
  private static final String DECIMAL_PAYROLL_NUMBER_EXPECTED_OUTPUT = "/1064-04-2013_28-08-2013_expected_output.csv";
  private static final String IGNORE_COL_MID_INPUT = "/ignore-middle-col-input.xls";
  private static final String IGNORE_COL_MID_OUTPUT_EXP = "/ignore-middle-col-output-expected.csv";
  private static final String IGNORE_COL_MID_OUTPUT_GEN_EXP = "/ignore-middle-col-output-generated.csv";
  private static final String DISTINCT_COLUMN_VALUE = "/6600001080.xls";

//    @Test
  public void testColCountMismatch01() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes = null;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream
          (COLUMN_COUNT_MISMATCH_INPUT_01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail("Is valid file: This test must pass.");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

//    @Test
  public void testColCountMismatch02() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = colCountMismatch02DataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream
          (COLUMN_COUNT_MISMATCH_INPUT_02);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail("Must fail: column count discrepancy.");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> colCountMismatch02DataType() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.CURRENCY);
    return dataType;
  }

  // 6600003474-OCT-2012.xls

//    @Test
  public void testColumnOrderMismatch01() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes = null;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream
          (COLUMN_NAME_ORDER_MISMATCH_INPUT_01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail("Must fail: column order discrepancy.");
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (IllegalStateException e) {
      System.out.println(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

//    @Test
  public void testEmptyColumnsAtEnd() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(COLUMN_EMPTY_AT_END_01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail("Should not fail. Empty columns is handled in getHeaders(Workbook workbook)");
      }
      conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

//    @Test
  public void testEmptySpreadSheet() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(EMPTY_SS_INPUT01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
    } catch (IllegalStateException e) {
      if (e.getMessage().equals("null header row in upload file")) {
        System.out.println("null header row in upload file");
      }
      else {
        Assert.fail();
      }
    }
  }

  /**
   * header on line 3
   * empty rows in data
   */
//  @Test
  public void testSpesifiedHeaderLine() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createHeadersDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap, 3);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(HEADER_DATA);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      String csvString = new String(csvBytes);
      //todo add asserts
      System.out.println(csvString);
    } catch (Exception e) {
      if (!e.getMessage().equals("Spreadsheet does not seem to contain any information")) {
        e.printStackTrace();
        Assert.fail();
      }
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createHeadersDataType() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("aaa", CsvFieldDataType.STRING);
    dataType.put("bbb", CsvFieldDataType.STRING);
    dataType.put("ccc", CsvFieldDataType.STRING);
    dataType.put("ddd", CsvFieldDataType.STRING);
    return dataType;
  }

  //  @Test
  public void testIgnoreColumns() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createIgnoreColsDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap, 3);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(HEADER_DATA);
      xlsBytes = IOUtils.toByteArray(inputStream);
      Workbook workbook = conversion.createWorkbook(FileType.XLS, xlsBytes);
      conversion.setWorkbook(workbook);
      conversion.createHeaderNameIndex();
      List<List<Cell>> dataGrid = conversion.createCellGrid(workbook);
      System.out.println();
      //todo add asserts
    } catch (Exception e) {
      if (!e.getMessage().equals("xxxxx")) {
        e.printStackTrace();
        Assert.fail();
      }
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createIgnoreColsDataType() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("aaa", CsvFieldDataType.STRING);
    dataType.put("bbb", CsvFieldDataType.STRING);
    dataType.put("ddd", CsvFieldDataType.STRING);
    return dataType;
  }

  //  @Test
  public void testEmptyRowsAtEnd() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType();
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(ROWS_EMPTY_AT_END_INPUT01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail();
      }
      FileOutputStream outStream = new FileOutputStream(XlsDataExtractionUtilTestCase.ROWS_EMPTY_AT_END_OUTPUT01);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      File csvOutput = new File(XlsDataExtractionUtilTestCase.ROWS_EMPTY_AT_END_OUTPUT01);
      File csvExpected = new File(XlsDataExtractionUtilTestCase.ROWS_EMPTY_AT_END_OUTPUT_EXPECTED01);
      boolean b = FileUtils.contentEqualsIgnoreEOL(csvOutput, csvExpected, null);
      if (!b) {
        Assert.fail("The output file and expected output must be the same.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

//  @Test
//  public void testSpesifiedHeaderLine() throws Exception {
//    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createHeadersDataType();
//    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap, 3);
//    byte[] xlsBytes;
//    try {
//      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(HEADER_DATA);
//      xlsBytes = IOUtils.toByteArray(inputStream);
//      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
//      String csvString = new String(csvBytes);
//      //todo add asserts
//      System.out.println(csvString);
//    } catch (Exception e) {
//      if (!e.getMessage().equals("Spreadsheet does not seem to contain any information")) {
//        e.printStackTrace();
//        Assert.fail();
//      }
//    }
//  }

  //  @Test
  public void testConvertXlsToCsv01() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = convertXlsToCsv01DataType();
    File xlsInput = new File(XlsDataExtractionUtilTestCase.SCHEME_2421_INPUT01);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(HEADER_DATA);
      xlsBytes = IOUtils.toByteArray(inputStream);
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      String csvString = new String(csvBytes);
      //todo add asserts
      System.out.println(csvString);
    } catch (Exception e) {
      if (!e.getMessage().equals("Spreadsheet does not seem to contain any information")) {
        e.printStackTrace();
        Assert.fail();
      }
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> convertXlsToCsv01DataType() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.CURRENCY);
    return dataType;
  }

  //  @Test
  public void testConvertCsvToXlsSupport1() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport();
    File csvInput = new File(XlsDataExtractionUtilTestCase.SUPPORT_IN1);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] csvBytes;
    try {
      csvBytes = IOUtils.toByteArray(new FileInputStream(csvInput));
      // boolean valid = conversion.validate(FileType.XLSX, csvBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] xlsBytes = conversion.convertCsvToXls(csvBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT1);
      outStream.write(xlsBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  //  @Test
  public void testConvertXlsToCsvSupport2() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport2();
    File xlsInput = new File(SUPPORT_IN2);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT2);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("ICB Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("UGLA Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("RFC Waiver Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.DECIMAL);
    dataType.put("Flexible SIB (Comp) Amount", CsvFieldDataType.DECIMAL);
    return dataType;
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport2() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("ICB Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("UGLA Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("RFC Waiver Sum Assured", CsvFieldDataType.CURRENCY);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.DECIMAL);
    dataType.put("Flexible SIB (Comp) Amount", CsvFieldDataType.DECIMAL);
    return dataType;
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataType() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.CURRENCY);
    return dataType;
  }

  //  @Test
  public void testConvertXlsToCsvSupport_6600002135() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType_6600002135();
    File xlsInput = new File(SUPPORT_IN3);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT3);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataType_6600002135() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.CURRENCY);
    return dataType;
  }

  // ===

  //  @Test
  public void testConvertCsvToXlsSupport_6600003474() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType_6600003474();
    File csvInput = new File(SUPPORT_IN4);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] csvBytes;
    try {
      csvBytes = IOUtils.toByteArray(new FileInputStream(csvInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] xlsBytes = conversion.convertCsvToXls(csvBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT4);
      outStream.write(xlsBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataType_6600003474() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Billing Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    return dataType;
  }

  //  @Test
  public void testConvertXlsToCsvSupport5() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport5();
    File xlsInput = new File(SUPPORT_IN5);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT5);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport5() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Billing Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    return dataType;
  }


  // ====

  //  @Test
  public void testConvertXlsToCsvSupport1158() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport1158();
    File xlsInput = new File(SUPPORT_IN1158);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT1158);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport1158() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible SIB (Comp) Amount", CsvFieldDataType.DECIMAL);

    return dataType;
  }

  //  @Test
  public void testConvertXlsToCsvSupport_HMS() throws Exception {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataType_HMS();
    File xlsInput = new File(SUPPORT_IN6);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      // boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      // if (!valid)
      // {
      // Assert.fail();
      // }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(SUPPORT_OUT6);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      // File csvOutput = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT01);
      // File csvExpected = new
      // File(XlsDataExtractionUtilTestCase.SCHEME_2421_OUTPUT_EXPECTED01);
      // boolean b = FileUtils.contentEquals(csvOutput, csvExpected);
      // if (!b)
      // {
      // Assert.fail();
      // }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  protected LinkedHashMap<String, CsvFieldDataType> createDataType_HMS() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.STRING);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Billing Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    return dataType;
  }

  //  @Test
  public void retrieveDataRange01() {
    byte[] csvBytes = null;
    List<String[]> rowsActual = null;
    List<String[]> rowsExpected = createExpectedOutput01();
    String path = "/csv-data-rangeinput.csv";
//    File csvInput = new File(path); //DATA_RANGE_INPUT01
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(path);
      csvBytes = IOUtils.toByteArray(inputStream);
      rowsActual = new XlsDataExtractionUtil().retrieveDataRange(FileType.CSV, csvBytes, 0, 1, false);
      for (int i = 0; i < rowsActual.size(); i++) {
        String[] actualRow = rowsActual.get(i);
        String[] expectedRow = rowsExpected.get(i);
        for (int j = 0; j < actualRow.length; j++) {
          if (!actualRow[j].equals(expectedRow[j])) {
            Assert.fail();
          }
        }
      }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  private List<String[]> createExpectedOutput01() {
    String[] line1 = {"Header 1", "Header 2", "Header 3", "Header 4", "Header 5"};
    String[] line2 = {"aaa", "bbb", "ccc", "", ""};
    List<String[]> rows = new ArrayList<String[]>();
    rows.add(line1);
    rows.add(line2);
    return rows;
  }

  @Test
  public void retrieveDataRange11() {
    byte[] csvBytes = null;
    List<String[]> rowsActual = null;
    List<String[]> rowsExpected = createExpectedOutput11();
    InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(DATA_RANGE_INPUT01);
    try {
      csvBytes = IOUtils.toByteArray(inputStream);
      rowsActual = new XlsDataExtractionUtil().retrieveDataRange(FileType.CSV, csvBytes, 1, 1, false);
      for (int i = 0; i < rowsActual.size(); i++) {
        String[] actualRow = rowsActual.get(i);
        String[] expectedRow = rowsExpected.get(i);
        for (int j = 0; j < actualRow.length; j++) {
          if (!actualRow[j].equals(expectedRow[j])) {
            Assert.fail();
          }
        }
      }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  @Test
  public void retrieveDataRangeXls00() {
    byte[] xlsBytes = null;
    List<String[]> rowsActual = null;
    List<String[]> rowsExpected = createExpectedOutputXls00();
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream("/6600004090-delta.xls");
      xlsBytes = IOUtils.toByteArray(inputStream);
      rowsActual = new XlsDataExtractionUtil().retrieveDataRange(FileType.XLS, xlsBytes, 0, 0, false);
      for (int i = 0; i < rowsActual.size(); i++) {
        String[] actualRow = rowsActual.get(i);
        String[] expectedRow = rowsExpected.get(i);
        for (int j = 0; j < expectedRow.length; j++) {
          if (!expectedRow[j].equals(actualRow[j])) {
            Assert.fail();
          }
        }
      }
    } catch (Exception e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  @Test
  public void retrieveDataRangeXlsHeaderRow3() throws IOException {
    byte[] xlsBytes = null;
    List<String[]> withEmptyRows = null;
    List<String[]> withoutEmptyRows = null;

    InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream
        ("/retrieveDataRangeXlsHeaderRow3.xls");
    xlsBytes = IOUtils.toByteArray(inputStream);
    withEmptyRows = new XlsDataExtractionUtil().retrieveDataRange(FileType.XLS, xlsBytes, 0, 6, true);
    withoutEmptyRows = new XlsDataExtractionUtil().retrieveDataRange(FileType.XLS, xlsBytes, 0, 6, false);
    Assert.assertEquals(7, withEmptyRows.size());
    Assert.assertEquals(5, withoutEmptyRows.size());
  }

  private List<String[]> createExpectedOutputXls00() {
    String[] line1 = {"Effective date", "First Name", "Risk Salary", "Prio Member Number", "Member Number"};
    List<String[]> rows = new ArrayList<String[]>();
    rows.add(line1);
    return rows;
  }

  //  @Test
  public void testGetCellValue() {
    byte[] xlsBytes = null;
    try {
      XlsDataExtractionUtil util = new XlsDataExtractionUtil();
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(CELL_VALUE_INPUT_XLS_01);
      xlsBytes = IOUtils.toByteArray(inputStream);
      Workbook workbook = util.createWorkbook(FileType.XLS, xlsBytes);
      Row firstRow = workbook.getSheetAt(0).getRow(0);
      for (int i = 0; i < 7; i++) {
        Cell cell = firstRow.getCell(i);
        String value = "";
        switch (i) {
          case 0:
            value = util.getCellValue(cell);
            System.out.println(value);
            Assert.assertEquals("500000.00", value);
            break;
          case 1:
            value = util.getCellValue(cell);
            System.out.println(value);
            Assert.assertEquals("01/06/2012", value);
            break;
          case 2:
            value = util.getCellValue(cell);
            System.out.println(value);
            Assert.assertEquals("Casper", value);
            break;
          case 3:
            value = util.getCellValue(cell);
            Assert.assertEquals("", value);
            break;
          case 4:
            value = util.getCellValue(cell);
            System.out.println(value);
            Assert.assertEquals("6700816421", value);
            break;
          case 5:
            value = util.getCellValue(cell);
            Assert.assertEquals("", value);
            break;
          case 6:
            value = util.getCellValue(cell);
            System.out.println(value);
            Assert.assertEquals("R 1234.56", value);
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      Assert.fail();
    }
  }

  @Test
  public void testGetHeaders() {
    XlsDataExtractionUtil util = new XlsDataExtractionUtil();
    try {
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(HEADERS);
      byte[] xlsxBytes = IOUtils.toByteArray(inputStream);
      Workbook workbook = util.createWorkbook(FileType.XLSX, xlsxBytes);
      List<String> headersLine2 = util.getHeaders(workbook, 2);
      Assert.assertEquals(headersLine2.get(0), "");
      Assert.assertEquals(headersLine2.get(1), "a");
      Assert.assertEquals(headersLine2.get(2), "b");
      Assert.assertEquals(headersLine2.get(3), "c");
      Assert.assertEquals(headersLine2.get(4), "");
      Assert.assertEquals(headersLine2.get(5), "d");

      List<String> headersLine4 = util.getHeaders(workbook, 4);
      Assert.assertEquals(headersLine4.get(0), "");
      Assert.assertEquals(headersLine4.get(1), "");
      Assert.assertEquals(headersLine4.get(2), "1");
      Assert.assertEquals(headersLine4.get(3), "");
      Assert.assertEquals(headersLine4.get(4), "2");
      Assert.assertEquals(headersLine4.get(5), "3.1");

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Test if the correct cell value is returned when using the XlsDataExtractionUtil util to find the first value for
   * a all column.
   */
  @Test
  public void testGetFistVerticalColumnValue() {
    byte[] xlsBytes = null;
    try {
      XlsDataExtractionUtil util = new XlsDataExtractionUtil();
      InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(SAMPLE_DATA_1);
      xlsBytes = IOUtils.toByteArray(inputStream);
      Map<Integer, String> dataMap = util.getFistVerticalColumnValues(FileType.XLSX, xlsBytes, 3);
      Assert.assertEquals("v3.1", dataMap.get(0));
      Assert.assertEquals("v1.2", dataMap.get(1));
      Assert.assertEquals("v1.3", dataMap.get(2));
      Assert.assertEquals(null, dataMap.get(3));
      Assert.assertEquals("v3.5", dataMap.get(4));
      Assert.assertEquals(null, dataMap.get(5));
      Assert.assertEquals("v1.7", dataMap.get(6));
      Assert.assertEquals(null, dataMap.get(7));
      Assert.assertEquals("v3.9", dataMap.get(8));
    } catch (Exception e) {
      Assert.fail();
    }

  }

  //      @Test
  public void retrieveDataRangeXls01() {
    byte[] xlsBytes = null;
    List<String[]> rowsActual = null;
    List<String[]> rowsExpected = createExpectedOutputXls01();
    File xlsInput = new File(DATA_RANGE_INPUT_XLS_01);
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      rowsActual = new XlsDataExtractionUtil().retrieveDataRange(FileType.XLS, xlsBytes, 0, 1, false);
      for (int rowIndex = 0; rowIndex <= 1; rowIndex++) {
        String[] actualRow = rowsActual.get(rowIndex);
        String[] expectedRow = rowsExpected.get(rowIndex);
        for (int colIndex = 0; colIndex < actualRow.length; colIndex++) {
          if (!actualRow[colIndex].equals(expectedRow[colIndex])) {
            Assert.fail();
          }
        }
      }
    } catch (IOException e) {
      Assert.fail();
      e.printStackTrace();
    }
  }

  private List<String[]> createExpectedOutputXls01() {
    String[] line1 = {"Effective date", "First Name", "Risk Salary", "Prio Member Number", "Member Number"};
    String[] line2 = {"01/06/2012", "Casper", "500000.00", "", "6700816421", "", "R 1,234.56"};
    List<String[]> rows = new ArrayList<String[]>();
    rows.add(line1);
    rows.add(line2);
    return rows;
  }

  private List<String[]> createExpectedOutput11() {
    String[] line1 = {"aaa", "bbb", "ccc", "", ""};
    String[] line2 = {"111", "222", "333", "", "444"};
    List<String[]> rows = new ArrayList<String[]>();
    rows.add(line1);
    rows.add(line2);
    return rows;
  }

  /**
   * test for getDistinctColumnMappings
   * It should return a distinct list of possible values in a column
   * Should also have an optional number for distinct values
   */
  //TODO must be reviewed. Get distinct list without doing the conversion
//  @Test
  public void testDistinctColumnMappings() {
//    try {
//      LinkedHashMap<String, CsvFieldDataType> dataTypeMap = createDataTypeDistinctColumnMappings();
//      File xlsInput = new File(DISTINCT1);
//      XlsDataExtractionUtil conversion
// = new XlsDataExtractionUtil(dataTypeMap);
//      byte[] xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
//      conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
//      List<String> vColumn = conversion.getDistinctColumnMappings("Sex", 2);
//      List<String> vColumnNoLimit = conversion.getDistinctColumnMappings("Title");
//
//      Assert.assertTrue("vColumn is empty", vColumn != null);
//      Assert.assertTrue("vColumnNoLimit is empty", vColumnNoLimit != null);
//
//      if (vColumn.size() <= 0) {
//        Assert.fail("Empty array");
//      }
//      if (vColumnNoLimit.size() <= 0) {
//        Assert.fail("Empty array");
//      }
//
//      for (String vStr : vColumn) {
//        System.out.print(vStr + " ");
//      }
//      for (String vStr : vColumnNoLimit) {
//        System.out.print(vStr + " ");
//      }
//    } catch (Exception ex) {
//      ex.printStackTrace();
//      Assert.fail(ex.getMessage());
//    }
  }

  /**
   * for {@link #testDistinctColumnMappings() testDistinctColumnMappings}
   *
   * @return LinkedHashMap representing dataTypeMap
   */
  protected LinkedHashMap<String, CsvFieldDataType> createDataTypeDistinctColumnMappings() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.INTEGER);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    return dataType;
  }

  /**
   * The purpose of this test is to check that the contents of the file are copied exactly as in the XLS
   * In this case in particular a number with a decimal e.g. 1.1 is copied as 1
   * meaning that all values after the decimal are ignored, which is incorrect
   */
//  @Test
  public void testConvertXlsToCsvSupport3() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport3();
    File xlsInput = new File(DECIMAL_PAYROLL_NUMBER_INPUT);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail();
      }
      FileOutputStream outStream = new FileOutputStream(DECIMAL_PAYROLL_NUMBER_GENERATED_OUTPUT);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      File csvOutput = new File(DECIMAL_PAYROLL_NUMBER_GENERATED_OUTPUT);
      File csvExpected = new File(DECIMAL_PAYROLL_NUMBER_EXPECTED_OUTPUT);
      boolean b = FileUtils.contentEqualsIgnoreEOL(csvOutput, csvExpected, null);
      if (!b) {
        Assert.fail("The output file and expected output must be the same.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * for {@link #testConvertXlsToCsvSupport3() testConvertXlsToCsvSupport3}
   *
   * @return LinkedHashMap representing dataTypeMap
   */
  private LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport3() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.INTEGER);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    return dataType;
  }

  /**
   * Test util with IGNORE column positioned in between other columns
   */
//  @Test
  public void testIgnoreColumnsInSourceFile() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeIgnore1();
    File xlsInput = new File(IGNORE_COL_MID_INPUT);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;
    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLS, xlsBytes);
      if (csvBytes == null || csvBytes.length == 0) {
        Assert.fail();
      }
      FileOutputStream outStream = new FileOutputStream(IGNORE_COL_MID_OUTPUT_GEN_EXP);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      File csvOutput = new File(IGNORE_COL_MID_OUTPUT_GEN_EXP);
      File csvExpected = new File(IGNORE_COL_MID_OUTPUT_EXP);
      boolean b = FileUtils.contentEqualsIgnoreEOL(csvOutput, csvExpected, null);
      if (!b) {
        Assert.fail("The output file and expected output must be the same.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  private LinkedHashMap<String, CsvFieldDataType> createDataTypeIgnore1() {
    // 0, 1, 0, 1, 1, 1, 0
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("XXX", CsvFieldDataType.EMPTY);
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("some weird system's export column", CsvFieldDataType.EMPTY);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("", CsvFieldDataType.EMPTY);
    return dataType;
  }

  /**
   * Test to see if the 1st cell in a row is empty.
   * If it is empty, the file conversion should not fail
   */
//  @Test
  public void testIs1stCellEmpty() {
    LinkedHashMap<String, CsvFieldDataType> dataTypMap = createDataTypeSupport4();
    File xlsInput = new File(EMPTY_1ST_ROW);
    XlsDataExtractionUtil conversion = new XlsDataExtractionUtil(dataTypMap);
    byte[] xlsBytes;

    try {
      xlsBytes = IOUtils.toByteArray(new FileInputStream(xlsInput));
      boolean valid = conversion.validate(FileType.XLSX, xlsBytes);
      if (!valid) {
        Assert.fail();
      }
      byte[] csvBytes = conversion.convertXlsToCsv(FileType.XLSX, xlsBytes);
      FileOutputStream outStream = new FileOutputStream(EMPTY_1ST_ROW_CSV_GENERATED);
      outStream.write(csvBytes);
      outStream.flush();
      outStream.close();

      // compare contents
      File csvOutput = new File(EMPTY_1ST_ROW_CSV_GENERATED);
      File csvExpected = new File(EMPTY_1ST_ROW_CSV_EXPECTED);
      boolean b = FileUtils.contentEqualsIgnoreEOL(csvOutput, csvExpected, null);
      if (!b) {
        Assert.fail("The output file and expected output must be the same.");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail();
    }
  }

  /**
   * for { testIs1stRowEmpty() testIs1stRowEmpty}
   *
   * @return LinkedHashMap representing dataTypeMap
   */
  private LinkedHashMap<String, CsvFieldDataType> createDataTypeSupport4() {
    LinkedHashMap<String, CsvFieldDataType> dataType = new LinkedHashMap<String, CsvFieldDataType>();
    dataType.put("Effective date", CsvFieldDataType.DATE);
    dataType.put("Title", CsvFieldDataType.STRING);
    dataType.put("First Name", CsvFieldDataType.STRING);
    dataType.put("Surname", CsvFieldDataType.STRING);
    dataType.put("Date of Birth", CsvFieldDataType.DATE);
    dataType.put("Sex", CsvFieldDataType.INTEGER);
    dataType.put("Member Group", CsvFieldDataType.STRING);
    dataType.put("Prio Member Number", CsvFieldDataType.STRING);
    dataType.put("Member Number", CsvFieldDataType.INTEGER);
    dataType.put("ID Number", CsvFieldDataType.INTEGER);
    dataType.put("Risk Salary", CsvFieldDataType.CURRENCY);
    dataType.put("Member Total Premium", CsvFieldDataType.CURRENCY);
    dataType.put("Passport Number", CsvFieldDataType.STRING);
    dataType.put("Payroll Number", CsvFieldDataType.STRING);
    dataType.put("Flex Option (FLAT/ MULTIPLE)", CsvFieldDataType.STRING);
    dataType.put("Flexible UGLA Amount", CsvFieldDataType.CURRENCY);
    dataType.put("Flexible SIB (Comp+) Amount", CsvFieldDataType.DECIMAL);
    return dataType;
  }


  // @Test
  // public void testGetDeclaredField()
  // {
  // XlsDataExtractionUtil util = new XlsDataExtractionUtil();
  // Field field = util.getField(new Record("Piet"), "name");
  // System.out.println(field.getName());
  // }
  //
  // @Test
  // public void testGetFields()
  // {
  // XlsDataExtractionUtil util = new XlsDataExtractionUtil();
  // String[] fieldNames = util.getFields("person:name");
  // for (String fieldName : fieldNames)
  // {
  // System.out.println(fieldName);
  // }
  // }
  //
  // class Record
  // {
  //
  // private String index;
  // private Person person;
  //
  // public Record(String index)
  // {
  // super();
  // this.index = index;
  // }
  //
  // public String getIndex()
  // {
  // return index;
  // }
  //
  // public void setIndex(String index)
  // {
  // this.index = index;
  // }
  //
  // public Person getPerson()
  // {
  // return person;
  // }
  //
  // public void setPerson(Person person)
  // {
  // this.person = person;
  // }
  //
  // }
  //
  // class Person
  // {
  //
  // private String name;
  //
  // public String getName()
  // {
  // return name;
  // }
  //
  // public void setName(String name)
  // {
  // this.name = name;
  // }
  //
  // }

  @Test
  public void testGetDistinctColumnValues() throws IOException {
    byte[] xlsBytes = null;

    XlsDataExtractionUtil util = new XlsDataExtractionUtil();
    InputStream inputStream = XlsDataExtractionUtilTestCase.class.getResourceAsStream(DISTINCT_COLUMN_VALUE);
    xlsBytes = IOUtils.toByteArray(inputStream);
    Set<String> dataSet = util.getDistinctColumnValues("Title", 3, FileType.XLS, xlsBytes);
    Assert.assertNotNull(dataSet);
    Assert.assertEquals(4, dataSet.size());
    Assert.assertFalse(dataSet.contains("Title"));
    Assert.assertTrue(dataSet.contains("Others"));
    Assert.assertTrue(dataSet.contains("Ms"));
    Assert.assertTrue(dataSet.contains("Mr"));
    Assert.assertTrue(dataSet.contains("Dr"));

  }
}
