package cosine;

import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.read.biff.BiffException;

public class TermExtractor {
  public static ArrayList<ClassObject> sourceObjects;

 // List<Method>
  static void fromFile(File src)
      throws ParseException,
      IOException, BiffException {
    /* 1. Build Class objects from source */
    sourceObjects =
        ParseUtility.generateClassObjects(src);
    // /*
    // * Map vulnerabilites to classes to include line numbers of
    // vulnerabillites
    // * and set to the instance of a ClassObject
    // */
    // ParseUtility.generateListOfVulnerabilitiesByClass(
    // sourceObjects,
    // assessment);

    //System.out.println(sourceObjects);
    // Create list of Method where each has a term frequency map
  /*  List<Method> methods = new ArrayList<Method>();
    for (ClassObject obj : sourceObjects) {
      obj.setLineNumbersToMethods(generateLineNumToMethod(obj.getCu()));
      HashMap<Integer, String> lineNumbersToMethods =
          obj.getLineNumbersToMethods();
      for (Integer i : lineNumbersToMethods.keySet()) {
        String methodBody = lineNumbersToMethods.get(i);
        Map<String, Integer> wordCount = countWords(methodBody);
        methods.add(new Method(
            obj.getFileName(),
            obj.getAbsolutePathName(),
            i,
            wordCount));
      }

    }*/
    //return methods;

  }

  private static HashMap<Integer, String> generateLineNumToMethod(
      CompilationUnit cu) {
    HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    List<TypeDeclaration> types = cu.getTypes();
    for (TypeDeclaration type : types) {
      List<BodyDeclaration> members = type.getMembers();
      for (BodyDeclaration member : members) {
        if (member instanceof MethodDeclaration) {
          MethodDeclaration method = (MethodDeclaration) member;

          int lineNumber = method.getBeginLine();
          String name = method.getName();
          hashMap.put(lineNumber, name);
        }
      }
    }

    return hashMap;
  }

  private static Map<String, Integer> countWords(String methodBody) {
    // TODO Auto-generated method stub
    return null;
  }
}
