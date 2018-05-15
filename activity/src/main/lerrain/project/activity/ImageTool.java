package lerrain.project.activity;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

public class ImageTool
{
    public static File compress(File src)
    {
        if (src.length() < 60000) //太小，没必要压缩
            return src;

        File dst;

        try
        {
            BufferedImage bi = ImageIO.read(src);
            int w = bi.getWidth();
            int h = bi.getHeight();

            if (src.length() * 1.0f / w / h < 0.2f) //压缩比已经很好，不压缩
                return src;

            String fn = src.getAbsolutePath().toLowerCase();
            fn = fn.substring(0, fn.lastIndexOf(".")) + "_s.jpg";

            dst = new File(fn);

            try (FileOutputStream fos = new FileOutputStream(dst))
            {
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
                JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
                param.setQuality(0.6f, true);
                encoder.setJPEGEncodeParam(param);
                encoder.encode(bi);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            dst = src;
        }

        return dst;
    }
}
