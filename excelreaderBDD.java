
public class excelreaderBDD {

	import io.cucumber.java.After;
	import io.cucumber.java.Before;
	import io.cucumber.java.en.Given;
	import io.cucumber.java.en.Then;
	import io.cucumber.java.en.When;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.chrome.ChromeDriver;
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

	    @Given("I have test data from the excel file")
	    public void loadTestData() throws IOException {
	        excel = new ExcelUtils(filePath);
	    }

	    @When("I execute the proxy tests")
	    public void executeProxyTests() throws IOException {
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
}
