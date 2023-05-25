package com.op.spectogram;

import com.op.Base;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class CircularSpectogram extends Base {

    private static final CircularSpectogram generate = new CircularSpectogram();

    public String dir = hostOpDir + "ANH/";

    private String ipPlot1 = "tracks001.png";
    private String ipPlot2 = "tracks002.png";
    private String ipPlot3 = "tracks003.png";
    private String ipPlot4 = "tracks004.png";
    private String opPlot = "track.png";
    private String palPlot = "palette.png";
    private int cropX = 142;
    private int cropY = 5;
    private int cropW = 1755;
    private int cropH = 927;
    private double minF = 0;
    private double maxF = 20000;

    private BufferedImage ibi;
    private Graphics2D ipG;
    private BufferedImage obi;
    private Graphics2D opG;
    private int w = 2000;
    private int h = 2000;
    public String opSpec = "spectogram.png";

    public static void main(String[] args) throws Exception {
        generate.doGeneration();

    }

    private void doGeneration() throws IOException {
        System.out.println("Starting Spectogram...");
        initImage();

        drawImage();

        saveResultImage();
    }

    private void drawImage() throws IOException {
        NavigableMap<Integer, Color> cols = new TreeMap<>();
        ArrayList<Color> allCols = new ArrayList<>();
        BufferedImage bi = ImageIO.read(new File(dir + palPlot));
        for (int y = 0; y < bi.getHeight(); y++) {
            int rgb = bi.getRGB(0, y);
            Color col = new Color(rgb);
            cols.put(col.getRGB(), col);
            allCols.add(col);
        }


        int innerRad = 100;
        int outerRad = 900;
        int cx = w / 2;
        int cy = h / 2;
        int spectW = cropW * 4;
        int spectStart = 10;
        int spectEnd = 6679;
        List<GeneralPath> paths = new ArrayList<>();
        double totAng = 350;
        for (int y = 0; y < cropH; y = y + 1 + (int)Math.log(cropH-y)) {
            GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD,
                    spectEnd);
            double r = innerRad + (outerRad - innerRad) * ((double) y / (double) cropH);
            double c = 2 * Math.PI * r;
            int xF = (int)(spectEnd/totAng);
            for (int x = spectStart; x < spectEnd; x = x + xF) {
                double ang = -90 + totAng * (double) x / (double) spectEnd;
                double radians = Math.toRadians(ang);
                int i = ibi.getRGB(x, y);
                Color col = new Color(i);
                Integer colInd = cols.ceilingKey(col.getRGB());
                if (colInd != null) {
                    int index = allCols.indexOf(cols.get(colInd));
                    double value = 1 - ((double) index) / ((double) (bi.getHeight()));
                    double rr = (value * 100);
                    int gg = (int) (255 * value);
                    int xx = (int) (cx + r * Math.cos(radians));
                    int yy = (int) (cy - rr + r * Math.sin(radians));

                    if (path.getCurrentPoint() == null) {
                        path.moveTo(xx, yy);
                    } else {
                        path.lineTo(xx, yy);
                    }

//                    Color put = new Color(gg, gg, gg);
//                    int d = 1;
//                    opG.setColor(put);
//                    opG.fillRect(xx, yy, d, d);
                } else {
                    int yi = 0;
                    //something
                }
            }
            paths.add(path);
        }

        System.out.println(paths.size());
        int c = 0;
        for (GeneralPath path : paths) {
            opG.setColor(allCols.get(allCols.size() - c - 1));
            opG.draw(path);
            c = c + (allCols.size()/paths.size());
        }
    }

    private void drawPalette() {

        ArrayList<Color> cols = new ArrayList<>();
        for (int y = 0; y < cropH; y++) {
            for (int x = 0; x < cropW; x++) {
                int i = ibi.getRGB(x, y);
                Color col = new Color(i);
                if (!cols.contains(col)) {
                    cols.add(col);
                }
            }
        }

        Collections.sort(cols, new Comparator<Color>() {
            @Override
            public int compare(Color o1, Color o2) {
                int rgb1 = o1.getRGB();
                int r1 = (rgb1 >> 16) & 0x000000FF;
                int g1 = (rgb1 >> 8) & 0x000000FF;
                int b1 = (rgb1) & 0x000000FF;

                int rgb2 = o2.getRGB();
                int r2 = (rgb2 >> 16) & 0x000000FF;
                int g2 = (rgb2 >> 8) & 0x000000FF;
                int b2 = (rgb2) & 0x000000FF;

                int gr1 = (r1 + g1 + b1) / 3;
                int gr2 = (r2 + g2 + b2) / 3;
                return gr2 - gr1;
            }
        });

        BufferedImage pal = new BufferedImage(100, cols.size(), BufferedImage.TYPE_INT_RGB);
        Graphics2D opGpal = (Graphics2D) pal.getGraphics();
        opGpal.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        int c = 0;
        for (Color col : cols) {
            opGpal.setColor(col);
            opGpal.fillRect(0, c, 100, 1);
            c++;
        }
        savePNGFile(pal, dir + palPlot, 720);
    }

    private void initImage() throws IOException {
        BufferedImage bi1 = ImageIO.read(new File(dir + ipPlot1));
        BufferedImage bi2 = ImageIO.read(new File(dir + ipPlot2));
        BufferedImage bi3 = ImageIO.read(new File(dir + ipPlot3));
        BufferedImage bi4 = ImageIO.read(new File(dir + ipPlot4));

        bi1 = bi1.getSubimage(cropX, cropY, cropW, cropH);
        bi2 = bi2.getSubimage(cropX, cropY, cropW, cropH);
        bi3 = bi3.getSubimage(cropX, cropY, cropW, cropH);
        bi4 = bi4.getSubimage(cropX, cropY, cropW, cropH);

        BufferedImage obiPlot;
        Graphics2D opGPlot;
        obiPlot = new BufferedImage(cropW * 4, cropH, BufferedImage.TYPE_INT_ARGB);
        opGPlot = (Graphics2D) obiPlot.getGraphics();
        opGPlot.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        opGPlot.setColor(Color.BLACK);
        opGPlot.fillRect(0, 0, cropW * 4, cropH);
        opGPlot.drawImage(bi1, null, 0, 0);
        opGPlot.drawImage(bi2, null, cropW, 0);
        opGPlot.drawImage(bi3, null, cropW * 2, 0);
        opGPlot.drawImage(bi4, null, cropW * 3, 0);

        savePNGFile(obiPlot, dir + opPlot, 720);

        ibi = ImageIO.read(new File(dir + opPlot));
        ipG = (Graphics2D) ibi.getGraphics();
        obi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        opG = (Graphics2D) obi.getGraphics();
        opG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        opG.setColor(Color.BLACK);
        opG.fillRect(0, 0, w, h);
    }


    private void saveResultImage() {
        savePNGFile(obi, dir + opSpec, 720);
    }
}
