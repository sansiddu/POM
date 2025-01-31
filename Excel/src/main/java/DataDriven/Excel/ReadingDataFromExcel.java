package DataDriven.Excel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadingDataFromExcel {

	public static void main(String[] args) throws  IOException{
		// TODO Auto-generated method stub

		FileInputStream file = new FileInputStream(System.getProperty("user.dir")+"\\testdata\\ProxyDataSheet.xlsx");
		
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		workbook.setMissingCellPolicy(MissingCellPolicy.CREATE_NULL_AS_BLANK);
		XSSFSheet sheet = workbook.getSheet("Sheet1");
	
		int totalRows = sheet.getLastRowNum();
		int totalCells = sheet.getRow(0).getLastCellNum();
		
		System.out.println("number of rows: "+ totalRows);
		System.out.println("number of cells: "+ totalCells);
		
		for(int r=0; r<=totalRows; r++) {
			XSSFRow currentRow = sheet.getRow(r);
			
			for(int c=0; c<totalCells; c++) {
				XSSFCell cell = currentRow.getCell(c);
				System.out.print(cell.toString()+ "\t");
			}
			System.out.println();
		}
	
	}

}
