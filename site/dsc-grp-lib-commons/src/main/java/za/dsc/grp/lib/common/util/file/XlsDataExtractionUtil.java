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

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import za.dsc.grp.lib.common.types.CsvFieldDataType;
import za.dsc.grp.lib.common.types.FileType;

//import org.apache.log4j.Logger;
//import za.co.discovery.util.MiscHelper;

/**
 * Copyright: Copyright (c) 2012 Company: Discovery Life This utility facilitate
 * conversions between *.csv and *.xls (or *.xlsx) file formats in both
 * directions. The main focus of this utility is to provide correct value
 * formatting for the different data types when doing the file conversion.
 * <p>
 * From a usage perspective this class has four main entry points or usage
 * scenarios:
 * <ul>
 * <li>
 * <code>public byte[] convertXlsToCsv(FileType fileExtension, byte[] xlsBytes)</code>
 * <li>
 * <code>public void convertXlsToCsv(String filePath, String fileName)</code>
 * <li><code>public byte[] convertCsvToXls(byte[] csvBytes)</code>
 * <li>
 * <code>public void convertCsvToXls(String filePath, String fileName)</code>
 * </ul>
 * <p>
 * Note: <code>cell.getCellType() == 0</code> is true for Cell's formatted as a
 * date or number values. <code>cell.getCellType() == 1</code> is true for
 * Cell's formatted as a string value.
 * <p>
 * The intension of this utility was to make its functionality as generic as
 * possible. Before using it though make sure to read the java docs for the
 * methods you use to understand the assumptions made.
 * </p>
 */


public class XlsDataExtractionUtil {

  //  private static final Logger logger = MiscHelper.getDefaultLogger(FileConversionUtil.class);

  public static final String STANDARD_XML_DATE_FORMAT = "yyyy-MM-dd";

  private String dateFormat = "dd/MM/yyyy";
  private String dateRegExpression = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
  private String csvRegExpression = "(?:\\s*(?:\\\"([^\\\"]*)\\\"|([^,]+))\\s*,?)+?";

  //public static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
  //public static final String DATE_REG_EXPRESSION = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)";
  public static final String CURRENCY_REG_EXPRESSION = "(\\D)(\\d)*((\\.)(\\d*))?";
  /**
   * Maps the source file's column headings to a data type (CsvFieldDataType).
   * NOTE: Source files with a CsvFieldDataType.EMPTY will be ignored and not
   * converted to the target file.
   */
  private LinkedHashMap<String, CsvFieldDataType> dataTypeMap;
  /**
   * Used to flag columns in the source file as mapped (1) or not mapped (0).
   */
  private int[] sourceMappedColumns;
  /**
   * One-to-mapping of CellStyle's to the list of headers.
   */
  private Map<Integer, CellStyle> cellStyleMap;
  /**
   * Distinct list of columns headers in the target file.
   */
  private List<String> headers;
  private boolean errorsFound;
  private String errorMessageInfo;
  private Workbook workbook;
  private List<List<Cell>> rowList;
  private int headerLineNo; //one based index

  private Map<String, String> xmlColumnMap;
  /**
   * index of finger print col inside the upload file
   */
  private int[] headerIndex;

  public XlsDataExtractionUtil(LinkedHashMap<String, CsvFieldDataType> dataTypeMap) {
    super();
    this.dataTypeMap = dataTypeMap;
//    sourceMappedColumns = new int[dataTypeMap.size()];
//    int index = 0;
//    for (CsvFieldDataType dataType : dataTypeMap.values()) {
//      sourceMappedColumns[index] = (dataType == CsvFieldDataType.EMPTY) ? 0 : 1;
//      index++;
//    }
    headers = createHeaders();
//    headerIndex = createHeaderNameIndex();
    this.headerLineNo = 1;
  }

  public XlsDataExtractionUtil(LinkedHashMap<String, CsvFieldDataType> dataTypeMap, int headerLineNo) {
    super();
    this.dataTypeMap = dataTypeMap;
//    sourceMappedColumns = new int[dataTypeMap.size()];
//    int index = 0;
//    for (CsvFieldDataType dataType : dataTypeMap.values()) {
//      sourceMappedColumns[index] = (dataType == CsvFieldDataType.EMPTY) ? 0 : 1;
//      index++;
//    }
    headers = createHeaders();
//    headerIndex = createHeaderNameIndex();
    this.headerLineNo = headerLineNo;
  }

  public XlsDataExtractionUtil() {
    super();
  }

  protected void initialise() {
    errorMessageInfo = "";
    errorsFound = false;
  }

  public String getDateFormat() {
    return dateFormat;
  }

  public void setDateFormat(String dateFormat) {
    this.dateFormat = dateFormat;
  }

  public String getDateRegExpression() {
    return dateRegExpression;
  }

  public void setDateRegExpression(String dateRegExpression) {
    this.dateRegExpression = dateRegExpression;
  }

  public Map<String, String> getXmlColumnMap() {
    return xmlColumnMap;
  }

  public void setXmlColumnMap(Map<String, String> xmlColumnMap) {
    this.xmlColumnMap = xmlColumnMap;
  }

  /**
   * Use this method to convert a Excel file (*.xls or *.xlsx) to a CSV file.
   * The created CSV file will be created at the given file path and will have
   * the same file name as the input file but with a *.csv extension. The
   * current version of this utility assumes that the first line of the file to
   * be converted contains header information.
   *
   * @param filePath
   * @param fileName
   * @throws Exception
   */
  public void convertXlsToCsv(String filePath, String fileName) throws Exception {
    initialise();
//    if (logger.isDebugEnabled())
//      printColumnDataTypeMapping();
    String filePathAndName = (StringUtils.endsWith(filePath, "\\") ? filePath = "\\" : filePath) + fileName;
    Workbook workbook = createWorkbook(filePathAndName);
    if (workbook != null) {
      List<List<Cell>> cellGrid = createCellGrid(workbook);
      rowList = cellGrid;
      String csvString = doExcelToCsvConversion(cellGrid);
      createCsvFile(csvString, filePath, fileName);
    }
  }

  /**
   * Use this method to convert a Excel files byte[] to a CSV byte array. The
   * current version of this utility assumes that the first line of the files to
   * be converted contains header information.
   *
   * @param fileExtension
   * @param xlsBytes
   * @return byte[]
   * @throws Exception
   */
  public byte[] convertXlsToCsv(FileType fileExtension, byte[] xlsBytes) throws Exception {
    initialise();
//    if (logger.isDebugEnabled())
//      printColumnDataTypeMapping();
    if (workbook == null) {
      workbook = createWorkbook(fileExtension, xlsBytes);
    }
    createHeaderNameIndex();
//    if (headers == null) { //shifted to constructor
//      headers = getHeaders(workbook, headerLineNo); //XXX
//    }
    iniCellStyleMap(workbook, headers); //XXX
    byte[] csvBytes = null;
    if (workbook != null) {
      List<List<Cell>> cellGrid = createCellGrid(workbook);
      rowList = cellGrid;
      String csvString = doExcelToCsvConversion(cellGrid);
      csvString = StringUtils.chomp(csvString);
      csvBytes = csvString.getBytes();
    }
    return csvBytes;
  }

  public String convertXlsToXml(FileType fileExtension, byte[] xlsBytes) throws Exception {
    initialise();
    if (workbook == null) {
      workbook = createWorkbook(fileExtension, xlsBytes);
    }
    createHeaderNameIndex();
    iniCellStyleMap(workbook, headers);
    String xml = "";
    if (workbook != null) {
      List<List<Cell>> cellGrid = createCellGrid(workbook);
      xml = doExcelToXmlConversion(cellGrid);
    }
    return xml;
  }

  protected String doExcelToXmlConversion(List<List<Cell>> cellGrid) throws Exception {
    StringBuilder stringBuilder = new StringBuilder();
    int currCol = -1;
    int currRow = -1;
    try {
      stringBuilder.append("<rows>");
      for (int r = 1; r < cellGrid.size(); r++) {
        stringBuilder.append("<row>");
        currRow = r;
        List<Cell> row = cellGrid.get(r);
        for (int c = 0; c < headers.size(); c++) {
          currCol = c;
          String currHeader = headers.get(c);
          Cell cell = row.get(c);
          CsvFieldDataType dataType = dataTypeMap.get(currHeader);
          String formattedCellValue = cell != null ? formatCellValue(cell, dataType) : "";
          //stringBuilder.append(c < row.size() - 1 ? formattedCellValue + "," : formattedCellValue + "\n");
          stringBuilder.append("<column type=\"");
          stringBuilder.append(dataType);
          stringBuilder.append("\" name=\"");
          stringBuilder.append(xmlColumnMap.get(currHeader));
          stringBuilder.append("\">");
          stringBuilder.append(formattedCellValue);
          stringBuilder.append("</column>");
        }
        stringBuilder.append("</row>");
      }
      stringBuilder.append("</rows>");
    } catch (Exception e) {
      errorMessageInfo =
          "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nCurrent column index: " + currCol
              + " Current row index: " + currRow;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return stringBuilder.toString();
  }

  /**
   * Some basic validations to verify upload file integrity.
   *
   * @param fileExtension
   * @param xlsBytes
   * @return boolean
   * @throws IllegalStateException
   */
  @Deprecated
  // validation will be moved to the service layer.
  public boolean validate(FileType fileExtension, byte[] xlsBytes) throws IOException, IllegalStateException {
    initialise();
    // COLUMN COUNT COMPARE
    int expectedColCount = dataTypeMap.size(); // dataTypeMap.size()
    if (workbook == null) {
      workbook = createWorkbook(fileExtension, xlsBytes);
    }
    if (headers == null) {
      headers = getHeaders(workbook);
    }
    int actualColCount = headers.size();
    if (expectedColCount != actualColCount) {
      errorsFound = true;
      errorMessageInfo =
          "Column count discrepancy: Expected column count: " + expectedColCount + ". Actual column count: " + actualColCount + ".";
      return false;
    }
    // COLUMN NAMES COMPARE
    for (String title : dataTypeMap.keySet()) {
      if (!headers.contains(title)) {
        errorsFound = true;
        errorMessageInfo = "Column with name: '" + title + "' is missing, spelled incorrectly or contains illegal spaces.";
        return false;
      }
    }
    // COLUMN ORDER COMPARE
    int index = 0;
    for (String title : dataTypeMap.keySet()) {
      String currFileCol = headers.get(index);
      if (!title.equals(currFileCol)) {
        errorsFound = true;
        errorMessageInfo = "Column " + (index + 1) + " must be '" + title + "', but '" + currFileCol + "' is found.";
        return false;
      }
      index++;
    }
    return true;
  }

  /**
   * Creates a list of header names.
   *
   * @param headerRow
   * @return List<String>
   */
  protected List<String> getHeaders(String[] headerRow) {
    List<String> headers = new ArrayList<String>();
    for (String header : headerRow) {
      headers.add(header.trim());
    }
    return headers;
  }

  /**
   * Create headers list from workbook. Accommodated to take care of empty rows
   * at the top of the spreadsheet.
   *
   * @param workbook
   * @return List<String>
   * @throws IllegalStateException
   */
  protected List<String> getHeaders(Workbook workbook) throws IllegalStateException {
    List<String> headers = new ArrayList<String>();
    for (int r = 0; r <= workbook.getSheetAt(0).getPhysicalNumberOfRows(); r++) {
      Row row = workbook.getSheetAt(0).getRow(r);
      if (row != null && !isEmptyRow(row)) {
        for (int c = 0; row.getCell(c) != null && !StringUtils.isEmpty(row.getCell(c).getStringCellValue().trim()); c++) {
          String header = row.getCell(c).getStringCellValue().trim();
          if (sourceMappedColumns[c] == 1) {
            headers.add(header);
          }
        }
        return headers;
      }
    }
    if (headers.size() == 0) {
      {
        throw new IllegalStateException("Spreadsheet does not seem to contain any information");
      }
    }
    return headers;
  }

  // /**
  // * Creates a list of header names.
  // *
  // * @param headerRow
  // * @return List<String>
  // */
  // protected List<String> getHeaders(List<Cell> headerRow)
  // {
  // List<String> headers = new ArrayList<String>();
  // for (Cell cell : headerRow)
  // {
  // String cellValue = cell.getStringCellValue();
  // headers.add(cellValue);
  // }
  // return headers;
  // }

  /**
   * This is where the actual conversion takes place. Excel's cell values get
   * converted into formatted string values.
   *
   * @param cellGrid
   * @return String
   * @throws Exception
   */
  protected String doExcelToCsvConversion(List<List<Cell>> cellGrid) throws Exception {
    StringBuilder stringBuilder = new StringBuilder();
    int currCol = -1;
    int currRow = -1;
    try {
      for (int c = 0; c < headers.size(); c++) {
        String header = headers.get(c);
        stringBuilder.append(c < headers.size() - 1 ? header + "," : header + "\n");
      }
      for (int r = 1; r < cellGrid.size(); r++) {
//        logger.debug("--------ROW = " + r + "-----------");
        currRow = r;
        List<Cell> row = cellGrid.get(r);
        for (int c = 0; c < headers.size(); c++) {
          currCol = c;
//          logger.debug("COL = " + c);
          String currHeader = headers.get(c);
          Cell cell = row.get(c);
          CsvFieldDataType dataType = dataTypeMap.get(currHeader);
//          logger.debug("\tdataType = " + dataType.getDescription());
          String formattedCellValue = cell != null ? formatCellValue(cell, dataType) : "";
//          logger.debug("\tformattedCellValue = " + formattedCellValue);
          stringBuilder.append(c < row.size() - 1 ? formattedCellValue + "," : formattedCellValue + "\n");
        }
      }
    } catch (Exception e) {
      errorMessageInfo =
          "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nCurrent column index: " + currCol
              + " Current row index: " + currRow;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return stringBuilder.toString();
  }

  /**
   * Creates a CSV file at a location provided from a string (
   * <code>csvString</code>) containing the file contents.
   *
   * @param csvString filePathAndName
   */
  protected void createCsvFile(String csvString, String filePath, String fileName) {
    String fileNameNoExtension = StringUtils.substringBeforeLast(fileName, ".");
    File csvFile = new File(filePath + fileNameNoExtension + ".csv");
    FileWriter fileWriter = null;
    try {
      fileWriter = new FileWriter(csvFile);
      fileWriter.append(csvString);
      fileWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fileWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Switches to different formatting algorithms depending on the column data
   * type.
   *
   * @param cell
   * @param dataType
   * @return String
   */
  protected String formatCellValue(Cell cell, CsvFieldDataType dataType) {
    String formattedCellValue = "";
    if (dataType == CsvFieldDataType.CURRENCY) {
      formattedCellValue = doCurrencyFormatting(cell, dataType);
    }
    else if (dataType == CsvFieldDataType.DATE) {
      formattedCellValue = doDateFormatting(cell, dataType);
    }
    else if (dataType == CsvFieldDataType.DECIMAL) {
      formattedCellValue = doDecimalFormatting(cell, dataType);
    }
    else if (dataType == CsvFieldDataType.INTEGER) {
      formattedCellValue = doIntegerFormatting(cell, dataType);
    }
    else if (dataType == CsvFieldDataType.STRING) {
      formattedCellValue = cell.getCellType() == 1 ? cell.getStringCellValue() : getCellValue(cell); // cell.setCellType(1)
    }
    return formattedCellValue;
  }

  /**
   * Handles currency formatting from Excel Cell value to a String value.
   *
   * @param cell
   * @param dataType
   * @return String
   */
  protected String doCurrencyFormatting(Cell cell, CsvFieldDataType dataType) {
    String formattedValue = "";
    String cellStringValue = "";
    if (cell.getCellType() == 0) {
      Number cellNumberValue = cell.getNumericCellValue();
      formattedValue = doNumberFormat(cellNumberValue, "#.00");
    }
    else if (cell.getCellType() == 1) {
      try {
        cellStringValue = cell.getStringCellValue();
        cellStringValue = StringUtils.remove(cellStringValue, ' ');
        Pattern pattern = Pattern.compile(CURRENCY_REG_EXPRESSION);
        Matcher matcher = pattern.matcher(cellStringValue);
        if (matcher.matches()) {
          cellStringValue = StringUtils.substring(cellStringValue, 0, 1) + " " + StringUtils.substring(cellStringValue, 1);
        }
        Number cellNumberValue = NumberFormat.getCurrencyInstance().parse(cellStringValue);
        formattedValue = doNumberFormat(cellNumberValue, "#.00");
      } catch (Exception e) {
        try {
          Number cellNumberValue = NumberFormat.getNumberInstance().parse(cellStringValue);
          formattedValue = doNumberFormat(cellNumberValue, "#.00");
        } catch (ParseException e1) {
          // "User error, NOT formatting issue. Keep value as is."
          if (!StringUtils.isEmpty(cellStringValue)) {
            e.printStackTrace();
          }
          formattedValue = cellStringValue;
        }
      }
    }
    return formattedValue;
  }

  /**
   * Handles date formatting from Excel Cell value to a String value.
   *
   * @param cell
   * @param dataType
   * @return String
   */
  protected String doDateFormatting(Cell cell, CsvFieldDataType dataType) {
    String formattedValue = "";
    if (cell.getCellType() == 0) {
      Date cellDateValue = cell.getDateCellValue();
      formattedValue = doDateFormat(cellDateValue, STANDARD_XML_DATE_FORMAT);
    }
    else if (cell.getCellType() == 1) {
      formattedValue = getDateCellValue(cell);
    }
    return formattedValue;
  }

  protected String getDateCellValue(Cell cell) {
    String formattedValue = "";
    String cellStringValue = "";
    try {
      cellStringValue = cell.getStringCellValue();
      Pattern pattern = Pattern.compile(dateRegExpression);
      Matcher matcher = pattern.matcher(cellStringValue);
      if (matcher.matches()) {
        DateFormat df = new SimpleDateFormat(dateFormat);
        Date date = df.parse(cellStringValue);
        formattedValue = doDateFormat(date, STANDARD_XML_DATE_FORMAT);
      }
      else {
        // "User error, NOT formatting issue. Keep value as is."
        formattedValue = cellStringValue;
      }
    } catch (Exception e) {
      // "User error, NOT formatting issue. Keep value as is."
      Date dateValue = cell.getDateCellValue();
      formattedValue = doDateFormat(dateValue, STANDARD_XML_DATE_FORMAT);
    }
    return formattedValue;
  }

  /**
   * Handles decimal value formatting from Excel Cell value to a String value.
   *
   * @param cell
   * @param dataType
   * @return String
   */
  protected String doDecimalFormatting(Cell cell, CsvFieldDataType dataType) {
    String formattedValue = "";
    String cellStringValue = "";
    if (cell.getCellType() == 0) {
      Number cellNumberValue = cell.getNumericCellValue();
      formattedValue = doNumberFormat(cellNumberValue, "#.00");
    }
    else if (cell.getCellType() == 1) {
      try {
        cellStringValue = cell.getStringCellValue();
        cellStringValue = StringUtils.remove(cellStringValue, ' ');
        Number cellNumberValue = NumberFormat.getNumberInstance().parse(cellStringValue);
        formattedValue = doNumberFormat(cellNumberValue, "#.00");
      } catch (Exception e) {
        // "User error, NOT formatting issue. Keep value as is."
        // e.printStackTrace();
        formattedValue = cellStringValue;
      }
    }
    return formattedValue;
  }

  /**
   * Handles integer formatting from Excel Cell value to a String value.
   *
   * @param cell
   * @param dataType
   * @return String
   */
  protected String doIntegerFormatting(Cell cell, CsvFieldDataType dataType) {
    String formattedValue = "";
    String cellStringValue = "";
    if (cell.getCellType() == 0) {
      Number cellNumberValue = cell.getNumericCellValue();
      formattedValue = doNumberFormat(cellNumberValue, "#");
    }
    else if (cell.getCellType() == 1) {
      try {
        cellStringValue = cell.getStringCellValue();
        Number number = NumberFormat.getInstance().parse(cellStringValue.trim());
        formattedValue = doNumberFormat(number, "#");
      } catch (Exception e) {
        // "User error, NOT formatting issue. Keep value as is."
        // e.printStackTrace();
        formattedValue = cellStringValue;
      }
    }
    return formattedValue;
  }

  /**
   * This is the generic number formatting handler.
   *
   * @param value
   * @param pattern
   * @return String
   */
  protected String doNumberFormat(Object value, String pattern) {
    String result = StringUtils.EMPTY;
    if (value == null) {
      return result;
    }
    if (value instanceof Number) {
      Number number = (Number) value;
      DecimalFormat df = new DecimalFormat(pattern);
      result = df.format(number);
    }
    return result;
  }

  /**
   * This is the generic date formatter.
   *
   * @param value
   * @param patternValue
   * @return String
   */
  protected String doDateFormat(Object value, String patternValue) {
    String result = StringUtils.EMPTY;
    if (value == null) {
      return result;
    }
    if (value instanceof Date) {
      Date tmpDateValue = (Date) value;
      DateFormat df = new SimpleDateFormat(patternValue);
      result = df.format(tmpDateValue);
    }
    return result;
  }

  /**
   * Creates a Excel Wookbook from the given file path and name.
   *
   * @param fileName
   * @return Workbook
   * @throws IOException
   */
  protected Workbook createWorkbook(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    DataInputStream myInput = new DataInputStream(fis);
    Workbook workBook = null;
    if (fileName.endsWith(".xls"))// older Excel
    {
//      logger.info("Start loading HSSFWorkbook from file: " + fileName);
      workBook = new HSSFWorkbook(myInput);
//      logger.info("Successfully created HSSFWorkbook.");
    }
    else if (fileName.endsWith(".xlsx"))// Excel 2007+
    {
//      logger.info("Start loading XSSFWorkbook from file: " + fileName);
      workBook = new XSSFWorkbook(myInput);
//      logger.info("Successfully created XSSFWorkbook.");
    }
    return workBook;
  }

  /**
   * Creates a Excel Workbook from the given files extension and excel bytes
   * array.
   *
   * @param fileExtension
   * @param xlsBytes
   * @return Workbook
   * @throws IOException
   */
  protected Workbook createWorkbook(FileType fileExtension, byte[] xlsBytes) throws IOException {
    InputStream inputStream = new ByteArrayInputStream(xlsBytes);
    Workbook workBook = null;
    try {
      if (fileExtension == FileType.XLS)// older Excel
      {
        workBook = new HSSFWorkbook(inputStream);
      }
      else if (fileExtension == FileType.XLSX)// Excel 2007+
      {
        workBook = new XSSFWorkbook(inputStream);
      }
    } catch (org.apache.poi.openxml4j.exceptions.InvalidOperationException e) {
      errorMessageInfo = "Verify that the correct FileType parameter (*.xls or *.xlsx) of the createWorkbook() method is used.\n";
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return workBook;
  }

  /**
   * This is a per row per cell representation of the Excel Workbook using
   * apache 'poi' framework. The method returns a list of rows (
   * <code>List<Cell>></code>).
   *
   * @param workbook
   * @return List<List<Cell>>
   * @throws Exception
   */
  protected List<List<Cell>> createCellGrid(Workbook workbook) throws Exception {
    List<List<Cell>> cellGrid = null;
    int currCol = -1;
    int currRow = -1;
    try {
      cellGrid = new ArrayList<List<Cell>>();
      Sheet mySheet = workbook.getSheetAt(0);
      Iterator<Row> rowIter = mySheet.rowIterator();
      while (rowIter.hasNext()) {
        currRow++;
        Row row = rowIter.next();
        if (isEmptyRow(row)) {
          //break;
          continue;
        }
        List<Cell> cellRowList = new ArrayList<Cell>();
        int targetColIndex = 0;
        for (int col = 0; col < headerIndex.length; col++) { //XXX
          Cell cell = row.getCell(headerIndex[col], Row.CREATE_NULL_AS_BLANK);
//          CellStyle style = cellStyleMap.get(targetColIndex);
//          cell.setCellStyle(style);
          cellRowList.add(cell);
          targetColIndex++;
        }
        cellGrid.add(cellRowList);
      }
    } catch (Exception e) {
      errorMessageInfo =
          "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nCurrent column index: " + currCol
              + " Current row index: " + currRow;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return cellGrid;
  }

  protected void createHeaderNameIndex() {
    int[] fpHeaderIndex = new int[dataTypeMap.size()];
    List<String> fileHeaders = getHeaders(workbook, headerLineNo);
    int count = 0;
    for (Map.Entry<String, CsvFieldDataType> entry : dataTypeMap.entrySet()) {
      int indexInUploadFile = fileHeaders.indexOf(entry.getKey());
      if (indexInUploadFile == -1) {
        throw new IllegalStateException("the finger print column '" + entry.getKey() + "' is not found in header list" +
            " of upload file");
      }
      fpHeaderIndex[count++] = indexInUploadFile;
    }
    headerIndex = fpHeaderIndex;
  }

  protected List<String> createHeaders() {
    if (headers == null) {
      headers = new ArrayList<String>();
    }
    for (Map.Entry<String, CsvFieldDataType> entry : dataTypeMap.entrySet()) {
      headers.add(entry.getKey());
    }
    return headers;
  }

  /**
   * This is a per row per cell representation of the Excel Workbook using
   * apache 'poi' framework. The method returns a list of rows (
   * <code>List<Cell>></code>).
   *
   * @param workbook
   * @return List<List<Cell>>
   * @throws Exception
   */
  protected List<List<Cell>> createCellGrid(Workbook workbook, int startIndex, int endIndex) throws Exception {
    List<List<Cell>> cellGrid = null;
    int currCol = -1;
    int currRow = -1;
    try {
      cellGrid = new ArrayList<List<Cell>>();
      Sheet mySheet = workbook.getSheetAt(0);
      int colCount = headers.size();
      Iterator<Row> rowIter = mySheet.rowIterator();
      for (int i = startIndex; i <= endIndex; i++) {
        currRow++;
        Row row = rowIter.next();
        if (isEmptyRow(row)) {
          break;
        }
        List<Cell> cellRowList = new ArrayList<Cell>();
        for (int c = 0; c < colCount; c++) {
          currCol = c;
          Cell cell = row.getCell(c, Row.CREATE_NULL_AS_BLANK);
          CellStyle style = cellStyleMap.get(c);
          cell.setCellStyle(style);
          cellRowList.add(cell);
        }
        cellGrid.add(cellRowList);
      }
    } catch (Exception e) {
      errorMessageInfo =
          "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nCurrent column index: " + currCol
              + " Current row index: " + currRow;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return cellGrid;
  }

  /**
   * This is needed because POI (
   * <code>sheet.rowIterator()<code>) 'pull' in empty rows from the Excel spreadsheet.
   *
   * @param row
   * @return boolean
   */

  protected boolean isEmptyRow(Row row) {
    boolean isEmptyRow = true;
    if (row == null) {
      return true;
    }
    for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
      Cell cell = row.getCell(cellNum);
      if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK && StringUtils.isNotBlank(cell.toString())) {
        isEmptyRow = false;
      }
    }
    return isEmptyRow;
//    if (row.getPhysicalNumberOfCells() <= 0) {
//      return true;
//    }
//    else if (row.getPhysicalNumberOfCells() == 1) {
//      for (int c = 0; c < row.getLastCellNum(); c++) {
//        Cell cell = row.getCell(c, Row.RETURN_BLANK_AS_NULL);
//        if (cell == null) {
//          return true;
//        }
//      }
//    }
//    else {
//      if (headers != null) // dataTypeMap
//      {
//        boolean isWholeRowEmpty = true;
//        for (int c = 0; c < headers.size(); c++) // dataTypeMap.size()
//        {
//          Cell cell = row.getCell(c, Row.RETURN_BLANK_AS_NULL);
//          if (cell != null)// if 1st cell is not empty, then the whole row is
//          // not empty
//          {
//            if (cell.getCellType() == 0) {
//              try {
//                Date date = cell.getDateCellValue();
//                if (date != null) {
//                  return false;
//                }
//              } catch (Exception consume1) {
//                try {
//                  Double number = cell.getNumericCellValue();
//                  if (number != null) {
//                    return false;
//                  }
//                } catch (Exception consume2) {
//
//                }
//              }
//            }
//            else if (cell.getCellType() == 1) {
//              if (!StringUtils.isEmpty(cell.getStringCellValue().trim())) {
//                return false;
//              }
//
//              if (StringUtils.isEmpty(cell.getStringCellValue().trim())) {
//                // do nothing
//              }
//              else {
//                isWholeRowEmpty = false;
//              }
//            }
//            continue;
//          }
//          else
//          // loop through the dataTypeMap(File Headers) to see if all the cells
//          // are empty and only then return true
//          {
//            boolean vFlag = true;
//            for (int x = 0; x < headers.size(); x++) // dataTypeMap.size()
//            {
//              Cell vTempCell = row.getCell(x, Row.RETURN_BLANK_AS_NULL);
//              if (vTempCell != null) {
//                // at the 1st occurrence of a not null cell, return false
//                vFlag = false;
//                return vFlag;
//              }
//            }
//            return true;
//          }
//        }
//
//        if (isWholeRowEmpty)// i.e. the whole row is made up of white spaces or
//        // empty strings
//        {
//          return true;
//        }
//      }
//    }
//    return false;
  }

  /**
   * Returns all the rows between and inclusive of the start and end indices of
   * the CSV file. Indices are zero-based.
   * <p/>
   * csvBytes
   *
   * @param startIndex
   * @param endIndex
   * @return List<String[]>
   * @throws IOException
   */
  public List<String[]> retrieveDataRange(FileType fileType, byte[] bytes, int startIndex, int endIndex,
                                          boolean includeEmptyRows)
      throws IOException {
    List<String[]> rows = new ArrayList<String[]>();
    switch (fileType) {
      case CSV:
        rows = retrieveDataRangeCSV(bytes, startIndex, endIndex);
        break;

      case XLS:
        rows = retrieveDataRangeXLS(bytes, startIndex, endIndex, includeEmptyRows);
        break;

      case XLSX:
        rows = retrieveDataRangeXLSX(bytes, startIndex, endIndex, includeEmptyRows);
        break;

      default:
        break;
    }
    return rows;
  }

  /**
   * Indices are zero-based.
   *
   * @param csvBytes
   * @param startIndex
   * @param endIndex
   * @return
   * @throws IOException
   */
  protected List<String[]> retrieveDataRangeCSV(byte[] csvBytes, int startIndex, int endIndex) throws IOException {
    InputStream bais = new ByteArrayInputStream(csvBytes);
    InputStreamReader isr = new InputStreamReader(bais);
    LineNumberReader lnr = new LineNumberReader(isr);
    List<String[]> rows = new ArrayList<String[]>();
    String currentLine = "";
    lnr.setLineNumber(-1);
    while ((currentLine = lnr.readLine()) != null) {
      int lineNbr = lnr.getLineNumber();
      if (lineNbr > endIndex) {
        break;
      }
      if (lineNbr >= startIndex && lineNbr <= endIndex) {
        String[] row = currentLine.split(",", -1);
        rows.add(row);
      }
    }
    return rows;
  }

  /**
   * Indices are zero-based.
   *
   * @param xlsBytes
   * @param startIndex
   * @param endIndex
   * @return
   * @throws IOException
   */
  protected List<String[]> retrieveDataRangeXLS(byte[] xlsBytes, int startIndex, int endIndex,
                                                boolean includeEmptyRows) throws IOException {
    List<String[]> rows = new ArrayList<String[]>();
    workbook = createWorkbook(FileType.XLS, xlsBytes);
    rows = getRowData(workbook, startIndex, endIndex, includeEmptyRows);
    return rows;
  }

  /**
   * Indices are zero-based.
   *
   * @param xlsBytes
   * @param startIndex
   * @param endIndex
   * @param includeEmptyRows
   * @return
   * @throws IOException
   */
  protected List<String[]> retrieveDataRangeXLSX(byte[] xlsBytes, int startIndex, int endIndex, boolean includeEmptyRows)
      throws IOException {
    List<String[]> rows = new ArrayList<String[]>();
    workbook = createWorkbook(FileType.XLSX, xlsBytes);
    rows = getRowData(workbook, startIndex, endIndex, includeEmptyRows);
    return rows;
  }

  protected List<String[]> getRowData(Workbook workbook, int startIndex, int endIndex, boolean includeEmptyRows) {
    List<String[]> lineData = new ArrayList<String[]>();
    List<String> rowData = new ArrayList<String>();
    int colCount = getColumnCount(workbook);
    for (int r = startIndex; r <= endIndex; r++) {
      Row row = workbook.getSheetAt(0).getRow(r);
      if (!isEmptyRow(row)) {
        rowData = new ArrayList<String>();
        for (int c = 0; c < colCount; c++) {
          String value = getCellValue(row.getCell(c)); // row.getCell(c).getStringCellValue().trim();
          rowData.add(value);
        }
        lineData.add(rowData.toArray(new String[colCount]));
      }
      else if (includeEmptyRows) {
        rowData = new ArrayList<String>();
        for (int c = 0; c < colCount; c++) {
          rowData.add("");
        }
        lineData.add(rowData.toArray(new String[colCount]));
      }
    }
    return lineData;
  }

  /**
   * This method is used to get a cell value without knowing the cell data type.
   * Alternatively in cases where the field data type is known but the physical
   * cell is formatted as some other data type and you need the string value of
   * the field.
   *
   * @param cell
   * @return cell value
   */
  protected String getCellValue(Cell cell) {
    String value = "";
    if (cell == null) {
      return "";
    }

    if (cell.getCellType() == 0) {
      if (HSSFDateUtil.isCellDateFormatted(cell)) {
        value = getDateCellValue(cell);
      }
      else {
        Number cellNumberValue = cell.getNumericCellValue();
        if (cell.getCellStyle().getDataFormatString().equals("General")) {
          DataFormatter formatter = new DataFormatter();
          value = formatter.formatCellValue(cell);
        }
        else {
          value = doNumberFormat(cellNumberValue, cell.getCellStyle().getDataFormatString());
        }
      }
    }
    else if (cell.getCellType() == 1) {
      value = cell.getStringCellValue();
    }
//      try {
//        String dataFormat = cell.getCellStyle().getDataFormatString();
//        if (StringUtils.contains(dataFormat, "/")) {
//          value = getDateCellValue(cell);
//        }
//        else {
//          Number cellNumberValue = cell.getNumericCellValue();
//          if (dataFormat.equals("0") || dataFormat.equals("General")) {
//            dataFormat = "#0";
//            value = doNumberFormat(cellNumberValue, dataFormat);
//          }
//          if (dataFormat.equals("0.00")) {
//            dataFormat = "#0.00";
//            value = doNumberFormat(cellNumberValue, dataFormat);
//          }
//          else if (dataFormat.equals("\"R\"\\ #,##0.00")) {
//            dataFormat = "##0.###";
//            value = doNumberFormat(cellNumberValue, dataFormat);
//            value = "R " + value;
//          }
//          else {
//            // dataFormat = "0";
//            value = doNumberFormat(cellNumberValue, dataFormat);
//          }
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
//    }
//    else if (cell.getCellType() == 1) {
//      value = cell.getStringCellValue();
//    }
    return value;
  }

  /**
   * Determine the column count for this spreadsheet. This assumes that there
   * are no empty columns but accommodate for having empty rows at the top. It
   * also assumes that the first row with data is the header column and that all
   * headers are string values and not dates or numbers.
   *
   * @param workbook
   * @return the column count
   */
  protected int getColumnCount(Workbook workbook) {
    int count = 0;
    for (int r = 0; r <= workbook.getSheetAt(0).getPhysicalNumberOfRows(); r++) {
      Row row = workbook.getSheetAt(0).getRow(r);
      if (!isEmptyRow(row)) {
        for (int c = 0; row.getCell(c) != null && !StringUtils.isEmpty(row.getCell(c).getStringCellValue().trim()); c++) {
          count++;
        }
        return count;
      }
    }
    return count;
  }

  protected int getColumnCount(Workbook workbook, boolean emptyRows) {
    int count = 0;
    for (int r = 0; r <= workbook.getSheetAt(0).getPhysicalNumberOfRows(); r++) {
      Row row = workbook.getSheetAt(0).getRow(r);
//      if (!isEmptyRow(row)) {
      for (int c = 0; row.getCell(c) != null && !StringUtils.isEmpty(row.getCell(c).getStringCellValue().trim()); c++) {
        count++;
      }
      return count;
    }
    //}
    return count;
  }

  // -------------------------###---------------------------

  /**
   * Use this method to convert a CSV file (*.csv) to a Excel (*.xls) file. The
   * created Excel file will be created at the given file path and will have the
   * same file name as the input file but with a *.xls extension. The current
   * version of this utility assumes that the first line of the file to be
   * converted contains header information.
   *
   * @param filePath
   * @param fileName
   * @throws Exception
   */
  protected void convertCsvToXls(String filePath, String fileName) throws Exception {
    initialise();
//    if (logger.isDebugEnabled())
//      printColumnDataTypeMapping();
    String filePathAndName = (StringUtils.endsWith(filePath, "\\") ? filePath = "\\" : filePath) + fileName;
    List<String[]> cellGrid = createCellGrid(filePathAndName);
    Workbook workBook = doCsvToExcelConversion(cellGrid);
    if (workBook != null) {
      createXlsFile(workBook, filePath, fileName);
    }
  }

  /**
   * Use this method to convert a CSV files byte[] to a Excel byte array. The
   * current version of this utility assumes that the first line of the file to
   * be converted contains header information.
   *
   * @param csvBytes
   * @return byte[]
   * @throws Exception
   */
  public byte[] convertCsvToXls(byte[] csvBytes) throws Exception {
    initialise();
//    if (logger.isDebugEnabled())
//      printColumnDataTypeMapping();
    List<String[]> cellGrid = createCellGrid(csvBytes);
    Workbook workBook = doCsvToExcelConversion(cellGrid);
    byte[] xlsBytes = null;
    if (workBook != null) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      workBook.write(baos);
      xlsBytes = baos.toByteArray();
    }
    return xlsBytes;
  }

  /**
   * Creates a Excel file at a location provided from the provided workbook
   * information.
   *
   * @param workbook
   * @param fileName
   * @throws IOException
   */
  protected void createXlsFile(Workbook workbook, String filePath, String fileName) throws IOException {
    String fileNameNoExtension = StringUtils.substringBeforeLast(fileName, ".");
    File xlsFile = new File(filePath + fileNameNoExtension + ".xls");
    FileOutputStream fos = new FileOutputStream(xlsFile);
    workbook.write(fos);
    fos.flush();
    fos.close();
  }

  /**
   * This is an in memory representation of the CSV file created from a file on
   * a given location. The method returns a list of rows from a *.csv file: (
   * <code>List<String[]></code>).
   *
   * @param fileName
   * @return List<String[]>
   * @throws IOException
   */
  protected List<String[]> createCellGrid(String fileName) throws IOException {
    FileInputStream fis = new FileInputStream(fileName);
    InputStreamReader isr = new InputStreamReader(fis);
    BufferedReader br = new BufferedReader(isr);
    List<String[]> rows = new ArrayList<String[]>();
    String currentLine = "";
    while ((currentLine = br.readLine()) != null) {
      String[] row = currentLine.split(",");
      rows.add(row);
    }
    return rows;
  }

  /**
   * This is an in memory representation of the CSV file created from a CSV byte
   * array. The method returns a list of rows from a *.csv file: (
   * <code>List<String[]></code>).
   *
   * @param csvBytes
   * @return List<String[]>
   * @throws Exception
   */
  protected List<String[]> createCellGrid(byte[] csvBytes) throws Exception {
    int currRow = -1;
    List<String[]> rows = null;
    try {
      InputStream bais = new ByteArrayInputStream(csvBytes);
      InputStreamReader isr = new InputStreamReader(bais);
      BufferedReader br = new BufferedReader(isr);
      rows = new ArrayList<String[]>();
      String currentLine = "";
      while ((currentLine = br.readLine()) != null) {
        currRow++;
        String[] row = null;
        if (!currentLine.contains("\"")) {
          row = currentLine.split(",");
        }
        else {
          Pattern pattern = Pattern.compile(csvRegExpression);
          Matcher matcher = pattern.matcher(currentLine);
          List<String> rowList = new ArrayList<String>();
          while (matcher.find()) {
            rowList.add(StringUtils.left(matcher.group(), matcher.group().length() - 1)) ;
          }
          row = rowList.toArray(new String[rowList.size()]);
        }
        rows.add(row);
      }
    } catch (Exception e) {
      errorMessageInfo = "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + " Current row index: " + currRow;
//      logger.error(" Current row index: " + currRow);
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return rows;
  }

  /**
   * This is where the actual conversion takes place. CSV field values gets
   * converted into formatted Excel Cell values.
   *
   * @param cellGrid
   * @return Workbook
   * @throws Exception
   */
  protected Workbook doCsvToExcelConversion(List<String[]> cellGrid) throws Exception {
    int currCol = -1;
    int currRow = -1;
    Workbook workBook = new HSSFWorkbook();
    try {
      Sheet sheet = workBook.createSheet("sheet1");
      List<String> headers = getHeaders(cellGrid.get(0));
      iniCellStyleMap(workBook, headers);
      Row row = sheet.createRow(0);
      for (int c = 0; c < headers.size(); c++) {
        String header = headers.get(c);
        Cell cell = row.createCell(c);
        CellStyle style = workBook.createCellStyle();
        style.setDataFormat((short) 0);
        cell.setCellStyle(style);
        cell.setCellValue(header);
      }
      for (int r = 1; r < cellGrid.size(); r++) {
        currRow = r;
        String[] rowData = cellGrid.get(r);
        row = sheet.createRow(r);
        for (int c = 0; c < rowData.length; c++) {
          currCol = c;
          String currHeader = headers.get(c);
          String value = rowData[c];
//          if (value.contains(",")) {
//            value = "\"" + value + "\""; //escaping commas
//          }
          CsvFieldDataType dataType = dataTypeMap.get(currHeader);
          Cell cell = row.createCell(c, Cell.CELL_TYPE_BLANK);
          cell = createCellAttributes(cell, dataType, value, cellStyleMap.get(c));
        }
      }

    } catch (Exception e) {
      errorMessageInfo =
          "createCellGrid(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nCurrent column index: " + currCol
              + " Current row index: " + currRow;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
    return workBook;
  }

  /**
   * Initialises the cell style map which will be used to cache excel CellStyle
   * objects. The key is the zero based column index.
   * <p/>
   * colCount
   *
   * @throws Exception
   */
  protected void iniCellStyleMap(Workbook workBook, List<String> headers) throws Exception {
    String header = null;
    try {
//      logger.info("Start Method: iniCellStyleMap");
//      logger.info("Header count: " + headers.size());
      cellStyleMap = new HashMap<Integer, CellStyle>();
      int targetColIndex = 0;
      int col = 0;
      for (Map.Entry<String, CsvFieldDataType> entry : dataTypeMap.entrySet()) {
//        if (sourceMappedColumns[col++] == 1) {
        CsvFieldDataType currDataType = entry.getValue();
        CellStyle style = workBook.createCellStyle();
        DataFormat format = workBook.createDataFormat();
        style.setDataFormat(format.getFormat(currDataType.getPattern()));
        cellStyleMap.put(targetColIndex++, style);
//        }
      }
//      logger.info("End Method: iniCellStyleMap");
    } catch (Exception e) {
      errorMessageInfo = "iniCellStyleMap(): \nMessage: " + e.getMessage() + "\nCause: " + e.getCause() + "\nColumn: " + header;
//      logger.error(errorMessageInfo, e);
      throw e;
    }
  }

  /**
   * Switches to different formatting algorithms depending on the column data
   * type.
   * <p/>
   * workBook
   *
   * @param cell
   * @param dataType
   * @param value
   * @return Cell
   */
  protected Cell createCellAttributes(Cell cell, CsvFieldDataType dataType, String value, CellStyle style) {
    if (dataType == CsvFieldDataType.CURRENCY) {
      cell = setCurrencyAttributes(cell, value, style);
    }
    else if (dataType == CsvFieldDataType.DATE) {
      cell = setDateAttributes(cell, value, style);
    }
    else if (dataType == CsvFieldDataType.DECIMAL) {
      cell = setDecimalAttributes(cell, value, style);
    }
    else if (dataType == CsvFieldDataType.INTEGER) {
      cell = setIntegerAttributes(cell, value, style);
    }
    else if (dataType == CsvFieldDataType.STRING) {
      cell.setCellStyle(style);
      cell.setCellValue(value);
    }
    return cell;
  }

  /**
   * Creates a correctly formatted Cell value for a CSV currency field value.
   * <p/>
   * workBook
   *
   * @param cell  dataType
   * @param value
   * @return Cell
   */
  protected Cell setCurrencyAttributes(Cell cell, String value, CellStyle style) {
    cell.setCellStyle(style);
    if (!StringUtils.isEmpty(value)) {
      Double doubleValue = Double.valueOf(value);
      cell.setCellValue(doubleValue);
    }
    return cell;
  }

  /**
   * Creates a correctly formatted Cell value for a CSV date field value.
   * <p/>
   * workBook
   *
   * @param cell  dataType
   * @param value
   * @return Cell
   */
  protected Cell setDateAttributes(Cell cell, String value, CellStyle style) {
    cell.setCellStyle(style);
    if (!StringUtils.isEmpty(value)) {
      Pattern pattern = Pattern.compile(dateRegExpression);
      Matcher matcher = pattern.matcher(value);
      if (matcher.matches()) {
        try {
          DateFormat df = new SimpleDateFormat(dateFormat);
          Date dateValue = df.parse(value);
          cell.setCellValue(dateValue);
        } catch (ParseException e) {
          // "User error, NOT formatting issue. Keep value as is."
          e.printStackTrace();
          style.setDataFormat((short) 0);
          cell.setCellStyle(style);
          cell.setCellValue(value);
        }
      }
      else {
        // "User error, NOT formatting issue. Keep value as is."
        style.setDataFormat((short) 0);
        cell.setCellStyle(style);
        cell.setCellValue(value);
      }
    }
    return cell;
  }

  /**
   * Creates a correctly formatted Cell value for a CSV decimal field value.
   * <p/>
   * workBook
   *
   * @param cell  dataType
   * @param value
   * @return Cell
   */
  protected Cell setDecimalAttributes(Cell cell, String value, CellStyle style) {
    cell.setCellStyle(style);
    if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
      Double doubleValue = Double.valueOf(value);
      cell.setCellValue(doubleValue);
    }
    return cell;
  }

  /**
   * Creates a correctly formatted Cell value for a CSV integer field value.
   * <p/>
   * workBook
   *
   * @param cell  dataType
   * @param value
   * @return Cell
   */
  protected Cell setIntegerAttributes(Cell cell, String value, CellStyle style) {
    cell.setCellStyle(style);
    if (!StringUtils.isEmpty(value) && StringUtils.isNumeric(value)) {
      Long longValue = Long.valueOf(value);
      cell.setCellValue(longValue);
    }
    return cell;
  }

  private void printColumnDataTypeMapping() {
//    logger.debug("------FileConversionUtil: START OF COLUMN DATA TYPE PRINTOUT------");
    for (String key : dataTypeMap.keySet()) {
//      logger.debug("------");
      CsvFieldDataType value = dataTypeMap.get(key);
//      logger.debug("Column Name    : " + key);
//      logger.debug("Column DataType: " + value);
    }
//    logger.debug("------END OF COLUMN DATA TYPE PRINTOUT------");
  }

  public boolean isErrorsFound() {
    return errorsFound;
  }

  public void setErrorsFound(boolean errorsFound) {
    this.errorsFound = errorsFound;
  }

  public String getErrorMessageInfo() {
    return errorMessageInfo;
  }

  public void setErrorMessageInfo(String errorMessageInfo) {
    this.errorMessageInfo = errorMessageInfo;
  }

  private int getColumnIndexFromName(String columnName) {
    for (int i = 0; i < headers.size(); i++) {
      if (StringUtils.equals(headers.get(i), columnName)) {
        return i;
      }
    }
    return -1;
  }

  public Set<String> getDistinctColumnValues(String columnName, int headerLineNo, FileType fileType, byte[] xlsBytes) {

    Set<String> distinctValueSet = new HashSet<String>();

    try {
      workbook = createWorkbook(fileType, xlsBytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
    List<String> headerList = getHeaders(workbook, headerLineNo);
    int columnIndex = 0;
    for (int i = 0; i < headerList.size(); i++) {
      if (headerList.get(i).equalsIgnoreCase(columnName)) {
        columnIndex = i;
        break;
      }
    }
    return getDistinctColumnValues(columnIndex, headerLineNo, fileType, xlsBytes);
  }

  /**
   * @param columnIndex
   * @param headerLineNo
   * @param fileType
   * @param xlsBytes
   * @return
   */
  public Set<String> getDistinctColumnValues(int columnIndex, int headerLineNo, FileType fileType, byte[] xlsBytes) {

    Set<String> distinctValueSet = new HashSet<String>();

    if (workbook == null) {
      try {
        workbook = createWorkbook(fileType, xlsBytes);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Sheet worksheet = workbook.getSheetAt(0);
    int numberOfRows = worksheet.getPhysicalNumberOfRows();
    Iterator<Row> rowIterator = worksheet.rowIterator();
    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      if (row.getRowNum() != headerLineNo - 1) {
        Cell cell = row.getCell(columnIndex);
        String cellStringValue = getCellValue(cell);
        if (StringUtils.isNotBlank(cellStringValue)) {
          distinctValueSet.add(cellStringValue.trim());
        }
      }
    }

    return distinctValueSet;
  }

  private int getColumnIndexFromName(Workbook workbook, int headerLineNo, String columnName) {
    Row currRow = workbook.getSheetAt(0).getRow(headerLineNo - 1);
    int index = -1;
    while (currRow.cellIterator().hasNext()) {
      index++;
      Cell currCell = currRow.cellIterator().next();
      String cellStringValue = getCellValue(currCell);
      if (cellStringValue.equals(columnName)) {
        return 0;
      }
    }

//    for(int col = 0; currRow.cellIterator()){
//
//    }
//    for (int i = 0; i < headers.size(); i++) {
//      if (StringUtils.equals(headers.get(i), columnName)) {
//        return i;
//      }
//    }
    return -1;
  }

  protected List<String> getHeaders(Workbook workbook, int headerLineNo) {

    List<String> headers = new ArrayList<String>();
    Row row = workbook.getSheetAt(0).getRow(headerLineNo - 1);
    if (row == null) {
      throw new IllegalStateException("null header row in upload file");
    }
    int lastColNo = row.getLastCellNum();
    for (int i = 0; i < lastColNo; i++) {
      Cell cell = row.getCell(i, Row.RETURN_BLANK_AS_NULL);
      String headerName = this.getCellValue(cell);
      headerName = headerName != null ? headerName.trim() : "";
      headers.add(headerName);
    }
    return headers;
  }

  /**
   * Retrieves the first not null/empty cell value in a column vertically below the header line for all columns and
   * returns a column index to value mapping.
   *
   * @return the cell value as a String
   */
  //TODO check performance on large upload files, especially those with 'deep' values
  //abort after x amount of rows with out finding values for certain cells??
  public Map<Integer, String> getFistVerticalColumnValues(FileType fileType, byte[] xlsBytes, int headerLineNo) {

    Map<Integer, String> colNameValueMap = new LinkedHashMap<Integer, String>();
    int currRowIndex = headerLineNo;
    Row currRow = null;
    int dynamicHeaderCount = 0;
    try {
      workbook = createWorkbook(fileType, xlsBytes);
      //initial dynamic header list
      List<String> headerList = getHeaders(workbook, headerLineNo);
      dynamicHeaderCount = headerList.size();
      //ini colNameValueMap with headers
      int index = 0;
      for (String header : headerList) {
        colNameValueMap.put(index++, null);
      }
      while ((currRow = getNextNotEmptyRow(currRowIndex)) != null) {
        currRowIndex = currRow.getRowNum();
        int lastColValueIndex = currRow.getLastCellNum();
        //check if for empty header columns with values
        if (lastColValueIndex > (dynamicHeaderCount)) { //headerList.size()
          int jump = lastColValueIndex - (dynamicHeaderCount);//headerList.size()
          for (int c = 0; c < jump; c++) {
            headerList.add("<<empty-" + c + ">>");
          }
          dynamicHeaderCount = lastColValueIndex;
        }
        //get the cell values for the current row
        for (int i = 0; i < dynamicHeaderCount; i++) {
          //no cell read needed if value is known
          if (!colNameValueMap.containsKey(i) || StringUtils.isBlank(colNameValueMap.get(i))) {
            Cell cell = currRow.getCell(i, Row.RETURN_BLANK_AS_NULL);
            String cellValue = getCellValue(cell);
            if (!StringUtils.isEmpty(cellValue)) {
              colNameValueMap.put(i, cellValue);
            }
          }
        }
        currRowIndex++;
      }
      //add columns with no data
      for (int c = 0; c < colNameValueMap.size(); c++) {
        if (!colNameValueMap.containsKey(c)) {
          colNameValueMap.put(c, null);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return colNameValueMap;
  }

  //create this method: Row getNextNotEmptyRow(int currRowIndex)

  /**
   * Cater for empty rows according to requirements. Allow for a max of 10 empty rows to prevent endless loops.
   *
   * @param startIndex
   * @return Row
   */
  private Row getNextNotEmptyRow(int startIndex) {
    Row currRow = null;
    for (int r = startIndex; r <= startIndex + 15; r++) {
      currRow = workbook.getSheetAt(0).getRow(r);
      if (!isEmptyRow(currRow)) {
        return currRow;
      }
    }
    return currRow;
  }

  /**
   * Retrieves the first not null/empty cell value in a column below the header line for a specific column.
   *
   * @return the cell value as a String
   */
  //TODO check performance on large upload files, especially those with 'deep' values
  public String getFistVerticalColumnValue(FileType fileType, byte[] xlsBytes, int headerLineNo, String columnName) {

    String cellStringValue = null;
    try {
      workbook = createWorkbook(fileType, xlsBytes);
      int columnIndex = getColumnIndexFromName(columnName);
      if (columnIndex != -1) {
        int currRowIndex = headerLineNo;
        while (!isEmptyRow(workbook.getSheetAt(0).getRow(currRowIndex))) {
          Row row = workbook.getSheetAt(0).getRow(currRowIndex);
          Cell currCell = row.getCell(currRowIndex);
          cellStringValue = getCellValue(currCell);
          if (!StringUtils.isEmpty(cellStringValue)) {
            return cellStringValue;
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return cellStringValue;
  }

  public Workbook getWorkbook() {
    return workbook;
  }

  public void setWorkbook(Workbook workbook) {
    this.workbook = workbook;
  }

  protected int[] getHeaderIndex() {
    return headerIndex;
  }

  protected void setHeaderIndex(int[] headerIndex) {
    this.headerIndex = headerIndex;
  }
}

//  String[] values = new String[headers.size() - 1];
//  try {
//    workbook = createWorkbook(fileType, xlsBytes);
//    int currRowIndex = 0;
//    while(!isEmptyRow(workbook.getSheetAt(0).getRow(currRowIndex))){
//      Row currRow = workbook.getSheetAt(0).getRow(currRowIndex);
//      for (int col = 0; col < values.length; col++) {
//        Cell currCell = rowList.get(x).get(columnIndex);
//        String cellStringValue = getCellValue(currCell);
//        if (!StringUtils.isEmpty(cellStringValue)) {
//          return cellStringValue;
//        }
//      }
//    }
//    for (int row = headerLineNo - 1; row < values.length; row++) {
//
//    }
//  } catch (IOException e) {
//    e.printStackTrace();
//  }


//  protected List<String[]> getRowData(Workbook workbook, int startIndex, int endIndex) {
//    List<String[]> lineData = new ArrayList<String[]>();
//    List<String> rowData = new ArrayList<String>();
//    int colCount = getColumnCount(workbook);
//    for (int r = startIndex; r <= endIndex; r++) {
//      Row row = workbook.getSheetAt(0).getRow(r);
//      if (!isEmptyRow(row)) {
//        rowData = new ArrayList<String>();
//        for (int c = 0; c < colCount; c++) {
//          String value = getCellValue(row.getCell(c)); // row.getCell(c).getStringCellValue().trim();
//          rowData.add(value);
//        }
//        lineData.add(rowData.toArray(new String[colCount]));
//      }
//    }
//    return lineData;
//  }

//  protected List<String[]> retrieveDataRangeXLS(byte[] xlsBytes, int startIndex, int endIndex) throws IOException {
//    List<String[]> rows = new ArrayList<String[]>();
//    workbook = createWorkbook(FileType.XLS, xlsBytes);
//    rows = getRowData(workbook, startIndex, endIndex);
//    return rows;
//  }


// public byte[] createCsvBytes(List<Object> dataSet, LinkedHashMap<String,
// DataType> recordMetaData)
// {
// String csvString = "";
// for (int row = 0; row < dataSet.size(); row++)
// {
// Object data = dataSet.get(row);
// for (String fieldMetaData : recordMetaData.keySet())
// {
// getFieldValue(data, fieldMetaData);
// }
// }
// return null;
// }
//
// protected Object getFieldValue(Object value, String fieldNames)
// {
// Object currValue = value;
// String[] fields = getFields(fieldNames);
// for (String fieldName : fields)
// {
// try
// {
// Field field = value.getClass().getDeclaredField(fieldName);
// Field currField = getField(currValue, fieldName);
// }
// catch (SecurityException e)
// {
// e.printStackTrace();
// }
// catch (NoSuchFieldException e)
// {
// e.printStackTrace();
// }
//
// }
//
// return null;
// }
//
//
// protected Field getField(Object value, String fieldName)
// {
// try
// {
// Field field = value.getClass().getDeclaredField(fieldName);
// return field;
// }
// catch (SecurityException e)
// {
// e.printStackTrace();
// }
// catch (NoSuchFieldException e)
// {
// e.printStackTrace();
// }
// return null;
// }
//
// protected String[] getFields(String fields)
// {
// String[] fieldArr = fields.split(":");
// return fieldArr;
// }