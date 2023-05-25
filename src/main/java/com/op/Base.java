package com.op;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Iterator;

public class Base {

    public String hostDir = "../host/scanography/";
    public String hostOpDir = hostDir+"op/";
    public String hostSrcDir = hostDir+"src/";

    public static void savePNGFile(BufferedImage opImage, String filePath, double dpi) {
        try {
            File outfile = new File(filePath);
            // Find a jpeg writer
            ImageWriter writer = null;
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("png");
            IIOMetadata metadata = null;
            if (iter.hasNext()) {
                writer = iter.next();
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(0.05f);
                }
                ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
                        .createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
                metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
                if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                    // continue;
                }
                double dpmm = dpi / 25.4;
                IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", Double.toString(dpmm));
                IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", Double.toString(dpmm));
                IIOMetadataNode dim = new IIOMetadataNode("Dimension");
                dim.appendChild(horiz);
                dim.appendChild(vert);
                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                root.appendChild(dim);
                metadata.mergeTree("javax_imageio_1.0", root);
            }
            // Prepare output file
            ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
            writer.setOutput(ios);
            // Write the image
            writer.write(null, new IIOImage(opImage, null, metadata), null);
            // Cleanup
            ios.flush();
            writer.dispose();
            ios.close();
            opImage = null;
            System.gc();
            System.out.println("Saved " + filePath);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void saveJPGFile(BufferedImage opImage, String file, double dpi, float quality) {
        try {
            File outfile = new File(file);

            // Find a jpeg writer
            ImageWriter writer = null;
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
            IIOMetadata metadata = null;
            if (iter.hasNext()) {
                writer = iter.next();
                ImageWriteParam writeParam = writer.getDefaultWriteParam();
                if (writeParam.canWriteCompressed()) {
                    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    writeParam.setCompressionQuality(1f);
                }
                ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier
                        .createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
                metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
                if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                    // continue;
                }
                double dpmm = dpi / 25.4;
                IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", Double.toString(dpmm));
                IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", Double.toString(dpmm));
                IIOMetadataNode dim = new IIOMetadataNode("Dimension");
                dim.appendChild(horiz);
                dim.appendChild(vert);
                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                root.appendChild(dim);
                metadata.mergeTree("javax_imageio_1.0", root);
            }
            // Prepare output file
            ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
            writer.setOutput(ios);
            // Write the image
            writer.write(null, new IIOImage(opImage, null, metadata), null);
            // Cleanup
            ios.flush();
            writer.dispose();
            ios.close();
            opImage = null;
            System.gc();
            System.out.println("Saved " + outfile);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void saveJPGFile2(BufferedImage opImage, File outfile, double dpi) {
        try {
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
            jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpgWriteParam.setCompressionQuality(1f);

            jpgWriter.setOutput(ImageIO.createImageOutputStream(outfile));
            IIOImage outputImage = new IIOImage(opImage, null, null);
            jpgWriter.write(null, outputImage, jpgWriteParam);
            jpgWriter.dispose();
            System.out.println("Saved " + outfile.getPath());
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
