package com.mwambachildrenschoir.act.scraper;

import com.mwambachildrenschoir.act.dao.ActDao;

public class ExportDonorToCSV {
	private ActDao actDao;
	
	public ExportDonorToCSV() {
		// Name,Company,Email,Phone,Mobile,Fax,Website,Street,City,State ,ZIP,Country
	}
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main() throws Exception {
		// kick it off
		new ExportDonorToCSV();
	}
	
	
}
