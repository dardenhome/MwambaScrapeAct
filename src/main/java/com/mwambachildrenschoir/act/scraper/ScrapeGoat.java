package com.mwambachildrenschoir.act.scraper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mwambachildrenschoir.act.dao.ActDao;

public class ScrapeGoat {
	final static Logger logger = LoggerFactory.getLogger(ScrapeGoat.class);
	private final String baseUrl = "http://actinternational.org/CRMACT/";
	private ActDao actDao;
	
	/**
	 * 
	 * @param user
	 * @param pass
	 */
	public ScrapeGoat(String user, String pass) throws Exception {
		actDao = new ActDao();
		actDao.getAllTransactions();
		WebDriver driver;
		driver = new FirefoxDriver();
		driver.get(baseUrl);
		
		logger.info("logging in");
		driver.findElement(By.id("txtUserName")).clear();
		driver.findElement(By.id("txtUserName")).sendKeys(user);
		driver.findElement(By.id("txtPassword")).sendKeys(pass);
		driver.findElement(By.id("btnLogin")).click();
		
		logger.info("redirecting to secondary login");
		driver.navigate().to(baseUrl + "platform/webtools/old_system_login.aspx");
		
		logger.info("click secondary login");
		driver.findElement(By.id("btnSubmit")).click();
		
		List<WebElement> elements = driver.findElements(By.tagName("form"));
		
		// there is one extra form on the page that we don't want to submit
		int maxAccounts = elements.size() - 1;
		int accountIndex = 0;
		String currentUrl = driver.getCurrentUrl();
				
		logger.info("beginning to iterate over accounts");
		while (accountIndex < maxAccounts) {
			
			WebElement form = elements.get(accountIndex);
			form.submit();
			// now we need to dive into each account by month
			handleMonths(driver);
			
			accountIndex++;
			driver.navigate().to(currentUrl);
			elements = driver.findElements(By.tagName("form"));
		}
		driver.close();
		driver.quit();
	}
	
	/**
	 * 
	 * @param driver
	 * @throws InterruptedException 
	 */
	private void handleMonths(WebDriver driver) throws Exception {
		Iterator<WebElement> elements = driver.findElements(By.tagName("form")).iterator();
				
		logger.info("beginning to iterate over months");
		while (elements.hasNext()) {
			WebElement form = elements.next();
			logger.info("heading into new month");
			form.findElement(By.xpath(".//input[@type='submit']")).click();
			Set<String> handles = driver.getWindowHandles();
			
			driver.switchTo().window((String)handles.toArray()[handles.size() -1]);
			// now we need to scrape the data on the screen
			handleMonth(driver);
			driver.close();
			driver.switchTo().window((String)handles.toArray()[0]);
		}
	}
	
	/**
	 * 
	 * @param driver
	 */
	private void handleMonth(WebDriver driver) {
		// parse out this page
		logger.info("scaping donors for current month");
		Iterator<WebElement> rows = driver.findElements(By.xpath("//tbody/tr")).iterator();
		while (rows.hasNext()) {
			WebElement row = rows.next();
			
			logger.info("scaping donor");
			Iterator<WebElement> fields = row.findElements(By.xpath(".//td")).iterator();
			int i = 0;			
			while (fields.hasNext()) {
				WebElement field = fields.next();
				if (field.getText().equals("No records returned.")) {
					logger.info("no records here, must be a future month");
					break;
				}
				
				switch(i) {
					case 0: logger.info("date: " + field.getText()); break;
					case 1: logger.info("num: " + field.getText()); break;
					case 2: logger.info("name: " + field.getText()); break; // need to dig into the donor's personal  info here
					case 3: logger.info("item: " + field.getText()); break;
					case 4: logger.info("amount: " + field.getText()); break;
				}
				i++;
			}
		}
		
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// kick it off
		new ScrapeGoat(args[0], args[1]);
	}
}
