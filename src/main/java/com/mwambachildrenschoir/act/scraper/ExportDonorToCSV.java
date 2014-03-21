package com.mwambachildrenschoir.act.scraper;

import com.mwambachildrenschoir.act.dao.ActDao;

public class ExportDonorToCSV {
  final static Logger logger = LoggerFactory.getLogger(ScrapeGoat.class);
	public ExportDonorToCSV() {
    ActDao actDao = new ActDao();
    Iterator<DonorEntity> donors = actDao.getAllDonors().iterator();
    
		// Name,Company,Email,Phone,Mobile,Fax,Website,Street,City,State ,ZIP,Country
    try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("donors.csv", true)))) {
        while(donors.hasNext()) {
            String txt = donor.get
            out.println("the text");
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
	public static void main() throws Exception {
		// kick it off
		new ExportDonorToCSV();
	}
	
	
}
