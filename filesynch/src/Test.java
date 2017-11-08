import lerrain.tool.Disk;

import java.io.File;

/**
 * Created by lerrain on 2017/11/5.
 */
public class Test
{
    public static void main(String[] args) throws Exception
    {
        File f1 = new File("Z:\\FFOutput/2010-01-14 15'22'32.jpg");
        File f2 = new File("Z:\\FFOutput/1.jpg");

        byte[] b1 = Disk.load(f1);
        byte[] b2 = Disk.load(f1);

        int len = Math.min(b1.length, b2.length);

        for (int i=1;i<=len;i++)
        {
            if (b1[b1.length - i] != b2[b2.length - i])
                System.out.print(i + ", ");
        }
    }
}
