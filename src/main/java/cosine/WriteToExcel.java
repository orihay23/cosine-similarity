package cosine;

import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.ejml.simple.SimpleMatrix;

public class WriteToExcel {

  /**
   * @param args
   * @throws IOException
   * @throws WriteException
   * @throws RowsExceededException
   */
  public static void main(String[] args)
      throws IOException,
      RowsExceededException,
      WriteException {
    WritableWorkbook wb =
        Workbook.createWorkbook(new File(
            "C:\\Users\\John Y\\coding\\cosine-similarity\\target\\out.xls"));
    WritableSheet sheet = wb.createSheet("First Sheet", 0);
    WritableFont boldFont =
        new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD);
    WritableCellFormat boldFormat = new WritableCellFormat(boldFont);
    WritableCellFormat yellow = new WritableCellFormat();
    yellow.setBackground(Colour.YELLOW);
    boldFormat.setBackground(Colour.YELLOW);
    // all methods
    int wbRow = 1;
    // just vulnerable methods
    int wbCol = 1;
    int count = 0;
    List<Integer> vulnCount = new ArrayList<Integer>();
    for (ClassObject co : TermExtractor.sourceObjects) {
      List<TypeDeclaration> types = co.getCu().getTypes();
      if (types == null)
        continue;
      for (TypeDeclaration type : types) {
        List<BodyDeclaration> members = type.getMembers();
        if (members == null)
          continue;
        for (BodyDeclaration member : members) {
          if (member instanceof MethodDeclaration) {
            if (((MethodDeclaration) member).getMethodTerms() != null) {
              if (((MethodDeclaration) member).getMethodTerms().size() != 0) {
                count++;
                String methName = ((MethodDeclaration) member).getName();
                Label label = new Label(0, wbRow++, methName, boldFormat);
                sheet.addCell(label);
                if (((MethodDeclaration) member).isVulnerable()) {
                  Label vuln = new Label(wbCol++, 0, methName, boldFormat);
                  sheet.addCell(vuln);
                  vulnCount.add(count);
                }
              }
            }

            // don't
            // count
            // methods
            // found
            // in
            // ABSTRACT
            // and
            // Interface
            // classes
            // count++;
          }
        }
      }
    }

    SimpleMatrix dummyCosine = new SimpleMatrix(1, 1);

    for (Integer j : vulnCount) {
      for (int i = 0; i < dummyCosine.numRows(); i++) {
        if (vulnCount.contains(i)) {
          Number toAddVuln = new Number(j, i, dummyCosine.get(i, j), yellow);
          sheet.addCell(toAddVuln);
        } else {
          Number toAddVuln = new Number(j, i, dummyCosine.get(i, j));
          sheet.addCell(toAddVuln);
        }
      }
    }

    wb.write();
    wb.close();
  }
}
