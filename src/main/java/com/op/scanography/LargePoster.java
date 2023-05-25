package com.op.scanography;

import com.op.Base;

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
	private String file = "ESB";
	private String weight = "STR";
	private String ipF = hostOpDir + file + "/FIN/" + file + "_" + weight + "_OUT.png";
	private String opF = hostOpDir + file + "/FIN/" + file + "_" + weight + "_OUT_L.png";

	public static void main(String[] args) throws Exception {
		large.make();
	}

	private void make() throws IOException {
		File f1 = new File(ipF);
		BufferedImage bi1 = ImageIO.read(f1);

		double wPostermm = 500;
		double hPostermm = 400;
		double dpi = 100;
		double mm2in = 25.4;
		int w = (int) (wPostermm * dpi);
		int h = (int) (hPostermm * dpi);

		double wimm = 400;
		double himm = 400;
		int wi = (int) (dpi * wimm / mm2in);
		int hi = (int) (dpi * himm / mm2in);

		int wb = (w - wi) / 2;
		int hb = (h - hi) / 2;
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
		opG.drawLine(wb + wi, hb, wb + wi - mark, hb);
		opG.drawLine(wb + wi, hb, wb + wi, hb + mark);

		opG.drawLine(wb + wi, hb + hi, wb + wi - mark, hb + hi);
		opG.drawLine(wb + wi, hb + hi, wb + wi, hb + hi - mark);
		opG.drawLine(wb, hb + hi, wb + mark, hb + hi);
		opG.drawLine(wb, hb + hi, wb, hb + hi - mark);

		opG.drawImage(bi1, wb, hb, wi, hi, null);

		// File fo = new File(opF);
		// ImageIO.write(opImage, "png", fo);

		savePNGFile(opImage, opF, dpi);
	}

}
