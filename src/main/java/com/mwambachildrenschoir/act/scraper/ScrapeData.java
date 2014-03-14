package com.mwambachildrenschoir.act.scraper;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ScrapeData {
	private final String baseUrl = "http://actinternational.org/crmact/";
	private WebDriver driver;
	
	public ScrapeData() {
		driver = new FirefoxDriver();
		driver.get(baseUrl);
	}

	public static void main(String[] args) {
		// kick it off
		new ScrapeData();
	}
}
