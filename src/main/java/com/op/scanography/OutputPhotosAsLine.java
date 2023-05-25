package com.op.scanography;

import com.op.Base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class OutputPhotosAsLine extends Base {

	private static OutputPhotosAsLine scanner;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		scanner = new OutputPhotosAsLine();
		scanner.readFiles();
	}

	private String file = "PHO";
	private String baseDir = "D:/My Documents/My Pictures/Wedding/";
	private String baseDir1 = "Andrew/converted/";
	private String baseDir2 = "ForAlbum/";
	private String baseDir3 = "Renata/";
	private String baseDir4 = "RuthRobin/";
	private String baseDir5 = "StephenCasey/";
	private String baseDir6 = "WedPics/";
	private String baseDirs[] = { baseDir1, baseDir3, baseDir4, baseDir5, baseDir6 };
	private String srcDir = baseDir;
	private String opDir = hostOpDir + file + "/";
	private int w = 0;
	private int h = 200;
	private String opFile = file + "_PHO.png";

	private void readFiles() throws Exception {
		int maxNumFilesUsed = 500;
		ArrayList<File> allFiles = new ArrayList<File>();
		for (String baseDirN : baseDirs) {
			File direct = new File(baseDir + baseDirN);
			System.out.println("Scanning dir..." + direct.getAbsolutePath());
			if (direct.isDirectory()) {
				File[] filesPerDir = direct.listFiles();
				System.out.println("Scanned dir. Num files=" + filesPerDir.length);
				Arrays.sort(filesPerDir, new Comparator<File>() {
					public int compare(File f1, File f2) {
						return Long.compare(getSort(f1), getSort(f2));
					}
				});

				filesPerDir = Arrays.copyOfRange(filesPerDir, 0,
						filesPerDir.length < maxNumFilesUsed ? filesPerDir.length : maxNumFilesUsed);
				for (File file : filesPerDir) {
					if (file.getName().endsWith(".jpg") || file.getName().endsWith(".JPG")) {
						allFiles.add(file);
					}
				}
			}
		}

		System.out.println("all=" + allFiles.size());
		if (true) {
			// return;
		}
		Collections.sort(allFiles, new Comparator<File>() {
			public int compare(File f1, File f2) {
				return Long.compare(getSort(f2), getSort(f2));
			}
		});

		for (File file : allFiles) {
			BufferedImage bi = ImageIO.read(file);
			double ww = bi.getWidth();
			double hh = bi.getHeight();
			double w3 = ww * (h / hh);
			w = w + (int) w3;
		}

		BufferedImage opImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D opG = (Graphics2D) opImage.getGraphics();

		double w2 = 0;
		for (int i = 0; i < allFiles.size(); i++) {
			System.out.println("file mod=" + getSort(allFiles.get(i)));
			BufferedImage bi = ImageIO.read(allFiles.get(i));
			double ww = bi.getWidth();
			double hh = bi.getHeight();
			double w3 = ww * (h / hh);

			opG.drawImage(bi, (int) w2, 0, (int) w3, h, null);
			System.out.println("scanned:" + allFiles.get(i).getPath());
			w2 = w2 + w3;
		}
		String op = opDir;
		File op1 = new File(op + opFile);
		ImageIO.write(opImage, "png", op1);
		System.out.println("Saved: " + op1.getAbsolutePath());

	}

	private long getSort(File f) {
		try {
			BasicFileAttributes attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
			return attr.creationTime().toMillis();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;
		}
	}
}
