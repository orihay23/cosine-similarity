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

		/*generate all class objects*/
		List<Method> methods =
				TermExtractor.fromFile(
						new File(
                "D:\\Smart_Data\\Behe\\today\\cosine-similarity"));

		/*generate master token list with counts as well as each team appearing in a given method*/
		TreeMap<String, Integer> masterTokenMap =
				ParseUtility.generateMasterTokenMap();


		/*count number of methods in source code*/
		int count = 0;
		for (ClassObject co : TermExtractor.sourceObjects) {
			List<TypeDeclaration> types = co.getCu().getTypes();
			for (TypeDeclaration type : types) {
				List<BodyDeclaration> members = type.getMembers();
				for (BodyDeclaration member : members) {
					if (member instanceof MethodDeclaration) {
						if(((MethodDeclaration) member).getMethodTerms() !=null) //don't count methods found in ABSTRACT and Interface classes
							count++;
					}
				}
			}
		}

		/*set size of function Term Frequency matrix using count from above and size of the masterTokenMap*/
		Matrix sparseMatrix = new Matrix(count, masterTokenMap.size());

		/*generate sparse matrix.  */
		int methodCounter = 0;
		for (ClassObject co : TermExtractor.sourceObjects) {
			List<TypeDeclaration> types = co.getCu().getTypes();
			for (TypeDeclaration type : types) {
				List<BodyDeclaration> members = type.getMembers();
				for (BodyDeclaration member : members) {
					if (member instanceof MethodDeclaration) {
						int keySetCounter = 0;
						TreeMap<String, Integer> methodMap = ((MethodDeclaration) member).getMethodTerms();
						if(methodMap == null) {continue;}
						for(String keys : masterTokenMap.keySet()){

							if(((MethodDeclaration) member).getMethodTerms().containsKey(keys)){
								sparseMatrix.set(methodCounter, keySetCounter, ((MethodDeclaration) member).getMethodTerms().get(keys));
							}
							keySetCounter++;
						}//end of keyset for
						keySetCounter = 0;
						methodCounter++;
					}//end of if
				}//end of most inner for
			}//end of second most inner for
		}//end of outer for

		System.out.println("SPARSE MATRIX: " + sparseMatrix.getRowDimension() + " " + sparseMatrix.getColumnDimension());

		
		/*create IDF matrix*/
		Matrix idf = new Matrix(1, masterTokenMap.size());
		int keySetCounter = 0;
		for(String keys : masterTokenMap.keySet()){
			//	System.out.println(keys);
			int value = masterTokenMap.get(keys);
			double finalValue = Math.log(count/(1+value));
			idf.set(0, keySetCounter, finalValue);
			keySetCounter++;
		}

		System.out.println("IDF MATRIX: " + idf.getRowDimension() + " " + idf.getColumnDimension());

		/*generate mxm diagonal matrix from the above idf matrix*/
		Matrix idfDiagonal = new Matrix(idf.getColumnDimension(), idf.getColumnDimension());
		for(int i = 0; i<idf.getColumnDimension(); i++){
			idfDiagonal.set(i, i, idf.get(0, i));
		}
		System.out.println("DIAG MATRIX: " + idfDiagonal.getRowDimension() + " " + idfDiagonal.getColumnDimension());
		
    // result of doing TF * IDF = finalMatrix
		Matrix finalMatrix = sparseMatrix.times(idfDiagonal);

		//finalMatrix.print(1,1);
		//TODO:  Normalize finalMatrix
    // TODO: Cosine Similarity

		System.out.println("FINAL MATRIX: " + finalMatrix.getRowDimension() +  " " +  finalMatrix.getColumnDimension());

    /* normalize finalMatrix */
    for (int i = 0; i < finalMatrix.getRowDimension(); i++) {
      Matrix summationMatrix =
          finalMatrix.getMatrix(i, i, 0, finalMatrix.getColumnDimension() - 1);
      double summationValue = 0;
      for (int x = 0; x < summationMatrix.getColumnDimension(); x++) {
        summationValue += Math.pow(summationMatrix.get(0, x), 2);
      }
      double finalSummationValue = Math.sqrt(summationValue);
      System.out.println("summation Value: " + summationValue);
      System.out.println("finalSummation Value: " + finalSummationValue);

      for (int j = 0; j < finalMatrix.getColumnDimension(); j++) {

        double value = finalMatrix.get(i, j);

        double normalizedValue = value / finalSummationValue;
        finalMatrix.set(i, j, normalizedValue);

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
}
