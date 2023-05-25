package com.op.scanography;

import com.op.Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class CombineReports extends Base {

	public static CombineReports combine = new CombineReports();
	private String dir = hostOpDir + "/Kickstarter/reports/zip/";
	private String all = "ALL.csv";

	public static void main(String[] args) throws Exception {
		combine.make();
	}

	private void make() {
		try {
			PrintWriter writer = new PrintWriter(dir + all, "UTF-8");

			File dirReports = new File(dir);
			if (dirReports.isDirectory()) {
				File files[] = dirReports.listFiles();
				boolean firstFile = true;
				for (File file : files) {
					if (!file.getName().startsWith(all) && !file.getName().startsWith("1.00")) {
						FileReader fr = new FileReader(file.getAbsolutePath());
						BufferedReader br = new BufferedReader(fr);
						String sCurrentLine;
						boolean firstLine = true;
						while ((sCurrentLine = br.readLine()) != null) {
							if (firstFile) {
								write(writer, sCurrentLine);
								firstFile = false;
								firstLine = false;
							} else {
								if (firstLine) {
									firstLine = false;
								} else {
									write(writer, sCurrentLine);
								}
							}
						}
					}
				}
			}
			writer.close();
		} catch (Exception e) {
			System.err.println(e);
		}
		System.out.println("" + dir + all);
	}

	private void write(PrintWriter writer, String sCurrentLine) throws IOException {
		System.out.println(sCurrentLine);
		writer.println(sCurrentLine);
	}

}
