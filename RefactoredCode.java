
public class RefactoredCode {

	
	import io.cucumber.java.After;
	import io.cucumber.java.Before;
	import io.cucumber.java.en.Then;
	import org.apache.poi.ss.usermodel.*;
	import org.apache.poi.xssf.usermodel.XSSFWorkbook;
	import org.openqa.selenium.By;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.WebElement;
	import org.openqa.selenium.chrome.ChromeDriver;
	import org.testng.annotations.DataProvider;
	import org.testng.annotations.Test;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.IOException;
	import java.util.*;

	public class ProxyStepDefinitions {
	    private WebDriver driver;
	    private ProxyPage proxyPage;
	    private ExcelUtils excel;
	    private final String filePath = "ProxyDataSheet.xlsx";

	    @Before
	    public void setUp() throws IOException {
	        driver = new ChromeDriver();
	        driver.get("URL_OF_THE_APPLICATION");
	        proxyPage = new ProxyPage(driver);
	        excel = new ExcelUtils(filePath);
	    }

	    @DataProvider(name = "proxyTestData")
	    public Iterator<Object[]> getTestData() throws IOException {
	        excel = new ExcelUtils(filePath);
	        List<Object[]> testData = new ArrayList<>();
	        Map<String, Integer> columnIndexMap = excel.getColumnIndexMap();

	        for (int i = 1; i < excel.getRowCount(); i++) {
	            if ("N".equalsIgnoreCase(excel.getCellData(i, columnIndexMap.get("Skip")))) {
	                testData.add(new Object[]{i, excel.getCellData(i, columnIndexMap.get("Proxy"))});
	            }
	        }
	        return testData.iterator();
	    }

	    @Test(dataProvider = "proxyTestData")
	    public void executeProxyTests(int rowIndex, String proxyUser) throws IOException {
	        Map<String, Integer> columnIndexMap = excel.getColumnIndexMap();
	        Map<String, String> results = new LinkedHashMap<>();

	        for (String columnName : columnIndexMap.keySet()) {
	            if (columnName.startsWith("IN-")) {
	                String outputColumn = "O-" + columnName.substring(3);
	                String inputValue = excel.getCellData(rowIndex, columnIndexMap.get(columnName));
	                boolean result = proxyPage.checkElement(columnName.substring(3), inputValue);
	                results.put(outputColumn, result ? "Pass" : "Fail");
	                results.put("Comments", result ? "Validation successful" : "Validation failed");
	            }
	        }
	        excel.setRowData(rowIndex, results);
	    }

	    @Then("I save the results back to the excel file")
	    public void saveResults() throws IOException {
	        excel.saveAndClose();
	    }

	    @After
	    public void tearDown() {
	        driver.quit();
	    }
	}

	class ProxyPage {
	    private final WebDriver driver;

	    public ProxyPage(WebDriver driver) {
	        this.driver = driver;
	    }

	    public boolean checkElement(String fieldName, String expectedValue) {
	        try {
	            WebElement element = driver.findElement(By.name(fieldName));
	            return element.getText().trim().equalsIgnoreCase(expectedValue.trim());
	        } catch (Exception e) {
	            return false;
	        }
	    }
	}

	class ExcelUtils {
	    private final String filePath;
	    private final Workbook workbook;
	    private final Sheet sheet;

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
	        Map<String, Integer> columnIndexMap = new LinkedHashMap<>();
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

	    public void setRowData(int rowIndex, Map<String, String> data) {
	        Map<String, Integer> columnIndexMap = getColumnIndexMap();
	        Row row = sheet.getRow(rowIndex);
	        if (row == null) row = sheet.createRow(rowIndex);

	        for (Map.Entry<String, String> entry : data.entrySet()) {
	            Integer colIndex = columnIndexMap.get(entry.getKey());
	            if (colIndex != null) {
	                Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
	                cell.setCellValue(entry.getValue());
	            }
	        }
	    }

	    public void saveAndClose() throws IOException {
	        FileOutputStream fos = new FileOutputStream(filePath);
	        workbook.write(fos);
	        fos.close();
	        workbook.close();
	    }
	}
}
