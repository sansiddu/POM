package DataDriven.Excel;

import org.apache.poi.ss.usermodel.Cell;

public class CellValue {

	private static final String STRING = null;
	private static final String NUMERIC = null;
	private static final String BOOLEAN = null;
	private static final String FORMULA = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}
	 static String getCellValueAsString(Cell cell) {
	    if (cell == null) {
	        return "NULL"; // or return an empty string ""
	    }
	    
	    switch (cell.getCellType()) {
	        case STRING:
	            return cell.getStringCellValue();
	        case NUMERIC:
	            return String.valueOf(cell.getNumericCellValue());
	        case BOOLEAN:
	            return String.valueOf(cell.getBooleanCellValue());
	        case FORMULA:
	            return cell.getCellFormula(); // Handle formula if necessary
	        case BLANK:
	            return ""; // Return empty string for blank cells
	        default:
	            return "UNKNOWN TYPE"; // Handle other potential types
	    }
	}

}
