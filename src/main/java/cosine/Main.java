package cosine;

import japa.parser.ParseException;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

import jxl.read.biff.BiffException;
import Jama.Matrix;
import Jama.SingularValueDecomposition;

public class Main {

  /**
   * @param args
   * @throws IOException
   * @throws ParseException
   * @throws BiffException
   */

  /*
   * ArrayList<String> dupList = ParseUtility.findDuplicateFileNames(new
   * ArrayList<String>(), new HashMap<String, String>(), new
   * File("F:\\SRWNM 1.0.2")); if(obj.getVulnerabilities().isEmpty()){ } else
   * if(obj.getVulnerabilities().size() > 1) { for(String temp :
   * obj.getVulnerabilities().keySet()){ System.out.println(temp);
   * if(dupList.contains(temp)){ System.out.println("FOUND"); } } } else {}
   * System.out.println("this is the number of CU objects created: " +
   * sourceObjects.size());
   */

  public static void main(String[] args)
      throws ParseException,
      IOException,
      BiffException {

    /* generate all class objects */
    List<Method> methods =
        TermExtractor.fromFile(
            new File(
                "C:\\Users\\John Y\\cw\\cosine-similarity"));

    /*
     * generate master token list with counts as well as each team appearing in
     * a given method
     */
    TreeMap<String, Integer> masterTokenMap =
        ParseUtility.generateMasterTokenMap();

    /* count number of methods in source code */
    int count = 0;
    for (ClassObject co : TermExtractor.sourceObjects) {
      List<TypeDeclaration> types = co.getCu().getTypes();
      for (TypeDeclaration type : types) {
        List<BodyDeclaration> members = type.getMembers();
        for (BodyDeclaration member : members) {
          if (member instanceof MethodDeclaration) {
            if (((MethodDeclaration) member).getMethodTerms() != null) // don't
                                                                       // count
                                                                       // methods
                                                                       // found
                                                                       // in
                                                                       // ABSTRACT
                                                                       // and
                                                                       // Interface
                                                                       // classes
              count++;
          }
        }
      }
    }

    /*
     * set size of function Term Frequency matrix using count from above and
     * size of the masterTokenMap
     */
    Matrix sparseMatrix = new Matrix(count - 21, masterTokenMap.size());

    /* generate sparse matrix. */
    int methodCounter = 0;
    for (ClassObject co : TermExtractor.sourceObjects) {
      List<TypeDeclaration> types = co.getCu().getTypes();
      for (TypeDeclaration type : types) {
        List<BodyDeclaration> members = type.getMembers();
        for (BodyDeclaration member : members) {
          if (member instanceof MethodDeclaration) {
            int keySetCounter = 0;
            TreeMap<String, Integer> methodMap =
                ((MethodDeclaration) member).getMethodTerms();
            if (methodMap == null || methodMap.isEmpty()) {
              continue;
            }
            boolean test = false;
            for (String keys : masterTokenMap.keySet()) {

              if (((MethodDeclaration) member).getMethodTerms().containsKey(
                  keys)) {
                sparseMatrix.set(
                    methodCounter,
                    keySetCounter,
                    ((MethodDeclaration) member).getMethodTerms().get(keys));
                if (((MethodDeclaration) member).getMethodTerms().get(keys) != 0) {
                  test = true;
                }

              }

              keySetCounter++;
            }// end of keyset for master token list
            if (!test) {
              System.out.println("empty ro in SPARSE matrix created");
              System.out.println(((MethodDeclaration) member).getMethodTerms());
              System.out.println(((MethodDeclaration) member).getName());
            }
            test = false;
            keySetCounter = 0;
            methodCounter++;
          }// end of if
        }// end of most inner for
      }// end of second most inner for
    }// end of outer for

    System.out.println("SPARSE MATRIX: "
        + sparseMatrix.getRowDimension()
        + " "
        + sparseMatrix.getColumnDimension());

    boolean t = false;
    for (int a = 0; a < sparseMatrix.getRowDimension(); a++) {
      for (int b = 0; b < sparseMatrix.getColumnDimension(); b++) {
        if (sparseMatrix.get(a, b) != 0) {
          t = true;
        }

      }
      if (!t) {
        System.out.println("Found empty row SPARSE MATRIX");
      }
      t = false;
    }

    /* create IDF matrix */
    Matrix idf = new Matrix(1, masterTokenMap.size());
    int keySetCounter = 0;
    for (String keys : masterTokenMap.keySet()) {
      // System.out.println(keys);
      int value = masterTokenMap.get(keys);
      double test = count / (1 + value);

      double finalValue = Math.log((double) count / (1.0 + (double) value));
      if (finalValue == 0.0) {
        System.out.println("this is final Value: " + finalValue);
        System.out.println("this is count of tokens found: " + value);
        System.out.println("this is total number of methods: " + count);
        System.out.println(masterTokenMap.get(keys));
        System.out.println(keys);
        System.out.println("this is test: " + test);
      }
      idf.set(0, keySetCounter, finalValue);
      keySetCounter++;
    }

    System.out.println("IDF MATRIX: "
        + idf.getRowDimension()
        + " "
        + idf.getColumnDimension());

    /* generate mxm diagonal matrix from the above idf matrix */
    Matrix idfDiagonal =
        new Matrix(idf.getColumnDimension(), idf.getColumnDimension());
    for (int i = 0; i < idf.getColumnDimension(); i++) {
      idfDiagonal.set(i, i, idf.get(0, i));
    }
    System.out.println("DIAG MATRIX: "
        + idfDiagonal.getRowDimension()
        + " "
        + idfDiagonal.getColumnDimension());

    boolean temp3 = false;
    for (int a = 0; a < idfDiagonal.getRowDimension(); a++) {
      for (int b = 0; b < idfDiagonal.getColumnDimension(); b++) {
        if (idfDiagonal.get(a, b) != 0) {
          temp3 = true;
        }

      }
      if (!temp3) {
        System.out.println("Found empty row in DIAGONAL MATRIX");

      }
      temp3 = false;
    }

    // result of doing TF * IDF = finalMatrix
    Matrix finalMatrix = sparseMatrix.times(idfDiagonal);

    // finalMatrix.print(1,1);
    // TODO: Normalize finalMatrix
    // TODO: Cosine Similarity

    System.out.println("FINAL MATRIX: "
        + finalMatrix.getRowDimension()
        + " "
        + finalMatrix.getColumnDimension());

    boolean temp1 = false;
    for (int a = 0; a < finalMatrix.getRowDimension(); a++) {
      for (int b = 0; b < finalMatrix.getColumnDimension(); b++) {
        if (finalMatrix.get(a, b) != 0) {
          temp1 = true;
        }

      }
      if (!temp1) {
        System.out.println("Found empty row in Final Matrix");

      }
      temp1 = false;
    }

    int howManyNulls = 0;

    /* normalize finalMatrix */
    for (int i = 0; i < finalMatrix.getRowDimension(); i++) {
      Matrix summationMatrix =
          finalMatrix.getMatrix(i, i, 0, finalMatrix.getColumnDimension() - 1);

      boolean temp2 = false;
      for (int a = 0; a < summationMatrix.getRowDimension(); a++) {
        for (int b = 0; b < summationMatrix.getColumnDimension(); b++) {
          if (summationMatrix.get(a, b) != 0) {
            temp2 = true;
          }

        }
        if (!temp2) {
          System.out.println("Found empty row in SUMMATION MATRIX");

        }
        temp2 = false;
      }

      double summationValue = 0;
      for (int x = 0; x < summationMatrix.getColumnDimension(); x++) {

        summationValue += Math.pow(summationMatrix.get(0, x), 2);
      }
      double finalSummationValue = Math.sqrt(summationValue);
      // System.out.println("summation Value: " + summationValue);
      // System.out.println("finalSummation Value: " + finalSummationValue);

      for (int j = 0; j < finalMatrix.getColumnDimension(); j++) {

        double value = finalMatrix.get(i, j);

        double normalizedValue = value / finalSummationValue;
        if (finalSummationValue == 0) {
          // System.out.println("i: " + i + " j: " + j);
          howManyNulls++;
        }
        finalMatrix.set(i, j, normalizedValue);

      }
    }

    /* SVD */
    // Refer to http://introcs.cs.princeton.edu/java/95linear/ "Lena"
    Matrix transposed = finalMatrix.transpose();
    int rank = 20;
    int M = transposed.getRowDimension();
    int N = transposed.getColumnDimension();
    SingularValueDecomposition svd = transposed.svd();
    Matrix Ur = svd.getU().getMatrix(0, M - 1, 0, rank - 1);
    Matrix Vr = svd.getV().getMatrix(0, N - 1, 0, rank - 1);
    Matrix Sr = svd.getS().getMatrix(0, rank - 1, 0, rank - 1);
    Matrix svdOutput = Ur.times(Sr).times(Vr.transpose());

    System.out.println("how many nulls? " + howManyNulls);
    /* Cosine Similarity: dot product between each method in training set */
    Matrix cosineSimilarity =
        new Matrix(finalMatrix.getRowDimension(), finalMatrix.getRowDimension());
    for (int i = 0; i < finalMatrix.getRowDimension(); i++) {
      Matrix firstDot =
          finalMatrix.getMatrix(i, i, 0, finalMatrix.getRowDimension());
      double[] firstDotArray = firstDot.getRowPackedCopy();
      for (int j = 0; j < finalMatrix.getRowDimension(); j++) {
        Matrix secondDot =
            finalMatrix.getMatrix(j, j, 0, finalMatrix.getRowDimension());
        double[] secondDotArray = secondDot.getRowPackedCopy();
        double dotProduct = dotProduct(firstDotArray, secondDotArray);
        double acos = Math.acos(dotProduct);
        double degrees = Math.toDegrees(acos);
        double percentage = ((180 - degrees) / degrees);
        cosineSimilarity.set(i, j, percentage);
        // if (percentage > 50) {
        // System.out.println(finalMatrix.get(i, j));
        // System.out.println("i " + i + ", j " + j + " percent: " +
        // percentage);
        // }
      }
    }

    //
    // /* 1. Build Class objects from source */
    // ArrayList<ClassObject> sourceObjects =
    // ParseUtility.generateClassObjects(new File("E:\\SRWNM 1.0.2"));
    // /*
    // * Map vulnerabilites to classes to include line numbers of
    // vulnerabillites
    // * and set to the instance of a ClassObject
    // */
    // ParseUtility.generateListOfVulnerabilitiesByClass(
    // sourceObjects,
    // new File(
    // "C:\\Users\\John Y\\workspace\\cosine-similarity\\src\\main\\resources\\NED_20110420_IA_Assessment_SRWNM_V1_0_2.xls"));
    //
    // for (ClassObject obj : sourceObjects) {
    // obj.getCu();
    // }
  }

  private static double dotProduct(
      double[] firstDotArray,
      double[] secondDotArray) {
    double sum = 0;
    for (int j = 0; j < firstDotArray.length; j++) {
      sum += firstDotArray[j] * secondDotArray[j];
      // System.out.println("first " + firstDotArray[j]);
      // System.out.println("2nd " + secondDotArray[j]);
      // System.out.println("sum is: " + sum);
    }
    return sum;
  }
}
