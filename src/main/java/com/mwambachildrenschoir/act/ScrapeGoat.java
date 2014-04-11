package com.mwambachildrenschoir.act;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mwambachildrenschoir.dao.ActDao;
import com.mwambachildrenschoir.dao.DonationEntity;
import com.mwambachildrenschoir.dao.DonorEntity;

public class ScrapeGoat {
	final static Logger logger = LoggerFactory.getLogger(ScrapeGoat.class);
	private final String baseUrl = "http://actinternational.org/CRMACT/";
	private ActDao actDao;
	private WebDriver driver;
	
	/**
	 * 
	 * @param user
	 * @param pass
	 */
	public ScrapeGoat(String beginDate, String endDate, boolean scrapeDonors) throws Exception {
		JavascriptExecutor js;
		actDao = new ActDao();
		driver = new HtmlUnitDriver(true); // true enables javascript

		driver.get(baseUrl);
		
		if (logger.isDebugEnabled()) logger.debug("logging in");
		driver.findElement(By.id("txtUserName")).clear();
		driver.findElement(By.id("txtUserName")).sendKeys(System.getProperty("act.user"));
		driver.findElement(By.id("txtPassword")).sendKeys(System.getProperty("act.pass"));
		driver.findElement(By.id("btnLogin")).click();
		
		if (logger.isDebugEnabled()) logger.debug("redirecting to secondary login");
		doSecondaryLogin();
		
		
		// Mwamba and IAM donation submit forms
		List<WebElement> donationForms = driver.findElements(By.tagName("form"));
		
		// there is one extra form on the page that we don't want to submit
		int donationsFormsCount = donationForms.size() - 1;
		int donatonsFormsIndex = 0;
		String currentDonationsUrl = driver.getCurrentUrl();
		
		// only pull transactions if the begin and end dates are set...otherwise
		// this may just be a deep dive into the donor info
		if (!beginDate.equals("") && !endDate.equals("")) {
		
			if (logger.isDebugEnabled()) logger.debug("beginning to iterate over accounts");
			while (donatonsFormsIndex < donationsFormsCount) {
				
				donationForms.get(donatonsFormsIndex).submit();
	
				// now we're looking at the pages that have the months for each account
				// we only need the first one because we can manipulat the hidden fields 
				// using javascript and select as many months as we want (or any month)
				List<WebElement> detailsForms = driver.findElements(By.tagName("form"));
				WebElement form = detailsForms.get(0);
				// need to manipulate the form so it opens in current window instead of top window				
				js = (JavascriptExecutor)driver;
				js.executeScript("document.forms[0].target='_self'", form);
				
				// now adjust the dates
				Iterator<WebElement> inputs = form.findElements(By.tagName("input")).iterator();
				while (inputs.hasNext()) {
					WebElement input = inputs.next();
					if (input.getAttribute("name").equals("BegDate")) {
						js.executeScript("document.forms[0].elements['BegDate'].value='" + beginDate + "';");
					}
					
					if (input.getAttribute("name").equals("EndDate")) {
						js.executeScript("document.forms[0].elements['EndDate'].value='" + endDate + "';");
					}
				}
				
				form.submit();
				scrapeMonthDonations();
				
				donatonsFormsIndex++;
				driver.navigate().to(currentDonationsUrl);
				donationForms = driver.findElements(By.tagName("form"));
				Thread.sleep(5000);
			}
		}
		
		if (scrapeDonors) updateDonors(driver);
		
		driver.close();
		driver.quit();
		logger.info("finished");
	}
	
	
	/**
	 * 
	 * @param driver
	 * @throws ParseException 
	 * @throws InterruptedException 
	 */
	private void scrapeMonthDonations() throws ParseException, InterruptedException {

		// parse out this page
		if (logger.isDebugEnabled()) logger.debug("scaping donors for current month");
		Iterator<WebElement> rows = driver.findElements(By.xpath("//tbody/tr")).iterator();
		while (rows.hasNext()) {
			WebElement row = rows.next();
			
			if (logger.isDebugEnabled()) logger.debug("scaping donor");
			Iterator<WebElement> fields = row.findElements(By.xpath(".//td")).iterator();
			int i = 0;			
			DonationEntity donation = new DonationEntity();
			String donorTxt = "";
			String dateTxt = "";
			while (fields.hasNext()) {

				WebElement field = fields.next();
				if (field.getText().equals("No records returned.")) {
					if (logger.isDebugEnabled()) logger.debug("no records for this month, must be a future month");
					continue;
				}
				switch(i) {
					case 0: {	
						// dates look like this: 1/30/2014
						dateTxt = field.getText();
						donation.setPaymentDate(new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH).parse(dateTxt));						
						break;
					}
					case 1: {
						donation.setPaymentNo(Integer.parseInt(field.getText().trim()));
						if (logger.isDebugEnabled()) logger.debug("num: " + field.getText()); 
						break;
					}
					
					case 2: {
						donorTxt = field.getText().replace("Donor Information", "").trim();
						DonorEntity donor = actDao.getDonorByName(donorTxt);
						if (donor == null) {
							donor = new DonorEntity();
							donor.setName(donorTxt);
						}
						String href = field.findElement(By.tagName("a")).getAttribute("href");
						donor.setAddress1("");
						donor.setAddress2("");
						donor.setCity("");
						donor.setState("");
						donor.setZip("");
						donor.setEmail("");
						donor.setPhone("");
						donor.setNotes(href);
						// only put a placeholder in for this donor if the entity does not already exist
						donation.setDonor(donor);
						break; 
					}

					case 3: {
						donation.setDescription(field.getText());
						if (logger.isDebugEnabled()) logger.debug("item: " + field.getText()); 
						break;
					}
					case 4: {
						donation.setAmount(Float.parseFloat(field.getText().trim()));
						if (logger.isDebugEnabled()) logger.debug("amount: " + field.getText()); 
						break;
					}
				}
				i++;
			}
			
			if (donation.getDonor().getId() == 0) continue;
			logger.info("found donation, date:" + dateTxt + ", donor:" + donorTxt);
			actDao.persistDonation(donation);
		}
		
	}
	
	/**
	 * 
	 * @param driver
	 * @throws UnsupportedEncodingException
	 */
	private void updateDonors(WebDriver driver) throws UnsupportedEncodingException {
		Iterator<DonorEntity> donors = (Iterator<DonorEntity>)actDao.getAllDonors().iterator();
		logger.info("begining deep dive into donor info");
		while (donors.hasNext()) {
			DonorEntity donor = donors.next();
			if (donor.getNotes() == null || donor.getNotes().trim().equals("")) continue;
			driver.navigate().to(donor.getNotes());
			Iterator<WebElement> iter = driver.findElements(By.xpath("//p")).iterator();
			if (driver.getPageSource().indexOf("No records returned.") > 0) {
				logger.warn("no donor information found for " + donor.getName());
				continue;
			};
			scrapeDonor(iter);
		}
	}
	
	/**
	 * 
	 * @param driver
	 */
	private long scrapeDonor(Iterator<WebElement> iter) {
		DonorEntity donor = new DonorEntity();
		
		int i = 0;
		while (iter.hasNext()) {
			WebElement p = iter.next();
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
				case 0: break;
				case 1: {
							String name = p.getText().trim();
							logger.info("diving into donor information for: " + name);
							donor.setName(name);
							break;
						}
				case 2: donor.setAddress1(p.getText().trim()); break;
				case 3: donor.setAddress2(p.getText().trim()); break;
				case 4: fillInDonorCSZ(donor, p.getText()); break;
				case 5: donor.setPhone(p.getText().substring(6).trim()); break;
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
			logger.error("could not scrape the donor city,state,zip from this: " + txtBak);
		}
	}
	
	/**
	 * this is a hack because of the ACT sessions 
	 */
	private void doSecondaryLogin() {
		driver.navigate().to(baseUrl + "platform/webtools/old_system_login.aspx");
		WebElement btnSubmit = driver.findElement(By.id("btnSubmit"));
		
	    JavascriptExecutor js = (JavascriptExecutor)driver;
	    js.executeScript( "document.forms[0].action='http://www.iaact.org/staffback/Ncheck_user.asp';" +
	    						"document.forms[0].__VIEWSTATE.name='NOVIEWSTATE';", 
	    						btnSubmit
	    					  );
	    btnSubmit.submit(); // secondary login
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// kick it off
		String beginDate = "";
		String endDate = "";
		boolean scrapeDonors = false;
		for (int i=0; i < args.length; i++) {
			if (args[i].equals("--beginDate")) {
				beginDate = args[i+1];
			}
			if (args[i].equals("--endDate")) {
				endDate = args[i+1];
			}
			if (args[i].equals("--scrapeDonors")) {
				scrapeDonors = true;
			}
		}
		
		if ((beginDate.equals("") || endDate.equals("")) && !scrapeDonors) {
			System.out.println("you must provide beginning and end date when --scrapeDonors is not provided: " + 
								"\n java -jar scrapegoat.jar --beginDate 01/01/2014 --endDate 12/31/2014 [--scrapeDonors]" + 
								"\n java -jar scrapegoat.jar --scrapeDonors");
			System.exit(0);
		}
		
		new ScrapeGoat(beginDate, endDate, scrapeDonors);
		System.exit(0);
	}
}
