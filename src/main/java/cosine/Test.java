package cosine;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

public class Test {
  static ArrayList<CompilationUnit> listOfCompiltationUnits =
      new ArrayList<CompilationUnit>();
  static HashMap<String, Integer> masterTokens = new HashMap<String, Integer>();

  public static ArrayList<CompilationUnit> generateCompilationUnits(
      final File folder) throws ParseException, IOException {
    for (final File fileEntry : folder.listFiles()) {
      if (fileEntry.isDirectory()) {
        // System.out.println("Reading files under the folder "+folder.getAbsolutePath());
        generateCompilationUnits(fileEntry);
      } else {
        if (fileEntry.isFile()) {
          String temp = fileEntry.getName();
          if ((temp.substring(temp.lastIndexOf('.') + 1, temp.length()).toLowerCase()).equals("java")) {
            FileInputStream in = new FileInputStream(fileEntry);
            CompilationUnit cu = JavaParser.parse(in);
            // new MethodVisitor().visit(cu, null);
            listOfCompiltationUnits.add(cu);
            // System.out.println(listOfCompiltationUnits.size());
            // in.close();
          }
        }
      }
    }
    return listOfCompiltationUnits;
  } // end of method

  public static void findKeywords(BlockStmt code) {

    if (code.toString().contains("for")) {
      int value = masterTokens.get("for");
      value++;
      masterTokens.put("for", value);
    }

  }

  public static void main(String[] args) throws Exception {

    // ArrayList<CompilationUnit> list = generateCompilationUnits(new
    // File("F:\\SRWNM 1.0.2"));

    // for(CompilationUnit cu : list){
    // new MethodVisitor().visit(cu, null);

    // }

    FileInputStream f =
        new FileInputStream(
            "D:\\Smart_Data\\Behe\\cosine-similarity\\src\\main\\java\\japa\\parser\\ast\\stmt\\DoStmt.java");
    CompilationUnit a = null;
    try {
      a = JavaParser.parse(f);

    } catch (Exception e) {

    } finally {
      f.close();
    }
    List<String> tokens = new ArrayList<String>();
    new MethodVisitor().visit(a, tokens);
    System.out.println(tokens);
  }

  /*
   * new MethodVisitor().visit(cu, null); // System.out.println(cu.); // prints
   * the resulting compilation unit to default system output //
   * System.out.println(cu.toString()); }
   */
  private static class MethodVisitor extends VoidVisitorAdapter {

    @Override
    public void visit(MethodDeclaration n, Object arg) {
      // here you can access the attributes of the method.
      // this method will be called for all methods in this
      // CompilationUnit, including inner class methods
      List<String> tokens = new ArrayList<String>();
      System.out.println(n.getBody());
      BlockStmt code = n.getBody();
      // get rid of all string literals -
      StringTokenizer s =
          new StringTokenizer(code.toString().replaceAll(
              "\"(?:\\\\\"|[^\"])*?\"",
              ""), ",�.,;)(}{][�\"+-/*�|||&&&!=@  ");
      while (s.hasMoreElements()) {
        tokens.add(s.nextToken());
        // System.out.println(s.nextToken());
      }
      arg = tokens;
    }
  }
}
