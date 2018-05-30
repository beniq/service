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
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Controller
public class ActivityController
{
	String root = "./static";

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

	@RequestMapping("/adjust_element.json")
	@ResponseBody
	public JSONObject adjust(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String elementId = json.getString("elementId");
		String parentId = json.getString("parentId");
		int page = Common.intOf(json.get("page"), -1);

		ActivityDoc doc = act.getAct(actId);
		Element element = doc.find(elementId);

		if (parentId != null)
		{
			Element parent = doc.find(parentId);
			element.getPage().remove(elementId);
			parent.addElement(element);
		}
		else if (page >= 0)
		{
			element.getPage().remove(elementId);
			doc.getList().get(page).addElement(element);
		}

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

	@RequestMapping("/cut_bg.json")
	@ResponseBody
	public JSONObject cutBg(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String elementId = json.getString("elementId");

		ActivityDoc doc = act.getAct(actId);
		Element element = doc.find(elementId);

		float x = element.getX(), y = element.getY();
		float dw, dh;

		String file = null;
		Element e = element.getParent();
		while (e != null)
		{
			if (file == null && e.getFile() != null && e.getFile().size() > 0)
				file = e.getFile().get(0);

			x += e.getX();
			y += e.getY();

			e = e.getParent();
		}

		if (file == null)
			file = element.getPage().getBackground();

		if (file != null)
		{
			String oriFile = (String)doc.getFiles().get(file);
			if (oriFile != null)
				file = oriFile;

			if (e != null)
			{
				dw = e.getW();
				dh = e.getH();
			}
			else
			{
				dw = element.getPage().getW();
				dh = element.getPage().getH();
			}

			String newFile = "/act/" + actId + "/" + elementId + ".jpg";
			File src = new File(Common.pathOf(root, file));
			File dst = new File(Common.pathOf(root, newFile));
			ImageTool.cut(dst, src, dw, dh, x, y, element.getW(), element.getH());

			element.setFile(newFile);
		}

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

	@RequestMapping("/new_element.json")
	@ResponseBody
	public JSONObject newElement(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		int index = json.getInteger("pageIndex");

		JSONObject o = json.getJSONObject("element");
		String parentId = o.getString("parentId");

		ActivityDoc doc = act.getAct(actId);
		Page page = doc.getList().get(index);

		Element e = new Element();
		if (parentId == null)
		{
			page.addElement(e);
		}
		else
		{
			Element parent = page.find(parentId);
			if (parent != null)
				parent.addElement(e);
		}

		fillElement(e, o);

//		String file = json.getString("file");
//		if (file != null)
//			e.setFile(file);

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
		ActivityDoc doc = act.getAct(actId);

		JSONObject elements = json.getJSONObject("elements");
		if (elements != null && elements.size() > 0)
		{
			for (String elementId : elements.keySet())
			{
				Element e = doc.find(elementId);
				fillElement(e, elements.getJSONObject(elementId));
			}
		}

		JSONObject element = json.getJSONObject("element");
		if (element != null)
		{
			String elementId = element.getString("id");
			Element e = doc.find(elementId);
			fillElement(e, element);
		}

		queue.add(doc);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	private void fillElement(Element e, JSONObject o)
	{
		e.setX(o.getFloat("x"));
		e.setY(o.getFloat("y"));
		e.setZ(Common.intOf(o.getInteger("z"), 0));
		e.setW(o.getFloat("w"));
		e.setH(o.getFloat("h"));
		e.setYs(Common.intOf(o.get("ys"), 0));
		e.setHs(Common.intOf(o.get("hs"), 0));

		e.setDisplay(Common.intOf(o.getInteger("display"), 1));
		e.setInput(o.getString("input"));
		e.setInputVerify(o.getJSONObject("inputVerify"));
		e.setAction(o.getJSONArray("action"));
		e.setVisible(o.getString("visible"));
		e.setName(o.getString("name"));
		e.setList(o.getString("list"));
		e.setVideo(o.getString("video"));

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
		e.setLineHeight(o.getString("lineHeight"));
		e.setAlign(Common.intOf(o.get("align"), 5));

		String text = o.getString("text");
		e.setText(Common.isEmpty(text) ? null : text);

		String color = o.getString("color");
		e.setColor(Common.isEmpty(color) ? null : color);

		if (o.containsKey("style"))
			e.setStyle(o.getJSONObject("style"));
	}

	@RequestMapping("/style.json")
	@ResponseBody
	public JSONObject style(@RequestBody JSONObject json)
	{
		Long actId = json.getLong("actId");
		String elementId = json.getString("elementId");
		JSONObject style = json.getJSONObject("style");

		ActivityDoc doc = act.getAct(actId);
		Element e = doc.find(elementId);
		e.setStyle(style);

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
				parent.addElement(img);
			}
		}

		for (MultipartFile file : files)
		{
			String fileName = file.getOriginalFilename().toLowerCase();
			if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"))
			{
				try (InputStream is = file.getInputStream())
				{
					String srcFile = "/temp/" + actId + "/" + fileName;
					File src = new File(Common.pathOf(root, srcFile));
					Disk.saveToDisk(is, src);

					File dst = ImageTool.compress(src, root, "act/" + actId + "/" + act.getDestFile(file.getOriginalFilename()));
					String uriName = "act/" + actId + "/" + dst.getName();

					doc.getFiles().put(uriName, srcFile);

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

							img.setX(Common.intOf(req.getParameter("x"), (int)parent.getW() / 2));
							img.setY(Common.intOf(req.getParameter("y"), (int)parent.getH() / 2));
						}
						else
						{
							if (h > 1200)
							{
								w = w * 1200 / h;
								h = 1200;
							}

							img.setX(Common.intOf(req.getParameter("x"), 750 / 2 - w / 2));
							img.setY(Common.intOf(req.getParameter("y"), 1200 / 2 - h / 2));
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

		File dest = new File(Common.pathOf("./static/" + env, actId.toString(), "main.html"));

		File parent = dest.getParentFile();
		parent.mkdirs();

		File dir = new File(Common.pathOf("./static/act", actId.toString()));
		for (File f : dir.listFiles())
			Disk.copy(f, new File(Common.pathOf("./static/" + env, actId.toString(), f.getName())), true);

		String html = new JQueryExport(doc, env).export();
		try (OutputStream os = new FileOutputStream(dest))
		{
			os.write(html.getBytes("utf-8"));
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", "https://sact.iyunbao.com/" + env + "/" + actId + "/main.html");

		return res;
	}

	@RequestMapping("/act/{actId}/{env}.html")
	public void test(HttpServletRequest req, HttpServletResponse res, @PathVariable Long actId, @PathVariable String env) throws Exception
	{
		ActivityDoc doc = act.getAct(actId);

		String html = new JQueryExport(doc, env).export();
		try (OutputStream os = res.getOutputStream())
		{
			os.write(html.getBytes("utf-8"));
		}
	}

//	@RequestMapping("/upload.json")
//	@ResponseBody
//	public JSONObject upload(@RequestBody JSONObject req)
//	{
//		String key = req.getString("password");
//		if (!Common.md5Of(Common.getString(new Date()) + "_ACT").equals(key))
//			throw new RuntimeException("error");
//
//		String root = req.getString("root");
//		JSONArray list = req.getJSONArray("files");
//
//		for (int i=0;i<list.size();i++)
//		{
//			JSONObject f = list.getJSONObject(i);
//			String fileName = f.getString("fileName");
//			String content = f.getString("content");
//
//			try
//			{
//				File file = new File(Common.pathOf(root, fileName));
//				file.getParentFile().mkdirs();
//
//				byte[] b = Common.decodeBase64ToByte(content);
//				Disk.saveToDisk(new ByteArrayInputStream(b), file);
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
//
//		JSONObject res = new JSONObject();
//		res.put("result", "success");
//
//		return res;
//	}
}
