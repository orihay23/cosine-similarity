package cosine;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.StringTokenizer;

public class MethodVisitor extends VoidVisitorAdapter<List<String>> {

  @Override
  public void visit(MethodDeclaration n, List<String> tokens) {
    // here you can access the attributes of the method.
    // this method will be called for all methods in this
    // CompilationUnit, including inner class methods
    System.out.println(n.getBody());
    BlockStmt code = n.getBody();
    // get rid of all string literals -
    StringTokenizer s =
        new StringTokenizer(code.toString().replaceAll(
            "\"(?:\\\\\"|[^\"])*?\"",
            ""), ",�.,;)(}{][�\"+-/*�|||&&&!=@  ");
    while (s.hasMoreElements()) {
      tokens.add(s.nextToken());
    }
  }
}
