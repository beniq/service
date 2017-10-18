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

@Controller
public class FileController
{
    @RequestMapping("/health")
    @ResponseBody
    @CrossOrigin
    public String health()
    {
        return "success";
    }

    @RequestMapping("/tree.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject tree(@RequestBody JSONObject json)
    {
        String root = json.getString("root");

        JSONObject res = new JSONObject();
        res.put("result", "success");
        res.put("content", pathOf(new File(root)));

        return res;
    }

    private JSONObject pathOf(File dir)
    {
        JSONObject list = new JSONObject();

        File[] files = dir.listFiles();
        if (files != null) for (File f : files)
        {
            if (f.isDirectory())
                list.put(f.getName(), pathOf(f));
        }

        return list;
    }

    @RequestMapping("/list.json")
    @ResponseBody
    @CrossOrigin
    public JSONObject list(@RequestBody JSONObject json)
    {
        String root = json.getString("root");
        String path = json.getString("path");

        JSONArray list = new JSONArray();

        File[] files = new File(Common.pathOf(root, path)).listFiles();
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
}