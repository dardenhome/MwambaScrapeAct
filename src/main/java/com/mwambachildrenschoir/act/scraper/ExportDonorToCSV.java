package com.mwambachildrenschoir.act.scraper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mwambachildrenschoir.act.dao.ActDao;
import com.mwambachildrenschoir.act.dao.DonorEntity;

public class ExportDonorToCSV {
	final static Logger logger = LoggerFactory.getLogger(ExportDonorToCSV.class);

	public ExportDonorToCSV() {
		ActDao actDao = new ActDao();
		Iterator<DonorEntity> donors = actDao.getAllDonors().iterator();

		// Name,Company,Email,Phone,Mobile,Fax,Website,Street,City,State,ZIP,Country
		File f = new File("donors.csv");
		if (f.exists()) f.delete();
		DonorEntity donor = null;
		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("donors.csv", true)))) {
			while (donors.hasNext()) {
				donor = donors.next();
				String dn = donor.getName();
				String address = donor.getAddress1().trim();
				if (donor.getAddress2().trim() != "") address += donor.getAddress2();
				
				try {
					dn = dn.substring(dn.indexOf(',') + 1).trim() + " " + dn.substring(0, dn.indexOf(',')).trim();
				} catch (StringIndexOutOfBoundsException e) {
					logger.error(dn);
					// obviously no comma in the name
					dn = donor.getName(); 
				}
				
				String txt = dn + ",," + donor.getEmail().trim() + "," + donor.getPhone().trim() + ",,,," + 
						address + "," + donor.getCity().trim() + "," +
						donor.getState().trim() + "," + donor.getZip().trim() + ",US";

				logger.info(txt);
				out.println(txt);
			}
			out.close();
		} catch (IOException e) {		
			logger.error("error writing donor", e);
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
