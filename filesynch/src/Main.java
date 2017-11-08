import lerrain.tool.Common;
import lerrain.tool.Disk;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by lerrain on 2017/10/29.
 */
public class Main
{
    public static void main(String[] arg)
    {
        String dir1 = arg[0];
        String dir2 = arg[1];
        String dir3;

        dir1 = "Z:/lex/记录";
        dir2 = "G:/record";
        dir3 = "G:/backup/record";

//        dir1 = "Z:/game/CALLUS/VO";
//        dir2 = "G:/数据/V";
//        dir3 = "G:/备份/Material";

        dir1 = "Z:/lex/好友";
        dir2 = "G:/file/memory";
        dir3 = "G:/backup/file/memory";

        FileMgr fm = new FileMgr(dir1, dir2, dir3);
        if (!fm.compare())
        {
            System.out.println("overwrite all (Y/N)?");

            Scanner sc = new Scanner(System.in);
            String ans = sc.next();

            if ("Y".equalsIgnoreCase(ans))
            {
                fm.synch();
            }
        }
        else
        {
            System.out.println("all file is up to date.");
        }
    }
}
