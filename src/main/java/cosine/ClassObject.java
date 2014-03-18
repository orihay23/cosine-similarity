package cosine;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

public class ClassObject {

  HashMap<Integer, String> lineNumbersToCode = new HashMap<Integer, String>();
  HashMap<Integer, String> lineNumbersToMethods =
      new HashMap<Integer, String>();
  TreeMap<String, ArrayList<Integer>> vulnerabilities =
      new TreeMap<String, ArrayList<Integer>>(); //
  CompilationUnit cu;
  String fileName;
  String absolutePathName;// for checking filenames that are duplicated across
                          // packages
  String canonicalPath;
  String path;

  public TreeMap<String, ArrayList<Integer>> getVulnerabilities() {
    return vulnerabilities;
  }

  public void setVulnerabilities(
      TreeMap<String, ArrayList<Integer>> vulnerabilities) {
    this.vulnerabilities = vulnerabilities;
  }

  public String getCanonicalPath() {
    return canonicalPath;
  }

  public void setCanonicalPath(String canonicalPath) {
    this.canonicalPath = canonicalPath;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public CompilationUnit getCu() {
    return cu;
  }

  public void setCu(CompilationUnit cu) {
    this.cu = cu;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getAbsolutePathName() {
    return absolutePathName;
  }

  public void setAbsolutePathName(String absolutePathName) {
    this.absolutePathName = absolutePathName;
  }

  public HashMap<Integer, String> getLineNumbersToCode() {
    return lineNumbersToCode;
  }

  public void setLineNumbersToCode(HashMap<Integer, String> lineNumbersToCode) {
    this.lineNumbersToCode = lineNumbersToCode;
  }

  public HashMap<Integer, String> getLineNumbersToMethods() {
    return lineNumbersToMethods;
  }

  public void setLineNumbersToMethods(
      HashMap<Integer, String> lineNumbersToMethods) {
    this.lineNumbersToMethods = lineNumbersToMethods;
  }

  public void setLineNumbersFromFile(File file) throws FileNotFoundException {
    int count = 1;
    Scanner fileScanner = new Scanner(file);

    while (fileScanner.hasNextLine()) {
      lineNumbersToCode.put(count, fileScanner.nextLine());
      count++;
    }
    fileScanner.close();
  }

  public static void main(String[] args) throws FileNotFoundException {

    ClassObject temp = new ClassObject();
    try {
      int a = 0;
      temp.setLineNumbersFromFile(new File(
          "F:\\JavaParser\\src\\ClassObject.java"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    System.out.println(temp.getLineNumbersToCode());

    FileInputStream in =
        new FileInputStream("F:\\JavaParser\\src\\ClassObject.java");

    // FileInputStream in = new
    // FileInputStream("E:\\Eclipse Workspace\\SampleProject\\src\\Requirement.java");

    CompilationUnit cu = null;
    try {
      // parse the file
      cu = JavaParser.parse(in);
      System.out.println("THIS IS THE TYPES: " + cu.getTypes().size());
    } catch (Exception e) {} finally {
      try {
        in.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    // visit and print the methods names
    new MethodVisitor().visit(cu, null);

  }

  /**
   * Simple visitor implementation for visiting MethodDeclaration nodes.
   */
  private static class MethodVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(MethodDeclaration n, Object arg) {
      // here you can access the attributes of the method.
      // this method will be called for all methods in this
      // CompilationUnit, including inner class methods

      System.out.println(n.getBeginLine());
      System.out.println(n.getName());
      System.out.println(n.getBody());

    }

    @Override
    public void visit(PackageDeclaration n, Object arg) {
      // here you can access the attributes of the method.
      // this method will be called for all methods in this
      // CompilationUnit, including inner class methods

      System.out.println(n.getBeginLine());
      System.out.println(n.getName());
      System.out.println(n.getClass());

    }

  }
}
