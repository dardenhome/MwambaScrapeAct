package com.mwambachildrenschoir.act.scraper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mwambachildrenschoir.act.dao.ActDao;
import com.mwambachildrenschoir.act.dao.DonorEntity;

public class ExportDonorToCSV {
	final static Logger logger = LoggerFactory.getLogger(ScrapeGoat.class);

	public ExportDonorToCSV() {
		ActDao actDao = new ActDao();
		Iterator<DonorEntity> donors = actDao.getAllDonors().iterator();

		// Name,Company,Email,Phone,Mobile,Fax,Website,Street,City,State
		// ,ZIP,Country
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("donors.csv", true)))) {
			while (donors.hasNext()) {
				DonorEntity donor = donors.next();
				String txt = donor.getName() + ",," + donor.getEmail() + "," + donor.getPhone() + ",,,," + 
						donor.getAddress1() + " " + donor.getAddress2() + "," + donor.getCity() + "," +
						donor.getState() + "," + donor.getZip() + ",US";

				logger.info(txt);
				out.println(txt);
				out.close();
			}
		} catch (IOException e) {
			logger.error("error writing to file", e);
		}
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// kick it off
		new ExportDonorToCSV();
		System.exit(0);
	}

}
