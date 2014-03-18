package cosine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TfIdf {
  private Map<String, Integer> masterWords;

  private TfIdf(Map<String, Integer> masterWords) {
    this.masterWords = masterWords;
  }

  static TfIdf fromMaster(
      Map<String, Integer> masterWords) {
    return new TfIdf(masterWords);
  }

  List<Integer> createArray(Method m) {
    List<Integer> ints = new ArrayList<Integer>();
    Map<String, Integer> methodCount = m.getWordCount();
    for (String t : masterWords.keySet()) {
      if (methodCount.containsKey(t)) {
        ints.add(methodCount.get(t));
      } else {
        ints.add(0);
      }
    }
    return ints;
  }
}
