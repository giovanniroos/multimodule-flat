package za.dsc.grp.lib.common.types;

/**
 * @author Giovanni Roos
 */

public enum FileType {
  UNKNOWN("", "", ""),
  PDF("PDF", "application/pdf", "pdf"),
  HTML("HTML", "text/html", "html"),
  CSV("CSV", "text/comma-separated-values", "csv"),
  XLS("XLS", "application/vnd.ms-excel", "xls"),
  XLSX("XLSX", "application/vnd.ms-excel", "xlsx"),
  TEXT("TEXT", "text/plain", "txt"),
  RTF("RTF", "application/rtf", "rtf"),
  XML("XML", "application/xml", "xml"),
  JPEG("JPEG", "image/jpeg", "jpg"),
  PNG("PNG", "image/png", "png");

  private String code;
  private String contentType;
  private String contentExtension;


  FileType(String code, String contentType, String contentExtension) {

    this.code = code;
    this.contentType = contentType;
    this.contentExtension = contentExtension;
  }

  public String getContentType() {
    return contentType;
  }

  public String getContentExtension() {
    return contentExtension;
  }

  public String getCode() {
    return code;
  }

  public static FileType getFileTypeByContentExtension(String extension) {
    for (FileType type : FileType.values()) {
      if (type.contentExtension.equalsIgnoreCase(extension)) {
        return type;
      }
    }
    return FileType.UNKNOWN;
  }
}
