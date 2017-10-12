package com.op.scanography;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScanFramer extends Base {

	private static ScanFramer splitter = new ScanFramer();

	private boolean small = true;
	private String film = "TFA";
	private String weightFile = "REY";
	private String angleFile = film + "_" + weightFile + "_OUT.png";
	private String srcDir = hostDir + "op/" + film + "/";
	private String opDirWeb = "D:/My Documents/My Eclipse/MovieMapperEclipse/git/movies/" + film + "/";
	private int mockType = 0;
	private String mockFile = "MOCK" + mockType;
	private String opFile = film + "_" + weightFile + "_" + mockFile + ".jpg";
	private int xMock = -1;
	private int yMock = -1;
	private int dMock = -1;

	private String frFile = hostDir + "op/kickstarter/ProjectPages/" + mockFile + ".jpg";

	private BufferedImage frbi;
	private BufferedImage ibi;
	private BufferedImage obi;
	private Graphics2D opG;
	private int w = -1;
	private int h = -1;
	private double dpi = 254;

	public static void main(String[] args) {
		try {
			splitter.doFrame();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void doFrame() throws IOException {
		System.out.println("Starting Framer...");
		initFiles();
		draw();
		if (mockType == 0) {
			saveWeb();
		} else {
			save();
		}

		if (small && mockType == 0) {
			opFile = film + "_" + weightFile + "_" + mockFile + "_SMALL.jpg";
			BufferedImage orig = obi;
			obi = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
			opG = (Graphics2D) obi.getGraphics();
			opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			opG.setColor(Color.WHITE);
			opG.fillRect(0, 0, 500, 500);

			opG.drawImage(orig, 0, 0, 500, 500, null);
			saveWeb();
		}
	}

	private void draw() throws IOException {

		if (mockType == 0) {
			xMock = 344;
			yMock = 361;
			dMock = 797;
			double fr = (double) dMock / (double) w;
			AffineTransform at = new AffineTransform();
			at.translate(xMock, yMock);
			at.scale(fr, fr);
			opG.drawImage(blur(1), at, null);
			// opG.drawImage(ibi, at, null);
		} else if (mockType == 1) {
			xMock = 34;
			yMock = 34;
			dMock = 235;
			double fr = (double) dMock / (double) w;
			AffineTransform at = new AffineTransform();
			at.translate(xMock, yMock);
			at.scale(fr, fr);
			opG.drawImage(blur(10), at, null);
			// opG.drawImage(ibi, at, null);
		} else if (mockType == 2) {
			xMock = 50;
			yMock = 50;
			dMock = 200;
			double fr = (double) dMock / (double) w;
			AffineTransform at = new AffineTransform();
			at.translate(xMock, yMock);
			at.scale(fr, fr);

			opG.drawImage(blur(10), at, null);

			float a = 0.1f;
			float f1 = 1f;
			float f2 = 0.5f;
			Color c1 = new Color(f1, f1, f1, a);
			Color c2 = new Color(f2, f2, f2, a);

			GradientPaint grad = new GradientPaint(xMock + dMock / 2, yMock + dMock / 2, c1, dMock, dMock, c2);
			opG.setPaint(grad);
			opG.fill(new Rectangle2D.Double(xMock, yMock, dMock, dMock));
		} else if (mockType == 3) {
			xMock = 34;
			yMock = 23;
			dMock = 227;
			double fr = (double) dMock / (double) w;
			AffineTransform at = new AffineTransform();
			at.translate(xMock, yMock);
			at.scale(fr, fr);
			opG.drawImage(blur(10), at, null);
		} else if (mockType == 4) {
			xMock = 53;
			yMock = 53;
			dMock = 206;
			double fr = (double) dMock / (double) w;
			AffineTransform at = new AffineTransform();
			at.translate(xMock, yMock);
			at.scale(fr, fr);
			opG.drawImage(blur(10), at, null);
		}
	}

	private void save() throws IOException {
		// savePNGFile(obi, srcDir + opFile, dpi);
		saveJPGFile(obi, srcDir + opFile, dpi, 0.95f);
	}

	private void saveWeb() throws IOException {
		// savePNGFile(obi, srcDir + opFile, dpi);
		saveJPGFile(obi, opDirWeb + opFile, dpi, 0.75f);
	}

	private void initFiles() throws IOException {
		frbi = ImageIO.read(new File(frFile));
		int frw = frbi.getWidth();
		int frh = frbi.getHeight();

		ibi = ImageIO.read(new File(srcDir + angleFile));
		w = ibi.getWidth();
		h = ibi.getHeight();

		obi = new BufferedImage(frw, frh, BufferedImage.TYPE_INT_RGB);
		opG = (Graphics2D) obi.getGraphics();
		opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		opG.setColor(Color.WHITE);
		opG.fillRect(0, 0, frw, frh);

		opG.drawImage(frbi, 0, 0, null);
	}

	private BufferedImage blur(int radius) {
		int size = radius * 2 + 1;
		float weight = 1.0f / (size * size);
		float[] data = new float[size * size];

		for (int i = 0; i < data.length; i++) {
			data[i] = weight;
		}

		Kernel kernel = new Kernel(size, size, data);
		ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		return op.filter(ibi, null);
	}
}
