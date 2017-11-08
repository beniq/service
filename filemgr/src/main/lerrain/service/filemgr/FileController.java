package lerrain.service.filemgr;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.Date;
import java.util.List;

@Controller
public class FileController
{
    @RequestMapping("/tree.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject tree(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        FileSystem fs = new FileSystem(root);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fs.tree());

        return res;
    }

    @RequestMapping("/list.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject list(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        String path = json.getString("path");

        JSONArray list = new JSONArray();

        FileSystem fsys = new FileSystem(root);
        List<File> files = fsys.list(path);
        if (files != null) for (File f : files)
        {
            if (f.isFile())
            {
                JSONObject fs = new JSONObject();
                fs.put("name", f.getName());
                fs.put("len", f.length());
                fs.put("modifyTime", Common.getTimeString(new Date(f.lastModified())));
                list.add(fs);
            }
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", list);

        return res;
    }

    @RequestMapping("/history.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject history(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        String file = json.getString("file");

        FileSystem fs = new FileSystem(root);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fs.getHistory(file));

        return res;
    }

    @RequestMapping("/compare.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject compare(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        String path = json.getString("path");

        JSONObject files = json.getJSONObject("files");

        FileSystem fs = new FileSystem(root);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fs.compare(path, files));

        return res;
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject delete(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        String file = json.getString("file");

        FileSystem fs = new FileSystem(root);

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", fs.delete(file));

        return res;
    }

    @RequestMapping("/copy.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject copy(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        JSONArray list = json.getJSONArray("files");

        FileSystem fs = new FileSystem(root);

        JSONObject r = new JSONObject();

        for (int i=0;i<list.size();i++)
        {
            JSONArray f = list.getJSONArray(i);

            boolean b = fs.copy(new File(Common.pathOf(root, f.getString(0))), f.getString(1));
            r.put(f.getString(1), b);

            System.out.println("copy " + f.getString(0) + " to " + f.getString(1) + " " + (b ? "success" : "fail"));
        }

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", r);

        return res;
    }
}