package com.op.scanography;

import com.op.Base;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.JOIN_MITER;

public class ScanFramer extends Base {

    private static ScanFramer splitter = new ScanFramer();

    private boolean small = true;
    private String film = "MAT";
    private String weightFile = "NEO";
    private String angleFile = film + "_" + weightFile + "_OUT.png";
    private String srcDir = hostOpDir + film + "/";
    private String opDirWeb = "D:/My Documents/My Eclipse/MovieMapperEclipse/git/movies/" + film + "/";


    private int[] MOCK_TYPE_BLACK_FRAME = {0, 344, 361, 797};//type, w, h, d
    private int[] MOCK_TYPE_FLOOR_BLACK = {1, 34, 34, 235};
    private int[] MOCK_TYPE_HANGING = {2, 50, 50, 200};
    private int[] MOCK_TYPE_FLOOR_WOOD = {3, 34, 23, 227};
    private int[] MOCK_TYPE_DESK_BLACK = {4, 53, 53, 206};
    private int[] MOCK_TYPE_LARGE_WALL = {5, 469, 426, 942};
    private int[][] ALL_MOCK_TYPES = {MOCK_TYPE_BLACK_FRAME, MOCK_TYPE_FLOOR_BLACK, MOCK_TYPE_HANGING,
            MOCK_TYPE_FLOOR_WOOD, MOCK_TYPE_DESK_BLACK, MOCK_TYPE_LARGE_WALL};
    private int mockType = 5;//1, 3, 4
    private String mockFile = "MOCK" + mockType;
    private String opFile = film + "_" + weightFile + "_" + mockFile + ".jpg";
    private int xMock = -1;
    private int yMock = -1;
    private int dMock = -1;

    private String frFile = hostOpDir + "kickstarter/ProjectPages/" + mockFile + ".jpg";

    private BufferedImage frbi;
    private BufferedImage ibi;
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = -1;
    private int h = -1;
    private double dpi = 300;

    public static void main(String[] args) throws Exception {
        splitter.doFrame();
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

        xMock = ALL_MOCK_TYPES[mockType][1];
        yMock = ALL_MOCK_TYPES[mockType][2];
        dMock = ALL_MOCK_TYPES[mockType][3];
        if (mockType == MOCK_TYPE_BLACK_FRAME[0]) {
            double fr = (double) dMock / (double) w;
            AffineTransform at = new AffineTransform();
            at.translate(xMock, yMock);
            at.scale(fr, fr);
            opG.drawImage(blur(1), at, null);
            // opG.drawImage(ibi, at, null);
        } else if (mockType == MOCK_TYPE_FLOOR_BLACK[0]) {
            double fr = (double) dMock / (double) w;
            AffineTransform at = new AffineTransform();
            at.translate(xMock, yMock);
            at.scale(fr, fr);
            opG.drawImage(blur(10), at, null);
            // opG.drawImage(ibi, at, null);
        } else if (mockType == MOCK_TYPE_HANGING[0]) {
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
        } else if (mockType == MOCK_TYPE_FLOOR_WOOD[0]) {
            double fr = (double) dMock / (double) w;
            AffineTransform at = new AffineTransform();
            at.translate(xMock, yMock);
            at.scale(fr, fr);
            opG.drawImage(blur(10), at, null);
        } else if (mockType == MOCK_TYPE_FLOOR_BLACK[0]) {
            double fr = (double) dMock / (double) w;
            AffineTransform at = new AffineTransform();
            at.translate(xMock, yMock);
            at.scale(fr, fr);
            opG.drawImage(blur(10), at, null);
        } else if (mockType == MOCK_TYPE_LARGE_WALL[0]) {
            double fr = (double) dMock / (double) w;
            AffineTransform at = new AffineTransform();
            at.translate(xMock, yMock);
            at.scale(fr, fr);
            opG.drawImage(blur(1), at, null);
            addFrameShadow(xMock, yMock, dMock, 25);
        }
    }

    private void addFrameShadow(int xMock, int yMock, int dMock, int shadowD) {
        int str = shadowD;
        int g = 171;
        opG.setColor(new Color(g, g, g, 8));

        for (int s = str; s > 0; s = s - 2) {
            opG.setStroke(new BasicStroke(s, CAP_BUTT, JOIN_MITER, 1.0f, null, 0.0f));
            opG.drawLine(xMock +s - str/2, yMock, xMock +s- str/2, yMock + dMock);
        }
        for (int s = str; s > 0; s = s - 2) {
            opG.setStroke(new BasicStroke(s, CAP_BUTT, JOIN_MITER, 1.0f, null, 0.0f));
            opG.drawLine(xMock +s - str/2, yMock, xMock +s- str/2, yMock + dMock);
            opG.drawLine(xMock, yMock + s - str/2, xMock + dMock, yMock +s - str/2);
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
