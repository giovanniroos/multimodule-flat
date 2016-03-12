package za.dsc.grp.lib.common.types;

/**
 * @author Giovanni Roos
 */

public enum CsvFieldDataType {
  INTEGER("I", "Integer", "0"),
  DECIMAL("N", "Decimal", "0.00"),
  CURRENCY("C", "Currency", "0.00"),
  DATE("D", "Date", "dd/MM/yyyy"),
  STRING("S", "String", ""),
  EMPTY("E", "Empty", "");
  private String code;
  private String description;
  private String pattern;
  private String xmlDataType;

  private CsvFieldDataType(String code, String description, String pattern) {
    this.code = code;
    this.description = description;
    this.pattern = pattern;
    this.xmlDataType = xmlDataType;
  }

  public static synchronized CsvFieldDataType getEnumValue(String code) {
    for (CsvFieldDataType dataType : CsvFieldDataType.values()) {
      if (dataType.getCode().equals(code)) {
        return dataType;
      }
    }
    return null;
  }

  public static synchronized CsvFieldDataType getEnumByXmlDataType(String xmlDataType) {
    for (CsvFieldDataType dataType : CsvFieldDataType.values()) {
      if (dataType.getXmlDataType().equals(xmlDataType)) {
        return dataType;
      }
    }
    return null;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public String getXmlDataType() {
    return xmlDataType;
  }

  public void setXmlDataType(String xmlDataType) {
    this.xmlDataType = xmlDataType;
  }
}
