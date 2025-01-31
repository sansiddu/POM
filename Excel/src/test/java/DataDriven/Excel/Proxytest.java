package DataDriven.Excel;

import java.io.IOException;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Pages.ProxyPage;
import Utils.ExcelUtils;

public class Proxytest {

	 private WebDriver driver;
	    private ProxyPage proxyPage;
	    private ExcelUtils excel;
	    private String filePath = "ProxyDataSheet.xlsx";

//	    @Before
//	    public void setUp() throws IOException {
//	        driver = new ChromeDriver();
//	        driver.get("URL_OF_THE_APPLICATION");
//	        proxyPage = new ProxyPage(driver);
//	        excel = new ExcelUtils(filePath);
//	    }

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
}
