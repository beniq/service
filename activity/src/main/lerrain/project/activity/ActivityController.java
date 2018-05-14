package lerrain.project.activity;

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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
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
		res.put("content", DocUtil.toJson(doc));

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
		res.put("content", DocUtil.toJson(doc));

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
		res.put("content", DocUtil.toJson(doc));

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
		res.put("content", DocUtil.toJson(doc));

		return res;
	}

	@RequestMapping("/element.json")
	@ResponseBody
	public JSONObject element(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		int pageIndex = json.getIntValue("pageIndex");
		int index = json.getIntValue("index");

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(pageIndex);

		Element e = page.getList().get(index);
		JSONObject o = json.getJSONObject("element");

		e.setX(o.getFloat("x"));
		e.setY(o.getFloat("y"));
		e.setZ(o.getIntValue("z"));
		e.setW(o.getFloat("w"));
		e.setH(o.getFloat("h"));
		e.setFile(o.getString("image"));
		e.setBgColor(o.getString("bgcolor"));

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/file.do")
	@ResponseBody
	@CrossOrigin
	public JSONObject file(HttpServletRequest req, @RequestParam("file") List<MultipartFile> files, @RequestParam("type") String type, @RequestParam("pageIndex") int index, @RequestParam("actId") Long actId)
	{
		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(index);

		File dir = new File("./static/images/temp/");
		dir.mkdirs();

		for (MultipartFile file : files)
		{
			String fileName = file.getOriginalFilename().toLowerCase();
			if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"))
			{
				try (InputStream is = file.getInputStream())
				{
					File f = new File("./static/images/temp/" + file.getOriginalFilename());
					Disk.saveToDisk(is, f);

					BufferedImage bi = ImageIO.read(f);
					int w = bi.getWidth();
					int h = bi.getHeight();

					if (w > 750)
					{
						h = h * 750 / w;
						w = 750;
					}

					if ("desk".equalsIgnoreCase(type))
					{
						page.setBackground("images/temp/" + file.getOriginalFilename());
						page.setH(h);
					}
					else if ("element".equalsIgnoreCase(type))
					{
						Element img = new Element();
						img.setFile("images/temp/" + file.getOriginalFilename());

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
						img.setFile("images/temp/" + file.getOriginalFilename());
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
		res.put("content", DocUtil.toJson(doc));

		return res;
	}

	@RequestMapping("/depoly.json")
	@ResponseBody
	public JSONObject depoly(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);
		String env = json.getString("env");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", act.depoly(doc, env));

		return res;
	}
}
