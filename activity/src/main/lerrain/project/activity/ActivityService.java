package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.export.JQueryExport;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

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
		JQueryExport.rootHtml = Disk.load(new File("./static/template/root.htmlx"), "utf-8");
		JQueryExport.pageHtml = Disk.load(new File("./static/template/page.htmlx"), "utf-8");
		JQueryExport.popupCss = Disk.load(new File("./static/template/popup.cssx"), "utf-8");
		JQueryExport.playCss = Disk.load(new File("./static/template/play.cssx"), "utf-8");
		JQueryExport.starCss = Disk.load(new File("./static/template/star.cssx"), "utf-8");
		JQueryExport.textCss = Disk.load(new File("./static/template/text.cssx"), "utf-8");
	}

	public List<ActivityDoc> list(int from, int num)
	{
		List<Long> list = actDao.list(from, num);
		List<ActivityDoc> r = new ArrayList<>();
		for (Long id : list)
			r.add(getAct(id));
		return r;
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
		if (map.containsKey(actId))
			return map.get(actId);

		ActivityDoc doc = null;

		try
		{
			doc = actDao.load(actId);
			map.put(actId, doc);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			map.put(actId, null);
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
		String html = new JQueryExport(doc, env).export();

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
