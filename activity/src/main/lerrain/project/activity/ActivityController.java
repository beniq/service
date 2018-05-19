package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Event;
import lerrain.project.activity.base.Page;
import lerrain.project.activity.export.JQueryExport;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.List;

@Controller
public class ActivityController
{
	@Autowired
	ActivityService act;

	@Autowired
	WriteQueue queue;

	@PostConstruct
	@RequestMapping({"/reset"})
	@ResponseBody
	public String reset()
	{
		act.reset();
		return "success";
	}

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

	@RequestMapping("/save_act.json")
	@ResponseBody
	public JSONObject saveAct(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);

		doc.setCode(json.getString("code"));
		doc.setName(json.getString("name"));

		queue.add(doc);

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

	@RequestMapping("/save_page.json")
	@ResponseBody
	public JSONObject savePage(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);

		int pageIndex = json.getIntValue("pageIndex");
		Page page = doc.getList().get(pageIndex);
		page.setW(json.getIntValue("w"));
		page.setH(json.getIntValue("h"));

		queue.add(doc);

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

	@RequestMapping("/del_event.json")
	@ResponseBody
	public JSONObject delEvent(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String elementId = json.getString("elementId");
		String eventId = json.getString("eventId");

		ActivityDoc doc = act.getAct(actId);
		Element element = doc.find(elementId);

		Event ev = element.findEvent(eventId);
		element.getEvents().remove(ev);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/link_event.json")
	@ResponseBody
	public JSONObject link(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String fromEventId = json.getString("fromEventId");
		String invokeEventId = json.getString("invokeEventId");

		ActivityDoc doc = act.getAct(actId);
		Event fromEvent = doc.findEvent(fromEventId);
		Event invokeEvent = doc.findEvent(invokeEventId);

		fromEvent.setFinish(invokeEvent);

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
		String elementId = json.getString("elementId");

		ActivityDoc doc = act.getAct(actId);
		for (Page page : doc.getList())
			page.remove(elementId);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/event.json")
	@ResponseBody
	public JSONObject event(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String elementId = json.getString("elementId");

		ActivityDoc doc = act.getAct(actId);
		Element element = doc.find(elementId);

		Event event = new Event();
		event.setType(json.getString("type"));
		element.addEvent(event);

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", DocTool.toJson(doc));

		return res;
	}

	@RequestMapping("/save_event.json")
	@ResponseBody
	public JSONObject saveEvent(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		JSONObject ej = json.getJSONObject("event");
		String eventId = ej.getString("id");

		ActivityDoc doc = act.getAct(actId);
		Event event = doc.findEvent(eventId);

		event.setType(ej.getString("type"));
		event.setParam(ej.getJSONObject("param"));
		event.setFinish(ej.getJSONObject("onFinish"));

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
		String elementId = o.getString("id");
		String parentId = o.getString("parentId");

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(pageIndex);

		Element e;
		if (elementId == null)
		{
			e = new Element();
			if (parentId == null)
			{
				page.getList().add(e);
			}
			else
			{
				Element parent = page.find(parentId);
				if (parent != null)
					parent.getChildren().add(e);
			}
		}
		else
		{
			e = page.find(elementId);
		}

		e.setX(o.getFloat("x"));
		e.setY(o.getFloat("y"));
		e.setZ(Common.intOf(o.getInteger("z"), 0));
		e.setW(o.getFloat("w"));
		e.setH(o.getFloat("h"));
		e.setDisplay(Common.intOf(o.getInteger("display"), 1));

		e.setAction(o.getJSONArray("action"));

		if (o.containsKey("image"))
		{
			e.getFile().clear();
			JSONArray ja = o.getJSONArray("image");
			for (int k=0;k<ja.size();k++)
				e.getFile().add(ja.getString(k));
		}

		String bgColor = o.getString("bgColor");
		e.setBgColor(Common.isEmpty(bgColor) ? null : bgColor);

		String fontSize = o.getString("fontSize");
		e.setFontSize(Common.isEmpty(fontSize) ? null : fontSize);

		String text = o.getString("text");
		e.setText(Common.isEmpty(text) ? null : text);

		String color = o.getString("color");
		e.setColor(Common.isEmpty(color) ? null : color);

		if (o.containsKey("style"))
			e.setStyle(o.getJSONObject("style"));

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

		Element img = null;
		Element parent = null;

		if ("image".equalsIgnoreCase(type))
		{
			img = page.find(req.getParameter("elementId"));
			img.getFile().clear();
		}

		if ("element".equalsIgnoreCase(type))
		{
			img = new Element();

			String parentId = req.getParameter("parentId");
			if (parentId == null)
			{
				page.addElement(img);
			}
			else
			{
				parent = page.find(parentId);
				parent.getChildren().add(img);
			}
		}

		for (MultipartFile file : files)
		{
			String fileName = file.getOriginalFilename().toLowerCase();
			if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"))
			{
				try (InputStream is = file.getInputStream())
				{
					File src = new File(Common.pathOf(dir.getPath(), fileName));
					Disk.saveToDisk(is, src);

					File dst = ImageTool.compress(src, root, "act/" + actId + "/" + act.getDestFile(file.getOriginalFilename()));
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
						img.addFile(uriName);

						if (parent != null)
						{
							w = bi.getWidth();
							h = bi.getHeight();

							if (w > parent.getW())
							{
								img.setW(h * parent.getW() / w);
								img.setH(parent.getW());
							}
							else
							{
								img.setW(w);
								img.setH(h);
							}

							img.setX(0);
							img.setY(0);
						}
						else
						{
							if (h > 1200)
							{
								w = w * 1200 / h;
								h = 1200;
							}

							img.setX(750 / 2 - w / 2);
							img.setY(1200 / 2 - h / 2);
							img.setW(w);
							img.setH(h);
						}
					}
					else if ("image".equalsIgnoreCase(type))
					{
						img.addFile(uriName);
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
	public JSONObject deploy(@RequestBody JSONObject json) throws Exception
	{
		Long actId = json.getLong("actId");
		ActivityDoc doc = act.getAct(actId);
		String env = json.getString("env");

		File dest = new File(Common.pathOf("./static/act", actId.toString(), env + ".html"));

		String html = new JQueryExport(env).export(doc);
		try (OutputStream os = new FileOutputStream(dest))
		{
			os.write(html.getBytes("utf-8"));
		}

		String append = "prd".equalsIgnoreCase(env) ? "" : "-" + env;

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", "https://gpo" + append + ".iyunbao.com/act/" + actId + "/" + env + ".html");

		return res;
	}

	@RequestMapping("/act/{actId}/{env}.html")
	public void test(HttpServletRequest req, HttpServletResponse res, @PathVariable Long actId, @PathVariable String env) throws Exception
	{
		ActivityDoc doc = act.getAct(actId);

		String html = new JQueryExport(env).export(doc);
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
