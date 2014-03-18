package cosine;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;
import japa.parser.ast.stmt.BlockStmt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeMap;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class ParseUtility {

  static ArrayList<ClassObject> listOfClassObjects =
      new ArrayList<ClassObject>();

  public static ArrayList<String> findDuplicateFileNames(
      ArrayList<String> dup,
      HashMap<String, String> master,
      final File folder) throws ParseException, IOException {

    for (final File fileEntry : folder.listFiles()) {

      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
        findDuplicateFileNames(dup, master, fileEntry);
      } else {
        if (fileEntry.isFile()) {
          String fileName = fileEntry.getName();
          if ((fileName.substring(
              fileName.lastIndexOf('.') + 1,
              fileName.length()).toLowerCase()).equals("java")) {
            if (master.containsKey(fileName)) {
              dup.add(fileEntry.getAbsolutePath());
            } else {
              master.put(fileName, "");
            }
          }
        }
      }
    }

    return dup;
  }

  /*
   * Pass in a folder, and this method will generate a series of compilation
   * units for every class
   */
  public static ArrayList<ClassObject> generateClassObjects(final File folder)
      throws ParseException,
      IOException {

    for (final File fileEntry : folder.listFiles()) {
      ClassObject obj = new ClassObject();
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
        generateClassObjects(fileEntry);
      } else {
        if (fileEntry.isFile()) {
          String fileName = fileEntry.getName();
          if ((fileName.substring(
              fileName.lastIndexOf('.') + 1,
              fileName.length()).toLowerCase()).equals("java")) {
            // System.out.println(fileName);
            obj.setFileName(fileName);
            obj.setAbsolutePathName(fileEntry.getAbsolutePath());
            obj.setCanonicalPath(fileEntry.getCanonicalPath());
            obj.setPath(fileEntry.getPath());
            FileInputStream in = new FileInputStream(fileEntry);
            CompilationUnit cu = JavaParser.parse(in);
            obj.setCu(cu);
            listOfClassObjects.add(obj);
            // new MethodVisitor().visit(cu, null);
            // listOfCompiltationUnits.add(cu);
            // System.out.println(listOfCompiltationUnits.size());
            // in.close();
          }
        }
      }
    }
    return listOfClassObjects; // listOfCompiltationUnits;
  } // end of method

  public static void generateListOfVulnerabilitiesByClass(
      ArrayList<ClassObject> objList,
      File file) throws BiffException, IOException {

    // TreeMap<String, ArrayList<Integer>> key = new TreeMap<String,
    // ArrayList<Integer>>();
    Workbook workbook = Workbook.getWorkbook(file);// new
                                                   // File("C:\\Users\\550988\\Desktop\\SSAT Analysis\\SRWNM_1_0_3_if-else_BB.xls"));
    Sheet sheet = workbook.getSheet(0);
    Cell[] lineNumbers = sheet.getColumn(10);
    Cell[] fileNames = sheet.getColumn(8);
    // System.out.println("This is the number of rows found with content: " +
    // fileNames.length);
    // System.out.println("This is the number of rows found with content LineNumbers: "
    // + lineNumbers.length);

    for (int i = 1; i < lineNumbers.length; i++) {
      // System.out.println("This is the interation value: " + i);
      String stringa1 = lineNumbers[i].getContents();
      String fileName = fileNames[i].getContents().trim();
      if (fileName.isEmpty())
        continue;
      // System.out.println("excel content: " + fileName);
      for (ClassObject obj : objList) {
        if (fileName.equals(obj.getFileName())
            ||
            ((fileName.contains("/") || fileName.contains("\\")) && obj.getAbsolutePathName()
                .contains(fileName))) {
          ArrayList<Integer> list = ParseUtility.parseLineNumbers(stringa1);
          TreeMap<String, ArrayList<Integer>> key = obj.getVulnerabilities();
          if (key.containsKey(fileName)) {
            key.get(fileName).addAll(list);
          } else {
            key.put(fileName, list);
          }
          obj.setVulnerabilities(key);
          // System.out.println(obj.getVulnerabilities().toString());
        } else {}

      }// end of inner for

    }
  }

  /*parses line number from vulernabilities excel sheet:  i.e  75-69,100,1000-1059 = 75,100,1000*/
  public static ArrayList<Integer> parseLineNumbers(String values) {

    ArrayList<Integer> list = new ArrayList<Integer>();
    int firstValue = 0;
    int secondValue = 0;

    StringTokenizer tokenizer = new StringTokenizer(values, ", ");
    while (tokenizer.hasMoreTokens()) {
      String numbers = tokenizer.nextToken().trim();
      // System.out.println(numbers);
      if (numbers.contains("-")) {
        StringTokenizer hyphenRemover = new StringTokenizer(numbers, "-");
        String temp = hyphenRemover.nextToken().trim();
        // System.out.println(temp);
        firstValue = Integer.parseInt(temp);
        // secondValue = Integer.parseInt(hyphenRemover.nextToken());
      } else {

        firstValue = Integer.parseInt(numbers);
      }
      list.add(firstValue);
    }
    // System.out.println("THIS IS THE ARRAY LIST: " + list.toString());
    return list;
  }

  public static TreeMap<String, Integer> generateMasterTokenMap() {
    TreeMap<String, Integer> master = new TreeMap<String, Integer>();
    for (ClassObject cu : TermExtractor.sourceObjects) {
      List<TypeDeclaration> types = cu.getCu().getTypes();
      for (TypeDeclaration type : types) {
        List<BodyDeclaration> members = type.getMembers();
        for (BodyDeclaration member : members) {
          TreeMap<String, Integer> methodTerms = new TreeMap<String, Integer>();
          if (member instanceof MethodDeclaration) {
            MethodDeclaration method = (MethodDeclaration) member;
            
           //TODO: add the tokens from the method signature into the method term map and master map
            method.getAnnotations(); 
            method.getType(); 
            method.getTypeParameters(); 
            method.getThrows(); 
            method.getParameters();
                 
            BlockStmt body = method.getBody();
            if (body == null) {
            //  System.out.println(method);
              // TODO: breaking on abstract/interfaces
              break;
            }
            StringTokenizer s =
                    new StringTokenizer(body.toString().replaceAll(
                    		"\"(?:\\\\\"|[^\"])*?\"",
                            ""), " \n\t.;)(}{][:\"+-/*<>|||&&&!=@~!@#$%^&*,\\'?");
           /* StringTokenizer s =
                new StringTokenizer(body.toString().replaceAll(
                    "\"(?:\\\\\"|[^\"])*?\"",
                    ""), ",�.,;)(}{][�\"+-/*�|||&&&!=@  ");*/
            while (s.hasMoreElements()) {
              String term = s.nextToken();
              if (methodTerms.containsKey(term)) {
                // count of terms per method
                methodTerms.put(term, methodTerms.get(term) + 1);
              } else {
                methodTerms.put(term, 1);
                if (master.containsKey(term)) {
                  // # of docs with term in them
                  master.put(term, master.get(term) + 1);
                } else {
                  master.put(term, 1);
                }
              }
            }
            method.setMethodTerms(methodTerms);
          }
        }
      }

    }

    return master;
  }

  // TODO create function to find correct column in spreadsheet (File Name and
  // Line Number columns)
  // TODO might want to get the category, the concern type, possibly subcategory
  // TODO create a method in a generic way to perform the below task.
  // TODO create method to compare the below hashmap of line numbers indicating
  // location of the vulnerability to
  // the correct function/method in the source using the compilation unit.
  public static void main(String[] args) throws BiffException, IOException {

    // System.out.println(ParseUtility.parseLineNumbers("10-48, 50, 60, 100-173, 210-345"));

    TreeMap<String, ArrayList<Integer>> key =
        new TreeMap<String, ArrayList<Integer>>();
    Workbook workbook =
        Workbook.getWorkbook(new File(
            "C:\\Users\\550988\\Desktop\\SSAT Analysis\\SRWNM_1_0_3_if-else_BB.xls"));
    Sheet sheet = workbook.getSheet(0);
    Cell[] lineNumbers = sheet.getColumn(11);
    Cell[] fileNames = sheet.getColumn(9);

    for (int i = 1; i < lineNumbers.length; i++) {

      String stringa1 = lineNumbers[i].getContents();
      String fileName = fileNames[i].getContents();
      ArrayList<Integer> list = ParseUtility.parseLineNumbers(stringa1);
      if (key.containsKey(fileName)) {
        key.get(fileName).addAll(list);
      }
      key.put(fileName, list);
    }
    System.out.println(key.values().toString());
    System.out.println(key.keySet().toString());
  }// end of main

}
