package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ProxyPage {
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
