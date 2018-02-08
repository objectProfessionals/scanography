package com.op.colorwheel;

import com.op.scanography.Base;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ColorWheel extends Base {

    public static ColorWheel wheel = new ColorWheel();
    private String type = "ANH";
    private String file1 = type+"wheelOut";
    private String ipF = hostOpDir + type+"/"+type+"_LINE.png";
    private String opF = hostOpDir + type+"/" + file1 + ".png";
    private int w = 1000;
    private int h = 1000;
    private BufferedImage ibi;
    private BufferedImage obi;
    private Graphics2D opG;

    public static void main(String[] args) throws Exception {
        wheel.draw();
    }

    private void draw() throws IOException {
        initFile();

        int dd = 1;
        int ww = 1;
        for (int i=0; i<ibi.getWidth(); i=i+dd) {
            int rgb = ibi.getRGB(i, ibi.getHeight()/2);
            int r= (rgb >> 16) & 0x000000FF;
            int g = (rgb >>8 ) & 0x000000FF;
            int b = (rgb) & 0x000000FF;

            Color c = new Color(r,g,b);
            int[] xy = getPositions(r,g,b);
            opG.setColor(c);
            opG.fillRect(xy[0], xy[1], ww, ww);
        }

//        for (int i=0; i<5000; i++) {
//            int r = (int)(255*Math.random());
//            int g = (int)(255*Math.random());
//            int b = (int)(255*Math.random());
//        }
        savePNGFile(obi, opF, 300);
    }

    private void initFile() throws IOException {
        ibi = ImageIO.read(new File(ipF));



        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w, h);

    }


    private int[] getPositions(int r, int g, int b) {
        float[] hsv = new float[3];

        double radius = 500;
        double cx = 500;
        double cy = 500;

        Color.RGBtoHSB(r, g, b, hsv);
        double h = hsv[0];
        double s = hsv[1];
        double v = hsv[2];
        double radians = h*Math.PI*2;
        double degs = Math.toDegrees(radians);
        double sat = v*radius;

        double x = cx + sat*Math.cos(radians);
        double y = cy + sat*Math.sin(radians);

        int[] arr = {(int)x, (int)y};
        return arr;
    }


}
