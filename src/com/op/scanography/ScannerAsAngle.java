package com.op.scanography;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;

public class ScannerAsAngle extends Base {
    private double dpi = 300;
    private double mm2in = 25.4;

    private static ScannerAsAngle generate = new ScannerAsAngle();
    private String film = "ANH";
    private String weightFile = "VADER";
    private double strokemm = 4.825;
    private String outFileType = "png";
    // private String outFileType = "jpg";

    private double drawAngDeg = 30;
    private double gStart = 0.55;
    private double gMax = 0.44;

    private String opDir = hostDir + "scanography/" + film + "/";
    private double stroke = strokemm * dpi / mm2in; // 180K = 80.55

    private String framesFileName = film + "_LINE.png";
    private String opFileName = film + "_" + weightFile + "_OUT." + outFileType;
    private String weightFileName = film + "_" + weightFile + ".jpg";
    private String fontFile = hostDir + "scanography/FONTS/SLIMBOLD.ttf";
    private String copyright = "© www.movie-maps.net";
    private boolean textDrawn = false;

    private BufferedImage weightbi;
    private BufferedImage framesbi;
    private BufferedImage obi;
    private Graphics2D opG;
    private double wmm = 400;
    private double hmm = wmm;
    private int w = (int) ((wmm * dpi / mm2in));
    private int h = w;
    private double ww = -1;
    private double hh = -1;
    private int length = -1;

    private double bordermm = 35;// 20
    private double border = (int) ((bordermm * dpi / mm2in));
    ;
    private int borderl = -1;
    private int borderr = -1;
    private int bordert = -1;
    private int borderb = -1;
    private double drawAng = Math.toRadians(drawAngDeg);
    private double sinA = Math.sin(drawAng);
    private double cosA = Math.cos(drawAng);
    private double totAngDeg = 180;
    private boolean photos = false;

    public static void main(String[] args) throws Exception {
        generate.doGeneration();

    }

    private void doGeneration() throws IOException {
        System.out.println("Starting Scanner...");
        initImage();
        drawImage();
        save();
    }

    private void drawImage() {
        int numStart = 3;
        double x = 0;
        double y = stroke * numStart * sinA;
        int pos = 0;
        int DIR_B2T = 0;
        int DIR_T2B = 1;
        int dir = DIR_B2T;

        double dx = stroke * sinA;
        double dy = stroke * cosA;

        double ww = w - borderl - borderr;
        double hh = h - bordert - borderb;
        while (true) {
            // pos = draw(pos, x, y);
            pos = photos ? drawPhotos(pos, x, y) : drawLine(pos, x, y);
            if (dir == DIR_B2T) {
                x = x + cosA;
                y = y - sinA;
            } else if (dir == DIR_T2B) {
                x = x - cosA;
                y = y + sinA;
            }

            if (y < 0) {
                dir = DIR_T2B;
                pos = drawCurve(pos, x, y, true);
                x = x + dx;
                y = y + dy;
            } else if (x < 0 && dir == DIR_T2B) {
                dir = DIR_B2T;
                pos = drawCurve(pos, x, y, false);
                x = x + dx;
                y = y + dy;
            } else if (x > ww && dir == DIR_B2T) {
                dir = DIR_T2B;
                pos = drawCurve(pos, x, y, true);
                x = x + dx;
                y = y + dy;
            } else if (y > hh && dir == DIR_T2B) {
                dir = DIR_B2T;
                pos = drawCurve(pos, x, y, false);
                x = x + dx;
                y = y + dy;
            }
            if (pos >= length) {
                System.out.println("len> " + x + "," + y + " pos:" + pos + " / " + length);
                break;
            }
            if (x > ww && y > hh) {
                System.out.println("edge> " + x + "," + y + " pos:" + pos + " / " + length);
                break;
            }
        }

    }

    private int drawCurve(int point, double x1, double y1, boolean rtl) {
        double cx = x1;
        double cy = y1;
        double steps = (Math.PI * stroke);
        int xInc = 1;

        double angStart = rtl ? -drawAngDeg : -drawAngDeg;
        double angEnd = rtl ? -drawAngDeg + 180 : -drawAngDeg - 181;
        double angInc = rtl ? totAngDeg / steps : -totAngDeg / steps;

        for (double angDeg = angStart; rtl ? angDeg < angEnd : angDeg > angEnd; angDeg = angDeg + angInc) {
            int ret = photos ? drawRadialPhotos(point, cx, cy, angDeg, rtl) : drawRadial(point, cx, cy, angDeg, rtl);
            if (ret == -1) {
                return length;
            }
            point = point + xInc;
            // System.out.println(cx + "," + cy + " pos:" + point);
        }

        if (!rtl && !textDrawn && point > length - 2000) {
            drawText(cx, cy);
            textDrawn = true;
        }

        return point;
    }

    private int drawRadial(double subImageX, double x, double y, double angDeg, boolean rtl) {
        double dx = 0.5 * stroke * sinA;
        double dy = 0.5 * stroke * cosA;

        double cx = x + borderl + dx;
        double cy = y + bordert + dy;

        double tx = 0.5 * stroke * Math.sin(Math.toRadians(totAngDeg - angDeg));
        double ty = 0.5 * stroke * Math.cos(Math.toRadians(totAngDeg - angDeg));

        int fbiH = framesbi.getHeight();
        double scale = 1 / ((double) fbiH);
        double g = getGrey((int) x, (int) y);
        double sch = g * scale * stroke;

        AffineTransform at = new AffineTransform();
        at.translate(cx + tx, cy + ty);
        at.rotate(Math.toRadians(angDeg));
        at.scale(1, sch);
        at.translate(0, -fbiH / 2);

        if (subImageX >= length) {
            return -1;
        }
        BufferedImage subF = framesbi.getSubimage((int) subImageX, 0, 1, fbiH);

        BufferedImage temp = new BufferedImage(1, fbiH * 2, BufferedImage.TYPE_INT_RGB);
        temp.getGraphics().drawImage(subF, 0, 0, null);
        temp.getGraphics().drawImage(subF, 0, fbiH * 2, 1, -fbiH, null);
        double yFr = Math.abs(-angDeg - drawAngDeg) / (rtl ? 180 : 181);
        int yH = (int) (yFr * fbiH);
        BufferedImage temp2 = temp.getSubimage(0, yH, 1, fbiH);

        opG.drawImage(temp2, at, null);
        return 0;
    }

    private int drawLine(int pos, double x, double y) {
        int fbiH = framesbi.getHeight();
        double scale = 1 / ((double) fbiH);
        double g = getGrey((int) x, (int) y);
        double sch = g * scale * stroke;

        AffineTransform at = new AffineTransform();
        at.translate(x + borderl, y + bordert);
        at.rotate(-drawAng);
        at.scale(1, sch);
        at.translate(0, -fbiH / 2);

        BufferedImage subF = framesbi.getSubimage(pos, 0, 1, fbiH);
        opG.drawImage(subF, at, null);

        return pos + 1;
    }

    private int drawPhotos(int pos, double x, double y) {
        int fbiH = framesbi.getHeight();
        double scale = 1 / ((double) fbiH);
        double g = getGrey((int) x, (int) y);
        double sch = 1 * scale * stroke;

        int scH = (int) (fbiH * sch);
        int y1 = (fbiH - scH) / 2;

        AffineTransform at = new AffineTransform();
        at.translate(x + borderl, y + bordert);
        at.rotate(-drawAng);
        at.scale(1, g);
        at.translate(0, -scH / 2);

        System.out.println(y1);
        BufferedImage subF = framesbi.getSubimage(pos, y1, 1, scH);
        opG.drawImage(subF, at, null);

        return pos + 1;
    }

    private int drawRadialPhotos(double subImageX, double x, double y, double angDeg, boolean rtl) {
        double dx = 0.5 * stroke * sinA;
        double dy = 0.5 * stroke * cosA;

        double cx = x + borderl + dx;
        double cy = y + bordert + dy;

        double tx = 0.5 * stroke * Math.sin(Math.toRadians(totAngDeg - angDeg));
        double ty = 0.5 * stroke * Math.cos(Math.toRadians(totAngDeg - angDeg));

        int fbiH = framesbi.getHeight();
        double scale = 1 / ((double) fbiH);
        double g = getGrey((int) x, (int) y);
        double sch = g * scale * stroke;

        int scH = (int) (fbiH * sch);
        int y1 = (fbiH - scH) / 2;

        AffineTransform at = new AffineTransform();
        at.translate(cx + tx, cy + ty);
        at.rotate(Math.toRadians(angDeg));
        at.scale(1, g);
        at.translate(0, -scH / 2);

        if (subImageX >= length) {
            return -1;
        }
        BufferedImage subF = framesbi.getSubimage((int) subImageX, y1, 1, scH);

        BufferedImage temp = new BufferedImage(1, scH * 2, BufferedImage.TYPE_INT_RGB);
        temp.getGraphics().drawImage(subF, 0, 0, null);
        temp.getGraphics().drawImage(subF, 0, scH * 2, 1, -scH, null);
        double yFr = Math.abs(-angDeg - drawAngDeg) / (rtl ? 180 : 181);
        int yH = (int) (yFr * scH);
        BufferedImage temp2 = temp.getSubimage(0, yH, 1, scH);

        opG.drawImage(temp2, at, null);
        return 0;
    }

    private double getGrey(double xx, double yy) {
        double x = xx + borderl;
        double y = yy + bordert;
        Color col = getColor(x, y);
        double r = col.getRed();
        double g = col.getGreen();
        double b = col.getBlue();

        double gg = 1 - ((r + g + b) / 3.0) / 255.0;
        return gStart + gMax * gg;
    }

    private Color getColor(double x, double y) {
        // System.out.println(x + ":" + y);
        double xx = ww * (x / w);
        double yy = hh * (y / h);
        int rgb = weightbi.getRGB((int) xx, (int) yy);
        Color col = new Color(rgb);
        return col;
    }

    private void initImage() throws IOException {
        framesbi = ImageIO.read(new File(opDir + framesFileName));
        length = framesbi.getWidth();

        borderl = (int) (border - stroke * sinA * 0.5);
        bordert = (int) (border - stroke * cosA * 0.5);
        borderr = (int) (border + stroke * sinA * 0.5);
        borderb = (int) (border + stroke * cosA * 0.5);

        weightbi = ImageIO.read(new File(opDir + weightFileName));
        ww = weightbi.getWidth();
        hh = weightbi.getHeight();

        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        opG.setColor(Color.WHITE);
        opG.fillRect(0, 0, w, h);

        opG.setColor(Color.BLACK);
        opG.setStroke(new BasicStroke(2));
    }

    private void drawText(double x1, double y1) {
        double d = 0.75;
        double dx = d * 0.25 * stroke * sinA;
        double dy = d * 0.25 * stroke * cosA;

        double x = x1 + borderl - dx;
        double y = y1 + bordert - dy;

        InputStream is;
        try {
            is = new BufferedInputStream(new FileInputStream(fontFile));
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            font = font.deriveFont((float) (stroke * d));

            opG.setFont(font);

            GlyphVector gv = opG.getFont().createGlyphVector(opG.getFontRenderContext(), copyright);

            Shape out = gv.getOutline();
            double fw = out.getBounds2D().getWidth();
            double fh = out.getBounds2D().getHeight();
            AffineTransform at = new AffineTransform();
            AffineTransform tr = AffineTransform.getTranslateInstance(0, fh / 2);
            AffineTransform ro = AffineTransform.getRotateInstance(-drawAng);
            AffineTransform tr2 = AffineTransform.getTranslateInstance(x, y);

            at.concatenate(tr2);
            at.concatenate(ro);
            at.concatenate(tr);

            Shape transformedGlyph = at.createTransformedShape(out);
            opG.setColor(Color.WHITE);
            opG.fill(transformedGlyph);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void save() throws IOException {
        if ("jpg".equals(outFileType)) {
            saveJPGFile(obi, opDir + opFileName, dpi, 0.5f);
        } else if ("png".equals(outFileType)) {
            savePNGFile(obi, opDir + opFileName, dpi);
        }

        File f2 = new File(opDir + film + "_" + drawAngDeg + "_" + strokemm + ".txt");
        f2.createNewFile();
        System.out.println("saved: " + f2.getAbsolutePath());
    }

}
