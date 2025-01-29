
public class SeleniumPOM {

	import io.cucumber.java.After;
	import io.cucumber.java.Before;
	import io.cucumber.java.en.Given;
	import io.cucumber.java.en.Then;
	import io.cucumber.java.en.When;
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
	import java.util.HashMap;
	import java.util.Map;

	public class ProxyStepDefinitions {
	    private WebDriver driver;
	    private ProxyPage proxyPage;
	    private ExcelUtils excel;
	    private String filePath = "ProxyDataSheet.xlsx";

	    @Before
	    public void setUp() throws IOException {
	        driver = new ChromeDriver();
	        driver.get("URL_OF_THE_APPLICATION");
	        proxyPage = new ProxyPage(driver);
	        excel = new ExcelUtils(filePath);
	    }

	    @DataProvider(name = "proxyTestData")
	    public Object[][] getTestData() throws IOException {
	        excel = new ExcelUtils(filePath);
	        int rowCount = excel.getRowCount();
	        Map<String, Integer> columnIndexMap = excel.getColumnIndexMap();

	        Object[][] testData = new Object[rowCount - 1][2];
	        int dataIndex = 0;

	        for (int i = 1; i < rowCount; i++) {
	            String skipValue = excel.getCellData(i, columnIndexMap.get("Skip"));
	            String proxyUser = excel.getCellData(i, columnIndexMap.get("Proxy"));
	            if ("N".equalsIgnoreCase(skipValue)) {
	                testData[dataIndex][0] = i;
	                testData[dataIndex][1] = proxyUser;
	                dataIndex++;
	            }
	        }
	        return testData;
	    }

	    @Test(dataProvider = "proxyTestData")
	    public void executeProxyTests(int rowIndex, String proxyUser) throws IOException {
	        Map<String, Integer> columnIndexMap = excel.getColumnIndexMap();

	        for (Map.Entry<String, Integer> entry : columnIndexMap.entrySet()) {
	            String columnName = entry.getKey();
	            if (columnName.startsWith("IN-")) {
	                String outputColumn = "O-" + columnName.substring(3);
	                String inputValue = excel.getCellData(rowIndex, entry.getValue());
	                boolean result = proxyPage.checkElement(columnName.substring(3), inputValue);
	                String validationComment = result ? "Validation successful" : "Validation failed";
	                
	                excel.setCellData(rowIndex, columnIndexMap.get(outputColumn), result ? "Pass" : "Fail");
	                excel.setCellData(rowIndex, columnIndexMap.get("Comments"), validationComment);
	            }
	        }
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
	    private WebDriver driver;

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
}
