package com.op.colorwheel;

import com.op.Base;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

public class ColorWheel extends Base {

    public static ColorWheel wheel = new ColorWheel();
    private String type = "ANH";
    private String file1 = type + "wheelOut";
    private String ipF = hostOpDir + type + "/" + type + "_LINE1.png";
    private String opF = hostOpDir + "ANH/" + file1 + ".png";
    private double dotF = 0.001;
    private int w = 2000;
    private int h = w;
    private BufferedImage ibi;
    private BufferedImage obi;
    private Graphics2D opG;

    public static void main(String[] args) throws Exception {
        wheel.draw();
    }

    private void draw() throws IOException {
        initFile();

        int dd = 1;
        int count = 0;
        int sep = 1;
        HashMap<Color, Integer> col2num = new HashMap<>();
        TreeMap<Float, Color> v2col = new TreeMap<>();
        double maxNum = 0;
        for (int i = 0; i < ibi.getWidth(); i = i + dd) {
            int rgb = ibi.getRGB(i, 0);
            int r = (rgb >> 16) & 0x000000FF;
            int g = (rgb >> 8) & 0x000000FF;
            int b = (rgb) & 0x000000FF;

            int rr = sep * (r / sep);
            int gg = sep * (g / sep);
            int bb = sep * (b / sep);

            Color c = new Color(rr, gg, bb);

            float[] hsv = new float[3];
            Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsv);
            v2col.put(hsv[0]*hsv[1]*hsv[2], c);

            if (col2num.get(c) == null) {
                col2num.put(c, 1);
            } else {
                int newVal = col2num.get(c).intValue() + 1;
                col2num.put(c, newVal);
                if (maxNum < newVal) {
                    maxNum = newVal;
                }
            }
            count++;
        }

        System.out.println(maxNum);

        drawRef();
        //drawPlots(hm, maxNum, true);
        drawPlots(v2col, col2num, maxNum, false);

        System.out.println("v2col=" + v2col.keySet().size());
        System.out.println("col2num=" + col2num.keySet().size());

        System.out.println("count=" + count);
        savePNGFile(obi, opF, 300);
    }

    private void drawRef() {
        int th = w / 200;
        int ii = w / 2;
        for (int i = Color.BLACK.getRGB(); i < Color.WHITE.getRGB(); i = i + ii) {
            Color col = new Color(i);
            double[] xyz = getRefPositions(th, col.getRed(), col.getGreen(), col.getBlue());
            int x = (int) xyz[0];
            int y = (int) xyz[1];
            float[] hsv = new float[3];
            Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), hsv);

            Color col2 = Color.getHSBColor(hsv[0], 1, 1);
            opG.setColor(col2);
            opG.fillOval(x - th, y - th, th * 2, th * 2);
        }
    }

    private void drawPlots(TreeMap<Float, Color> v2col, HashMap<Color, Integer> col2num, double maxNum, boolean shadow) {
        double rff = 0.01;
        double rf = ((double) w) * rff;
        double zff = 0.02;
        double zf = ((double) w) * zff;
        double minww = ((double) w) * dotF;
//        for (Float v : v2col.keySet()) {
//            Color col = v2col.get(v);
        for (Color col : col2num.keySet()) {
            double num = col2num.get(col);
            double strC = num;
            float str = (float) Math.pow(strC, 0.25);
            double[] xyz = getPositions(col.getRed(), col.getGreen(), col.getBlue());
            int x = (int) xyz[0];
            int y = (int) xyz[1];
            double z = xyz[2];
            int ww = (int) (minww + ((z * rf)));
            //int ww = (int) (minww +str*10);
            if (shadow) {
                opG.setColor(Color.LIGHT_GRAY);
                int yShadow = (int) (y + z * zf);
                opG.fillOval(x - ww, yShadow - ww, ww * 2, ww * 2);
            } else {
                opG.setColor(col);
                opG.fillOval(x - ww, y - ww, ww * 2, ww * 2);
            }
        }
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


    private double[] getRefPositions(int th, int r, int g, int b) {

        double radius = (double) (-th*2 + w / 2);
        double cx = w / 2;
        double cy = h / 2;

        float[] hsv = new float[3];
        Color.RGBtoHSB(r, g, b, hsv);
        double h = hsv[0];
        double s = hsv[1];
        double v = hsv[2];
        double radians = h * Math.PI * 2 - Math.PI * 0.5;

        double x = radius * Math.cos(radians);
        double y = radius * Math.sin(radians);

        double[] arr = {cx + x, cy + y};
        return arr;

    }

    private double[] getPositions(int r, int g, int b) {
        float[] hsv = new float[3];

        double radius = 1 * (double) (w / 2);
        double cx = w / 2;
        double cy = h / 2;

        Color.RGBtoHSB(r, g, b, hsv);
        double h = hsv[0];
        double s = hsv[1];
        double v = hsv[2];
        double radians = h * Math.PI * 2 - Math.PI * 0.5;
        double sat = ((v * s)) * radius;
        double val = v;

        double x = sat * Math.cos(radians);
        double y = sat * Math.sin(radians);
        double z = val;
        double rr = Math.sqrt(x * x + y * y);
        x = x + cx;
        y = y + cy;

        double[] arr = {x, y, z, rr};
        return arr;
    }

    private void draw2() {
        int rad = w/2;
        BufferedImage img = new BufferedImage(rad, rad, BufferedImage.TYPE_INT_RGB);

        // Center Point (MIDDLE, MIDDLE)
        int centerX = img.getWidth() / 2;
        int centerY = img.getHeight() / 2;
        int radius = (img.getWidth() / 2) * (img.getWidth() / 2);

        // Red Source is (RIGHT, MIDDLE)
        int redX = img.getWidth();
        int redY = img.getHeight() / 2;
        int redRad = img.getWidth() * img.getWidth();

        // Green Source is (LEFT, MIDDLE)
        int greenX = 0;
        int greenY = img.getHeight() / 2;
        int greenRad = img.getWidth() * img.getWidth();

        // Blue Source is (MIDDLE, BOTTOM)
        int blueX = img.getWidth() / 2;
        int blueY = img.getHeight();
        int blueRad = img.getWidth() * img.getWidth();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                int a = i - centerX;
                int b = j - centerY;

                int distance = a * a + b * b;
                if (distance < radius) {
                    int rdx = i - redX;
                    int rdy = j - redY;
                    int redDist = (rdx * rdx + rdy * rdy);
                    int redVal = (int) (255 - ((redDist / (float) redRad) * 256));

                    int gdx = i - greenX;
                    int gdy = j - greenY;
                    int greenDist = (gdx * gdx + gdy * gdy);
                    int greenVal = (int) (255 - ((greenDist / (float) greenRad) * 256));

                    int bdx = i - blueX;
                    int bdy = j - blueY;
                    int blueDist = (bdx * bdx + bdy * bdy);
                    int blueVal = (int) (255 - ((blueDist / (float) blueRad) * 256));

                    Color c = new Color(redVal, greenVal, blueVal);

                    float hsbVals[] = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

                    Color highlight = Color.getHSBColor(hsbVals[0], hsbVals[1], 1);

                    img.setRGB(i, j, RGBtoHEX(highlight));
                } else {
                    img.setRGB(i, j, 0xFFFFFF);
                }
            }
        }

    }

    public int RGBtoHEX(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xffffff);
        if (hex.length() < 6) {
            if (hex.length() == 5)
                hex = "0" + hex;
            if (hex.length() == 4)
                hex = "00" + hex;
            if (hex.length() == 3)
                hex = "000" + hex;
        }
        hex = "#" + hex;
        return Integer.decode(hex);
    }

}
