package com.op.scanography;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class LargePoster extends Base {

	public static LargePoster large = new LargePoster();
	private String file = "GBPP";
	private String weight = "GBPP";
	private String ipF = hostDir + "op/" + file + "/FIN/" + file + "_" + weight + "_OUT.png";
	private String opF = hostDir + "op/" + file + "/FIN/" + file + "_" + weight + "_OUT_L.png";

	public static void main(String[] args) throws Exception {
		large.make();
	}

	private void make() throws IOException {
		File f1 = new File(ipF);
		BufferedImage bi1 = ImageIO.read(f1);

		double win = 40;
		double hin = 30;
		double dpi = 200;
		double mm2in = 25.4;
		int w = (int) (win * dpi);
		int h = (int) (hin * dpi);

		double wpmm = 600;
		double hpmm = 600;
		int wp = (int) (dpi * wpmm / mm2in);
		int hp = (int) (dpi * hpmm / mm2in);

		int wb = (w - wp) / 2;
		int hb = (h - hp) / 2;
		BufferedImage opImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D opG = (Graphics2D) opImage.getGraphics();
		opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		opG.setColor(Color.WHITE);
		opG.fillRect(0, 0, w, h);

		opG.setStroke(new BasicStroke(10f));
		opG.setColor(Color.LIGHT_GRAY);
		opG.drawRect(0, 0, w, h);

		double markmm = 10;
		int mark = (int) (dpi * markmm / mm2in);
		opG.drawLine(wb, hb, wb + mark, hb);
		opG.drawLine(wb, hb, wb, hb + mark);
		opG.drawLine(wb + wp, hb, wb + wp - mark, hb);
		opG.drawLine(wb + wp, hb, wb + wp, hb + mark);

		opG.drawLine(wb + wp, hb + hp, wb + wp - mark, hb + hp);
		opG.drawLine(wb + wp, hb + hp, wb + wp, hb + hp - mark);
		opG.drawLine(wb, hb + hp, wb + mark, hb + hp);
		opG.drawLine(wb, hb + hp, wb, hb + hp - mark);

		opG.drawImage(bi1, wb, hb, wp, hp, null);

		// File fo = new File(opF);
		// ImageIO.write(opImage, "png", fo);

		savePNGFile(opImage, opF, dpi);
	}

}
