package lerrain.service.filemgr;

import lerrain.tool.Common;

import java.io.File;
import java.util.*;

/**
 * Created by lerrain on 2017/10/19.
 */
public class FileSystem
{
    String root;

    public FileSystem(String root)
    {
        this.root = root;
    }

    private String historyOf(String dest)
    {
        return Common.pathOf(root, "history", dest);
    }

    public Map tree()
    {
        return treeOf(new File(root), ",history,temp,");
    }

    private Map treeOf(File dir, String exclude)
    {
        Map list = new HashMap();

        File[] files = dir.listFiles();
        if (files != null) for (File f : files)
        {
            if (f.isDirectory() && (exclude == null || exclude.indexOf(","+f.getName()+",") < 0))
                list.put(f.getName(), treeOf(f, null));
        }

        return list;
    }

    public List<File> list(String dir)
    {
        List<File> r = new ArrayList<>();

        File d = new File(Common.pathOf(root, dir));
        if (d.isDirectory())
        {
            File[] fs = d.listFiles();
            if (fs != null) for (File f : fs)
            {
                r.add(f);
            }
        }

        return r;
    }

    public boolean copy(File f, String dest)
    {
        File d = new File(Common.pathOf(root, dest));
        if (d.isFile())
        {
            String verDir = historyOf(dest);
            new File(verDir).mkdirs();

            String verFile = Common.pathOf(verDir, Common.getString(new Date(), "yyyyMMddHHmmss"));
            d.renameTo(new File(verFile));
        }
        else if (d.isDirectory())
        {
            System.out.println(dest + " is a directory, can't overwrite.");
            return false;
        }

        d = new File(Common.pathOf(root, dest));
        if (!d.exists())
            return f.renameTo(new File(Common.pathOf(root, dest)));

        return false;
    }

    public boolean delete(String dest)
    {
        File d = new File(Common.pathOf(root, dest));
        if (d.isDirectory())
        {
            File[] fs = d.listFiles();
            if (fs == null || fs.length == 0)
                return d.delete();
        }
        else if (d.isFile())
        {
            String verDir = historyOf(dest);
            new File(verDir).mkdirs();

            String verFile = Common.pathOf(verDir, Common.getString(new Date(), "yyyyMMddHHmmss"));
            return d.renameTo(new File(verFile));
        }

        return false;
    }

    public List<String> getHistory(String dest)
    {
        List<String> r = new ArrayList<>();

        File dir = new File(historyOf(dest));
        File[] files = dir.listFiles();
        if (files != null) for (File f : files)
        {
            r.add(f.getName());
        }

        return r;
    }

    public void rollback(String dest)
    {

    }
}
