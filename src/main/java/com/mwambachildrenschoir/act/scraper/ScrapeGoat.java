package com.mwambachildrenschoir.act.scraper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByTagName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mwambachildrenschoir.act.dao.ActDao;
import com.mwambachildrenschoir.act.dao.DonationEntity;
import com.mwambachildrenschoir.act.dao.DonorEntity;

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
	 * @throws ParseException 
	 * @throws InterruptedException 
	 */
	private void handleMonth(WebDriver driver) throws ParseException, InterruptedException {
		// parse out this page
		logger.info("scaping donors for current month");
		Iterator<WebElement> rows = driver.findElements(By.xpath("//tbody/tr")).iterator();
		while (rows.hasNext()) {
			WebElement row = rows.next();
			
			logger.info("scaping donor");
			Iterator<WebElement> fields = row.findElements(By.xpath(".//td")).iterator();
			int i = 0;			
			DonationEntity at = new DonationEntity(); 
			while (fields.hasNext()) {
				WebElement field = fields.next();
				if (field.getText().equals("No records returned.")) {
					logger.info("no records here, must be a future month");
					break;
				}
				
				switch(i) {
					case 0: {	
						// dates look like this: 1/30/2014
						at.setPaymentDate(new SimpleDateFormat("mm/dd/yyyy", Locale.ENGLISH).parse(field.getText()));
						logger.info("date: " + field.getText()); 
						break;
					}
					case 1: {
						at.setPaymentNo(Integer.parseInt(field.getText().trim()));
						logger.info("num: " + field.getText()); 
						break;
					}
					
					case 2: {
						logger.info("name: " + field.getText());
						

						// need to dig into the donor's personal  info here
						// find the anchor tag
						WebElement donorLink = field.findElement(By.tagName("a"));
						Actions act = new Actions(driver);
						WebElement onElement = donorLink;
						act.contextClick(onElement).perform();
						Thread.sleep(1000);
						act.sendKeys("w").perform();						
						Thread.sleep(1000);
						Set<String> handles = driver.getWindowHandles();
						
						driver.switchTo().window((String)handles.toArray()[handles.size() -1]);
						// now we need to scrape the data on the screen
						at.setDonorId(scrapeDonor(driver));
						driver.close();
						driver.switchTo().window((String)handles.toArray()[1]);
						break; 
					}

					case 3: {
						at.setDescription(field.getText());
						logger.info("item: " + field.getText()); 
						break;
					}
					case 4: {
						at.setAmount(Float.parseFloat(field.getText().trim()));
						logger.info("amount: " + field.getText()); 
						break;
					}
				}
				i++;
			}
			actDao.persistDonation(at);
		}
		
	}
	
	
	/**
	 * 
	 * @param driver
	 */
	private int scrapeDonor(WebDriver driver) {
		if (driver.findElement(By.tagName("body")).getText().trim().equals("No records returned.")) return 0;
		
		Iterator<WebElement> ps = driver.findElements(By.xpath("//p")).iterator();
		
		int i = 0;
		
		if (!ps.hasNext()) return 0;
			
		DonorEntity donor = new DonorEntity();
		while (ps.hasNext()) {
			WebElement p = ps.next();
			/*
			 * 0 = title (Donor Information)
			 * 1 = name (this is our unique key
			 * 2 = address1
			 * 3 = address2
			 * 4 = city\nstate&nbsp;\nzip
			 * 5 = phone:
			 * 6 = email address from a mailto: href
			 */
			switch (i) {
				case 0:
				case 1: donor.setName(p.getText().trim()); break;
				case 2: donor.setAddress1(p.getText().trim()); break;
				case 3: donor.setAddress2(p.getText().trim()); break;
				case 4: fillInDonorCSZ(donor, p.getText()); break;
				case 5: donor.setPhone(p.getText().substring(6)); break;
				case 6: {
							WebElement emailLink = p.findElement(By.tagName("a"));
							donor.setEmail(emailLink.getText().trim());
							break;
						}
			}
			i++;
		} 
		return actDao.persistDonor(donor);
		 
	}
	
	/**
	 * fill in the donor city, state, zip
	 * @param donor
	 */
	private void fillInDonorCSZ(DonorEntity donor, String txt) {
		String txtBak = txt;
		try {
			// Westminister, MD  21157-3476
			donor.setCity(txt.substring(0, txt.indexOf(',')));
			txt = txt.substring(txt.indexOf(',') + 1).trim();
			donor.setState(txt.substring(0, 2));
			txt = txt.substring(2).trim();
			donor.setZip(txt.substring(0).trim());
		} catch (Exception e) {
			logger.warn("could not scrape the donor city,state,zip from this: " + txtBak);
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
