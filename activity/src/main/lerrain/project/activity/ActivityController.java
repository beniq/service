package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;
import lerrain.project.activity.export.JQueryExport;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

@Controller
public class ActivityController
{
	@Autowired
	ActivityService act;

	@Autowired
	WriteQueue queue;

	@RequestMapping("/view_act.json")
	@ResponseBody
	public JSONObject viewAct(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/new_act.json")
	@ResponseBody
	public JSONObject newAct(@RequestBody JSONObject json)
	{
		ActivityDoc doc = act.newDoc();

		Page page = new Page();
		doc.getList().add(page);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/new_page.json")
	@ResponseBody
	public JSONObject newPage(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);

		Page page = new Page();
		doc.getList().add(page);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/new_element.json")
	@ResponseBody
	public JSONObject newElement(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		int index = json.getInteger("pageIndex");

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(index);

		String file = json.getString("file");

		Element img = new Element();
		img.setFile(file);

		page.addElement(img);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/del_element.json")
	@ResponseBody
	public JSONObject delElement(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		int pageIndex = json.getIntValue("pageIndex");
		int index = Common.intOf(json.getInteger("index"), -1);

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(pageIndex);
		page.getList().remove(index);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/element.json")
	@ResponseBody
	public JSONObject element(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		int pageIndex = json.getIntValue("pageIndex");
		JSONObject o = json.getJSONObject("element");
		int index = Common.intOf(o.getInteger("index"), -1);

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(pageIndex);

		Element e;
		if (index < 0)
		{
			e = new Element();
			page.getList().add(e);
		}
		else
		{
			e = page.getList().get(index);
		}

		e.setX(o.getFloat("x"));
		e.setY(o.getFloat("y"));
		e.setZ(Common.intOf(o.getInteger("z"), 0));
		e.setW(o.getFloat("w"));
		e.setH(o.getFloat("h"));

		String action = o.getString("action");
		e.setAction(Common.isEmpty(action) ? null : o.getString("action"));

		String param = o.getString("param");
		e.setActionParam(Common.isEmpty(param) ? null : o.getString("param"));

		String image = o.getString("image");
		e.setFile(Common.isEmpty(image) ? null : image);

		String bgColor = o.getString("bgColor");
		e.setBgColor(Common.isEmpty(bgColor) ? null : bgColor);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/file.do")
	@ResponseBody
	@CrossOrigin
	public JSONObject file(HttpServletRequest req, @RequestParam("file") List<MultipartFile> files, @RequestParam("type") String type, @RequestParam("pageIndex") int index, @RequestParam("actId") Long actId)
	{
		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(index);

		String root = "./static";

		File dir = new File(Common.pathOf(root, "/act/" + actId));
		dir.mkdirs();
		dir = new File(Common.pathOf(root, "/temp/" + actId));
		dir.mkdirs();

		for (MultipartFile file : files)
		{
			String fileName = file.getOriginalFilename().toLowerCase();
			if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"))
			{
				try (InputStream is = file.getInputStream())
				{
					File src = new File(Common.pathOf(dir.getPath(), fileName));
					Disk.saveToDisk(is, src);

					File dst = ImageTool.compress(src, root, "act/" + actId + "/" + act.nextFileId());
					String uriName = "act/" + actId + "/" + dst.getName();

					BufferedImage bi = ImageIO.read(dst);
					int w = bi.getWidth();
					int h = bi.getHeight();

					if (w > 750)
					{
						h = h * 750 / w;
						w = 750;
					}

					if ("desk".equalsIgnoreCase(type))
					{
						page.setBackground(uriName);
						page.setH(h);
					}
					else if ("element".equalsIgnoreCase(type))
					{
						Element img = new Element();
						img.setFile(uriName);

						if (h > 1200)
						{
							w = w * 1200 / h;
							h = 1200;
						}

						img.setX(750 / 2 - w / 2);
						img.setY(1200 / 2 - h / 2);
						img.setW(w);
						img.setH(h);

						page.addElement(img);
					}
					else if ("image".equalsIgnoreCase(type))
					{
						int eInx = Common.intOf(req.getParameter("elementIndex"), -1);
						Element img = page.getList().get(eInx);
						img.setFile(uriName);
						img.setH(h * img.getW() / w);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/deploy.json")
	@ResponseBody
	public JSONObject deploy(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);
		String env = json.getString("env");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", act.deploy(doc, env));

		return res;
	}

	@RequestMapping("/act/{actId}/test.html")
	public void test(HttpServletRequest req, HttpServletResponse res, @PathVariable Long actId) throws Exception
	{
		ActivityDoc doc = act.getAct(actId);
		String env = "test";

		String html = new JQueryExport().export(doc);
		try (OutputStream os = res.getOutputStream())
		{
			os.write(html.getBytes("utf-8"));
		}
	}


	@RequestMapping("/upload.json")
	@ResponseBody
	public JSONObject upload(@RequestBody JSONObject req)
	{
		String key = req.getString("password");
		if (!Common.md5Of(Common.getString(new Date()) + "_ACT").equals(key))
			throw new RuntimeException("error");

		String root = req.getString("root");
		JSONArray list = req.getJSONArray("files");

		for (int i=0;i<list.size();i++)
		{
			JSONObject f = list.getJSONObject(i);
			String fileName = f.getString("fileName");
			String content = f.getString("content");

			try
			{
				File file = new File(Common.pathOf(root, fileName));
				file.getParentFile().mkdirs();

				byte[] b = Common.decodeBase64ToByte(content);
				Disk.saveToDisk(new ByteArrayInputStream(b), file);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}
}
