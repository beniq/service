package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.export.JQueryExport;
import lerrain.project.activity.export.JQueryTemplate;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActivityService
{
	@Autowired
	ActivityDao actDao;

	Map<Long, ActivityDoc> map = new HashMap<>();

	long actIdSeq = -1;

	@PostConstruct
	public void reset()
	{
		JQueryTemplate.root = Disk.load(new File("./static/act/root.html"), "utf-8");
		JQueryTemplate.page = Disk.load(new File("./static/act/page.html"), "utf-8");
		JQueryTemplate.popupCss = Disk.load(new File("./static/act/popup.css"), "utf-8");
		JQueryTemplate.playCss = Disk.load(new File("./static/act/play.css"), "utf-8");
	}

	public String getDestFile(String dest)
	{
		long k = 0;
		for (int i=0;i<dest.length();i++)
			k += dest.charAt(i);

		return k + "";
	}

	public ActivityDoc getAct(Long actId)
	{
		ActivityDoc doc = map.get(actId);

		if (doc == null)
		{
			doc = actDao.load(actId);
			map.put(actId, doc);
		}

		return doc;
	}

	public ActivityDoc newDoc()
	{
		if (actIdSeq < 0)
			actIdSeq = actDao.maxId() + 1;

		ActivityDoc doc = new ActivityDoc();
		doc.setActId(actIdSeq++);

		map.put(doc.getActId(), doc);

		return doc;
	}

	public String deploy(ActivityDoc doc, String env)
	{
		String url = null;
		String html = new JQueryExport(env).export(doc);

		JSONObject req = new JSONObject();

		JSONArray files = new JSONArray();
		for (File f : new File("./static/images/temp/" + doc.getActId()).listFiles())
		{
			JSONObject file = new JSONObject();
			file.put("fileName", doc.getActId() + "/" + f.getName());
			file.put("content", Common.encodeBase64(Disk.load(f)));
			files.add(file);
		}

		req.put("key", Common.md5Of(Common.getString(new Date()) + "_ACT"));
		req.put("root", "x:/1/activity");
		req.put("files", files);

		if ("test".equals(env))
		{
			Network.request("http://localhost:7555/upload.json", req.toJSONString());
			url = "http://localhost:7555/" + doc.getActId() + "/main.html";
		}
		else if ("uat".equals(env))
		{

		}
		else if ("prd".equals(env))
		{

		}

		return url;
	}
}
