package com.op.scanography;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import javax.imageio.ImageIO;

public class OutputMovieAsLine extends Base {

	private static OutputMovieAsLine scanner;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		scanner = new OutputMovieAsLine();
		scanner.readFiles();
	}

	private String file = "GBPP";
	private String srcDir = hostDir + "src/" + file + "/";
	private String opDir = hostDir + "op/" + file + "/";
	private int w = 0;
	private int h = 0;
	private String opFile = file + "_LINE.png";
	private int frameStripBorder = 0;
	// private int frameStripBorder = 24;
	// private int frameStripBorder = 13; // for 1024 x 576 1.85:1
	// private int frameStripBorder = 72; // for 1024 x 576 2.35:1

	private void readFiles() throws Exception {
		String src = srcDir;
		File direct = new File(src);
		System.out.println("Scanning dir...");
		if (direct.isDirectory()) {
			File[] files = direct.listFiles();
			// int numFilesUsed = 1000;
			int numFilesUsed = files.length;
			System.out.println("Scanned dir. Num files=" + numFilesUsed);
			files = Arrays.copyOfRange(files, 0, numFilesUsed);
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File f1, File f2) {
					String n1 = f1.getName();
					String n2 = f2.getName();
					int n1a = Integer.parseInt(n1.substring(5, n1.indexOf(".")));
					int n2a = Integer.parseInt(n2.substring(5, n2.indexOf(".")));
					return -Integer.compare(n1a, n2a);
					// return Long.compare(f2.lastModified(),
					// f1.lastModified());
				}
			});

			BufferedImage bi = ImageIO.read(files[0]);
			int ww = bi.getWidth();
			int hh = bi.getHeight();
			w = numFilesUsed;
			h = bi.getHeight();
			BufferedImage opImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
			Graphics2D opG = (Graphics2D) opImage.getGraphics();

			for (int i = 0; i < numFilesUsed; i++) {
				int ind = numFilesUsed - i - 1;
				BufferedImage bi1 = ImageIO.read(files[ind]);
				BufferedImage biSub = bi1.getSubimage(ww / 2, frameStripBorder, 1, hh - (2 * frameStripBorder));
				opG.drawImage(biSub, i, 0, 1, h, null);
				System.out.println("scanned:" + files[ind].getPath() + " ***    " + ind + "/" + numFilesUsed);
			}
			String op = opDir;
			File op1 = new File(op + opFile);
			ImageIO.write(opImage, "png", op1);
			System.out.println("Saved: " + op1.getAbsolutePath());
		}

	}
}
