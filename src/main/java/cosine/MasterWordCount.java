package cosine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterWordCount {
  static Map<String, Integer> count(List<Method> methods) {
    Map<String, Integer> masterWords = new HashMap<String, Integer>();
    // counts number of methods in which term exists
    for (Method m : methods) {
      for (String t : m.getWordCount().keySet()) {
        if (masterWords.containsKey(t)) {
          masterWords.put(t, masterWords.get(t) + 1);
        } else {
          masterWords.put(t, 1);
        }
      }
    }
    return masterWords;
  }
}
