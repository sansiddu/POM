package Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	private String filePath;
	private Workbook workbook;
	private Sheet sheet;

	public ExcelUtils(String filePath) throws IOException {
		this.filePath = filePath;
		FileInputStream fis = new FileInputStream(filePath);
		workbook = new XSSFWorkbook(fis);
		sheet = workbook.getSheetAt(0);
	}

	public int getRowCount() {
		return sheet.getPhysicalNumberOfRows();
	}

	public Map<String, Integer> getColumnIndexMap() {
		Map<String, Integer> columnIndexMap = new HashMap<>();
		Row headerRow = sheet.getRow(0);
		for (Cell cell : headerRow) {
			columnIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
		}
		return columnIndexMap;
	}

	public String getCellData(int rowIndex, int colIndex) {
		Row row = sheet.getRow(rowIndex);
		Cell cell = row.getCell(colIndex);
		return (cell != null) ? cell.toString() : "";
	}

	public void setCellData(int rowIndex, int colIndex, String value) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) row = sheet.createRow(rowIndex);
		Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
		cell.setCellValue(value);
	}

	public void saveAndClose() throws IOException {
		FileOutputStream fos = new FileOutputStream(filePath);
		workbook.write(fos);
		fos.close();
		workbook.close();
	}
}
