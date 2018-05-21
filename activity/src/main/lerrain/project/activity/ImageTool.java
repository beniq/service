package lerrain.project.activity;

import com.sun.imageio.plugins.jpeg.JPEGImageWriter;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

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

    private static File copy(File src, String path, String dest)
    {
        String suffix = src.getName();
        suffix = suffix.substring(suffix.length() - 4);

        File dst = new File(Common.pathOf(path, dest + suffix));
        Disk.copy(src, dst);

        return dst;
    }
}
