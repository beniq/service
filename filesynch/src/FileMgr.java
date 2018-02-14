import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.omg.CORBA.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by lerrain on 2017/10/29.
 */
public class FileMgr
{
    long ALWAYS_COMPARE = 1024L * 1024 * 16;

    String dir1, dir2, dir3;

    Map<String, String> res;
    Map<File, File> ren;

    public FileMgr(String dir1, String dir2, String history)
    {
        this.dir1 = dir1;
        this.dir2 = dir2;
        this.dir3 = history;
    }

    public boolean compare()
    {
        System.out.println("SRC: " + dir1);
        System.out.println("DST: " + dir2);

        res = new LinkedHashMap<>();
        ren = new LinkedHashMap<>();

        List<String> list1 = listFile(new File(dir1), "", new ArrayList<String>());
        List<String> list2 = listFile(new File(dir2), "", new ArrayList<String>());

        for (String dfs : list2)
        {
            File src = new File(dir1 + "/" + dfs);
            File dst = new File(dir2 + "/" + dfs);

            if (dst.isDirectory() && !src.isDirectory())
            {
                ren.put(dst, new File(dir3 + "/" + dfs));
            }
            else if (dst.isFile() && !src.isFile())
            {
                long len = dst.length();

                for (String str : list1)
                {
                    File s1 = new File(dir1 + "/" + str);
                    if (s1.isFile() && s1.length() == len && s1.lastModified() == dst.lastModified())
                    {
                        File s2 = new File(Common.pathOf(dst.getParent(), s1.getName()));
                        if (!s2.exists())
                        {
                            boolean same = true;

                            if (len < ALWAYS_COMPARE)
                            {
                                byte[] b1 = md5Of(dst);
                                byte[] b2 = md5Of(s1);

                                same = b1.length == b2.length;
                                if (same)
                                {
                                    for (int i = 0; i < b1.length; i++)
                                    {
                                        if (b1[i] != b2[i])
                                        {
                                            same = false;
                                            break;
                                        }
                                    }
                                }

                            }

                            if (same)
                            {
                                list1.remove(str);

                                ren.put(dst, s2);
                                break;
                            }
                        }
                    }
                }

                if (!ren.containsKey(dst))
                    ren.put(dst, new File(dir3 + "/" + dfs));
            }
        }

        for (String str : list1)
        {
            System.out.print("scanning " + str + " ...");

            String diff = compareFile(str);
            System.out.println(diff == null ? "" : diff);

            if (diff != null)
                res.put(str, diff);
        }

        for (Map.Entry<File, File> e : ren.entrySet())
        {
            System.out.println(String.format("MOVE %s -> %s", e.getKey().toString(), e.getValue().toString()));
        }

        for (Map.Entry<String, String> e : res.entrySet())
        {
            System.out.println(String.format("COPY %s -> %s", e.getKey(), e.getValue()));
        }

        return res.isEmpty() && ren.isEmpty();
    }

    public void synch()
    {
        if (!ren.isEmpty())
        {
            for (Map.Entry<File, File> e : ren.entrySet())
            {
                File f2 = e.getKey();
                File f3 = e.getValue();

                System.out.println(String.format("move %s -> %s", f2.getAbsolutePath(), f3.getAbsolutePath()));

                File parentDir = f3.getParentFile();
                if (!parentDir.isDirectory())
                    parentDir.mkdirs();

                f2.renameTo(f3);
            }
        }

        if (!res.isEmpty())
        {
            for (Map.Entry<String, String> e : res.entrySet())
            {
                File src = new File(dir1 + "/" + e.getKey());
                File dst = new File(dir2 + "/" + e.getKey());

                File dir = dst.getParentFile();
                if (!dir.isDirectory())
                    dir.mkdirs();

                System.out.println(String.format("copy %s -> %s", src, dst));
                Disk.copy(src, dst);

                if (dst.lastModified() != src.lastModified())
                    dst.setLastModified(src.lastModified());
            }
        }
    }

    private String compareFile(String str)
    {
        File f2 = new File(dir2 + "/" + str);
        if (!f2.isFile())
        {
            return "NEW";
        }
        else
        {
            File f1 = new File(dir1 + "/" + str);
            boolean same = f1.length() == f2.length();
            if (!same)
                return "LENGTH";

            long time = System.currentTimeMillis() - f2.lastModified();
            if (f1.length() < ALWAYS_COMPARE || str.indexOf("WJ-20171202") >= 0)
            {
                byte[] b1 = md5Of(f1);
                byte[] b2 = md5Of(f2);

                same = b1.length == b2.length;
                if (same)
                {
                    for (int i = 0; i < b1.length; i++)
                    {
                        if (b1[i] != b2[i])
                        {
                            same = false;
                            break;
                        }
                    }
                }

                if (!same)
                    return "MD5";
            }

            if (f1.lastModified() != f2.lastModified())
                f2.setLastModified(f1.lastModified());
        }

        return null;
    }

    private static List<String> listFile(File root, String path, List<String> list)
    {
        File[] fs = root.listFiles();
        if (fs != null) for (File f : fs)
        {
            if (f.isDirectory())
            {
                listFile(f, path + f.getName() + "/", list);
            }
            else if (f.isFile())
            {
                if (!f.getName().equalsIgnoreCase("desktop.ini"))
                    list.add(path + f.getName());
            }
        }

        return list;
    }

    private static byte[] md5Of(File file)
    {
        MessageDigest messageDigest = null;

        byte[] b = new byte[1024 * 1024];

        try (InputStream is = new FileInputStream(file))
        {
            messageDigest = MessageDigest.getInstance("MD5");

            List<byte[]> res = new ArrayList<>();

            int c = -1;
            int t = 0;

            while ((c = is.read(b)) >= 0)
            {
                messageDigest.reset();
                messageDigest.update(b);

                byte[] block = messageDigest.digest();
                res.add(block);

                t += block.length;
            }

            byte[] md5 = new byte[t];
            t = 0;

            for (byte[] bb : res)
            {
                System.arraycopy(bb, 0, md5, t, bb.length);
                t += bb.length;
            }

            messageDigest.reset();
            messageDigest.update(md5);
        }
        catch (Exception e)
        {
            throw new RuntimeException("md5 exception", e);
        }

        return messageDigest.digest();
    }
}
