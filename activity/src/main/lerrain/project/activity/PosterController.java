package lerrain.project.activity;

import com.alibaba.fastjson.JSON;
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
import java.util.UUID;

@Controller
public class PosterController
{
	String root = "./static";

	@Autowired
	ActivityService act;

	@RequestMapping("/list_poster.json")
	@ResponseBody
	public JSONObject listPoster(@RequestBody JSONObject json)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", act.listPoster(0, 20));

		return res;
	}

	@RequestMapping("/save_poster.json")
	@ResponseBody
	public JSONObject savePoster(@RequestBody JSONObject json)
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");

		if (json.containsKey("id"))
			res.put("content", act.savePoster(json));

		return res;
	}

	@RequestMapping("/poster.do")
	@ResponseBody
	@CrossOrigin
	public JSONObject poster(HttpServletRequest req, @RequestParam("file") List<MultipartFile> files, @RequestParam("type") String type)
	{
		File dir = new File(Common.pathOf(root, "temp/poster"));
		dir.mkdirs();

		dir = new File(Common.pathOf(root, "poster"));
		dir.mkdirs();

		Object r = null;

		for (MultipartFile file : files)
		{
			String fileName = file.getOriginalFilename().toLowerCase();
			if (fileName.endsWith(".png") || fileName.endsWith(".jpg") || fileName.endsWith(".gif"))
			{
				try (InputStream is = file.getInputStream())
				{
					String srcFile = "/temp/poster/" + fileName;
					File src = new File(Common.pathOf(root, srcFile));
					Disk.saveToDisk(is, src);

					File dst = ImageTool.copy(src, root, "poster/" + UUID.randomUUID().toString());
					String uriName = "poster/" + dst.getName();

					if ("poster".equalsIgnoreCase(type))
					{
						JSONObject n = new JSONObject();
						n.put("imgUrl", uriName);

						r = act.savePoster(n);
						break;
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/poster.jpg")
	public void poster(HttpServletRequest req, HttpServletResponse res) throws Exception
	{
		JSONObject param = JSON.parseObject(java.net.URLDecoder.decode(req.getParameter("param"), "utf-8"));
		String imgUrl = param.getString("imgUrl");
		try (OutputStream os = res.getOutputStream())
		{
			ImageTool.draw(new File(Common.pathOf(root, imgUrl)), param, os);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
