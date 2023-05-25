package com.op.scanography;

import com.op.Base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JoinLines extends Base {

	public static JoinLines joinLines = new JoinLines();
	private String file1 = "FOTR1";
	private String file2 = "FOTR";
	private String ipF1 = hostOpDir + file1 + "/" + file1 + "_Line.png";
	private String ipF2 = hostOpDir +  file2 + "/" + file2 + "_Line.png";
	private String opFile = file2;
	private String opF = hostOpDir + file1 + "/" + opFile + "_Line_ALL.png";

	public static void main(String[] args) throws Exception {
		joinLines.mergeFiles();
	}

	private void mergeFiles() throws IOException {
		File f1 = new File(ipF1);
		BufferedImage bi1 = ImageIO.read(f1);

		File f2 = new File(ipF2);
		BufferedImage bi2 = ImageIO.read(f2);

		int h = 200;
		int b = 26;
		int top = b;
		int bot = bi1.getHeight() - b;
		int hh = h - (2 * b);

		BufferedImage opImage = new BufferedImage(bi1.getWidth() + bi2.getWidth(), hh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D opG = (Graphics2D) opImage.getGraphics();
		opG.drawImage(bi1.getSubimage(0, top, bi1.getWidth(), bot), 0, 0, null);
		opG.drawImage(bi2.getSubimage(0, top, bi2.getWidth(), bot), bi1.getWidth(), 0, null);

		File fo = new File(opF);
		ImageIO.write(opImage, "png", fo);
		System.out.println("Saved: " + fo.getAbsolutePath());
	}

}
