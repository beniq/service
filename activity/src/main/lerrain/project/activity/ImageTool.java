package lerrain.project.activity;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import lerrain.tool.Common;
import lerrain.tool.Disk;

import javax.imageio.ImageIO;
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
            try (FileOutputStream fos = new FileOutputStream(dst))
            {
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
                JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
                param.setQuality(0.6f, true);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(bi);
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
