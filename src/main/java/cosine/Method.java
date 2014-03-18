package cosine;

import java.util.Map;

public class Method {
  private final String fileName;
  private final String absolutePathName;
  private final Integer lineNumber;
  private final Map<String, Integer> wordCount;

  public Method(
      String fileName,
      String absolutePathName,
      Integer lineNumber,
      Map<String, Integer> wordCount) {
    this.fileName = fileName;
    this.absolutePathName = absolutePathName;
    this.lineNumber = lineNumber;
    this.wordCount = wordCount;
  }

  public String getFileName() {
    return fileName;
  }

  public String getAbsolutePathName() {
    return absolutePathName;
  }

  public Integer getLineNumber() {
    return lineNumber;
  }

  public Map<String, Integer> getWordCount() {
    return wordCount;
  }
}
