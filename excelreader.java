
public class excelreader {

	
	import org.apache.poi.ss.usermodel.*;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;
	import org.openqa.selenium.*;
	import org.openqa.selenium.chrome.ChromeDriver;
	import java.io.*;
	import java.util.HashMap;
	import java.util.Map;

	// Utility class for handling Excel operations
	class ExcelUtils {
	    private Workbook workbook;
	    private Sheet sheet;
	    private String filePath;

	    public ExcelUtils(String filePath) throws IOException {
	        this.filePath = filePath;
	        FileInputStream fis = new FileInputStream(new File(filePath));
	        this.workbook = new XSSFWorkbook(fis);
	        this.sheet = workbook.getSheetAt(0);
	    }

	    public int getRowCount() {
	        return sheet.getPhysicalNumberOfRows();
	    }

	    public String getCellData(int row, int col) {
	        Cell cell = sheet.getRow(row).getCell(col);
	        return (cell != null) ? cell.getStringCellValue() : "";
	    }

	    public void setCellData(int row, int col, String value) {
	        Row sheetRow = sheet.getRow(row);
	        if (sheetRow == null) sheetRow = sheet.createRow(row);
	        Cell cell = sheetRow.createCell(col);
	        cell.setCellValue(value);
	    }

	    public void saveAndClose() throws IOException {
	        FileOutputStream fos = new FileOutputStream(new File(filePath));
	        workbook.write(fos);
	        fos.close();
	        workbook.close();
	    }
	}

	// Page Object Model for the Proxy Page
	class ProxyPage {
	    private WebDriver driver;
	    
	    public ProxyPage(WebDriver driver) {
	        this.driver = driver;
	    }

	    public boolean checkElement(String elementId, String expectedValue) {
	        try {
	            WebElement element = driver.findElement(By.id(elementId));
	            return element.getText().equalsIgnoreCase(expectedValue);
	        } catch (NoSuchElementException e) {
	            return false;
	        }
	    }
	}

	// Main class to execute Selenium tests based on the Excel data
	public class SeleniumPOMExcel {
	    public static void main(String[] args) {
	        String filePath = "ProxyDataSheet.xlsx";
	        WebDriver driver = new ChromeDriver();
	        driver.get("URL_OF_THE_APPLICATION");
	        ProxyPage proxyPage = new ProxyPage(driver);

	        try {
	            ExcelUtils excel = new ExcelUtils(filePath);
	            int rowCount = excel.getRowCount();
	            Map<String, Integer> columnIndexMap = new HashMap<>();

	            Row headerRow = excel.sheet.getRow(0);
	            for (Cell cell : headerRow) {
	                columnIndexMap.put(cell.getStringCellValue(), cell.getColumnIndex());
	            }

	            for (int i = 1; i < rowCount; i++) {
	                String skipValue = excel.getCellData(i, columnIndexMap.get("Skip"));
	                if ("N".equalsIgnoreCase(skipValue)) {
	                    for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
	                        String columnName = entry.getKey();
	                        if (columnName.startsWith("IN-")) {
	                            String outputColumn = "O-" + columnName.substring(3);
	                            String inputValue = excel.getCellData(i, entry.getValue());
	                            boolean result = proxyPage.checkElement(columnName.substring(3), inputValue);
	                            excel.setCellData(i, columnIndexMap.get(outputColumn), result ? "Pass" : "Fail");
	                        }
	                    }
	                }
	            }
	            
	            excel.saveAndClose();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        driver.quit();
	    }
	}
}
