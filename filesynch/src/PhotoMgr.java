import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class PhotoMgr
{
    public static void main(String[] args)
    {
        File dir = new File("Z:\\lex\\ºÃÓÑ\\ÑîÐ¦Ð¦¡¡¡ï");
        for (File f : dir.listFiles())
        {
            if (!f.getName().toLowerCase().endsWith(".jpg"))
                continue;

            String s = readPic(f);
            if (s != null && !"".equals(s.trim()) && Integer.parseInt(s.substring(0, 4)) > 2000)
            {
                s = s.substring(0, 4) + "-" + s.substring(5, 7) + "-" + s.substring(8, 10) + " " + s.substring(11, 13) + "'" + s.substring(14, 16) + "'" + s.substring(17, 19);

                for (int i = 0; i < 100; i++)
                {
                    File f2 = new File(dir.getAbsolutePath() + "\\" + s + (i == 0 ? "" : " (" + i + ")") + ".jpg");

                    if (f.getName().equals(f2.getName()))
                        break;

                    System.out.println(f + " -> " + f2);
                    if (f.getName().equalsIgnoreCase(f2.getName()) || !f2.exists())
                    {
                        f.renameTo(f2);
                        break;
                    }
                }

//			    try {
//			        String cmd = "ren \"" + f.getAbsolutePath() + "\" \"" + dir.getAbsolutePath() + "\\" + s + ".jpg\"";
//					System.out.println(cmd);
//
//					Runtime.getRuntime().exec(cmd).waitFor();
//			        } catch (Exception e) {
//			        e.printStackTrace();
//			        }
            }
        }
    }

    private static String readPic(File jpegFile)
    {
        Metadata metadata;
        String time = null;
        try
        {
            metadata = JpegMetadataReader.readMetadata(jpegFile);
            Iterator<Directory> it = metadata.getDirectories().iterator();
            while (it.hasNext())
            {
                Directory exif = it.next();
                Iterator<Tag> tags = exif.getTags().iterator();
                while (tags.hasNext())
                {
                    Tag tag = (Tag) tags.next();

                    if ("Date/Time".equalsIgnoreCase(tag.getTagName()))
                        return tag.getDescription();
                    else if (tag.getTagName().startsWith("Date/Time"))
                        time = tag.getDescription();
                }

            }
        }
        catch (JpegProcessingException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return time;
    }
}
