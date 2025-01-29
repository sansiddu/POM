
	import org.junit.After;
	import org.junit.Before;
	import org.junit.Test;
	import org.openqa.selenium.WebDriver;
	import org.openqa.selenium.chrome.ChromeDriver;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.Map;
	
	
	public class ProxyTest {
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
	    
	    @Test
	    public void testProxyPage() throws IOException {
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
	    }
	    
	    @After
	    public void tearDown() {
	        driver.quit();
	    }
	}

}
