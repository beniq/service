package lerrain.project.activity;

import com.alibaba.fastjson.JSONObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.sun.imageio.plugins.jpeg.JPEGImageWriter;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;

public class ImageTool
{
    public static File compress(File src, String path, String dest)
    {
        if (src.length() < 60000) //太小，没必要压缩
            return copy(src, path, dest);

        try
        {
            BufferedImage bi = ImageIO.read(src);
            int w = bi.getWidth();
            int h = bi.getHeight();

            if ((w * h < 750 * 900 && src.getName().endsWith(".png")) || src.length() * 1.0f / w / h < 0.2f) //压缩比已经很好，不压缩
                return copy(src, path, dest);

            File dst = new File(Common.pathOf(path, dest + ".jpg"));
            if (dst.exists())
                dst.delete();

            try (FileOutputStream fos = new FileOutputStream(dst); ImageOutputStream ios = ImageIO.createImageOutputStream(fos);)
            {
                JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();
                imageWriter.setOutput(ios);

                IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bi), null);

                JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
                jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(0.6f);

                imageWriter.write(imageMetaData, new IIOImage(bi, null, null), null);
                imageWriter.dispose();
            }

            return dst;
        }
        catch (Exception e)
        {
            e.printStackTrace();

            return copy(src, path, dest);
        }
    }

    public static void cut(File dst, File src, float dw, float dh, float x, float y, float w, float h)
    {
        try
        {
            BufferedImage bi = ImageIO.read(src);

            int xx = Math.round(x * bi.getWidth() / dw);
            int yy = Math.round(y * bi.getHeight() / dh);
            int ww = Math.round(w * bi.getWidth() / dw);
            int hh = Math.round(h * bi.getHeight() / dh);

            ColorModel cm = bi.getColorModel();
            BufferedImage n = new BufferedImage(cm, cm.createCompatibleWritableRaster(ww, hh), cm.isAlphaPremultiplied(), null);

            Graphics2D g = n.createGraphics();
            g.drawImage(bi, 0, 0, ww, hh, xx, yy, xx + ww, yy + hh, null);

            try (FileOutputStream fos = new FileOutputStream(dst); ImageOutputStream ios = ImageIO.createImageOutputStream(fos);)
            {
                JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();
                imageWriter.setOutput(ios);

                IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(n), null);

                JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
                jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(0.6f);

                imageWriter.write(imageMetaData, new IIOImage(n, null, null), null);
                imageWriter.dispose();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static File copy(File src, String path, String dest)
    {
        String suffix = src.getName();
        suffix = suffix.substring(suffix.length() - 4);

        File dst = new File(Common.pathOf(path, dest + suffix));
        Disk.copy(src, dst, true);

        return dst;
    }

    public static void draw(File src, JSONObject param, OutputStream fos)
    {
        try
        {
            BufferedImage n = ImageIO.read(src);
            Graphics2D g = n.createGraphics();

            int w = n.getWidth();
            int h = n.getHeight();

            String qrUrl = param.getString("qrUrl");
            if (!Common.isEmpty(qrUrl))
            {
                double qrw = Common.doubleOf(param.get("qrw"), 0) * w / 100;
                double qrx = Common.doubleOf(param.get("qrx"), 0) * w / 100 - qrw / 2;
                double qry = Common.doubleOf(param.get("qry"), 0) * h / 100 - qrw / 2;

                try
                {
                    Map hints = new HashMap();
                    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    BitMatrix bitMatrix = multiFormatWriter.encode(qrUrl, BarcodeFormat.QR_CODE, 400, 400, hints);

                    BufferedImage bi = MatrixToImageWriter.toBufferedImage(bitMatrix);
                    g.drawImage(bi, (int)qrx, (int)qry, (int)qrw, (int)qrw, null);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            String name = param.getString("cust");
            float size = (float)Common.doubleOf(param.getString("nameFontSize"), 0);
            if (!Common.isEmpty(name) && size > 0)
            {
                double nx = Common.doubleOf(param.get("namex"), 0) * w / 100;
                double ny = Common.doubleOf(param.get("namey"), 0) * h / 100;

//                g.setFont(g.getFont().deriveFont(size));
                g.setFont(FontTool.getFont("微软雅黑", g.getFont()).deriveFont(size));
                Rectangle2D r2 = g.getFontMetrics().getStringBounds(name, g);
                g.drawString(name, (float)nx - (float)r2.getWidth() / 2, (float)ny + (float)r2.getHeight() / 2);
            }

            try (ImageOutputStream ios = ImageIO.createImageOutputStream(fos);)
            {
                JPEGImageWriter imageWriter = (JPEGImageWriter) ImageIO.getImageWritersBySuffix("jpg").next();
                imageWriter.setOutput(ios);

                IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(n), null);

                JPEGImageWriteParam jpegParams = (JPEGImageWriteParam) imageWriter.getDefaultWriteParam();
                jpegParams.setCompressionMode(JPEGImageWriteParam.MODE_EXPLICIT);
                jpegParams.setCompressionQuality(0.6f);

                imageWriter.write(imageMetaData, new IIOImage(n, null, null), null);
                imageWriter.dispose();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
