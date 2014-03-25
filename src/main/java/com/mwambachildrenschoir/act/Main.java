package com.mwambachildrenschoir.act;

public class Main {

	public static void main(String[] args) throws Exception {
		// kick it off
		if (args.length == 0) {
			System.out.print("Usage: java --jar act.jar [--scrape-donations &| --export-donors]");
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("--scrape-donations")) {
				new ScrapeGoat();
			}

			if (args[i].equals("--export-donors")) {
				new ExportDonorToCSV();
			}
		}
		
		System.exit(0);

	}
}
