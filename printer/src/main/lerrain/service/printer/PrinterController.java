package lerrain.service.printer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.document.LexDocument;
import lerrain.tool.document.export.ObjectPainter;
import lerrain.tool.document.export.Painter;
import lerrain.tool.document.export.PdfPainterNDF;
import lerrain.tool.document.export.PngPainter;
import lerrain.tool.document.typeset.TypesetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@ControllerAdvice
public class PrinterController
{
	@Autowired
	PrinterService printer;

	@Autowired
	MailServer mailServer;

	@Value("${path.temp}")
	String tempPath;

	@Value("${url.temp}")
	String urlPrefix;

	@RequestMapping("/health")
	@ResponseBody
	@CrossOrigin
	public String health()
	{
		return "success";
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public JSONObject exc(Exception e)
	{
		JSONObject res = new JSONObject();
		res.put("result", "fail");
		res.put("reason", e.getMessage());

		return res;
	}

	@RequestMapping("/reset")
	@ResponseBody
	@CrossOrigin
	public String reset()
	{
		printer.reset();
		return "success";
	}

	@RequestMapping({"/print.stream", "/stream.json"})
	public void printStream(@RequestBody JSONObject p, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		String key = p.getString("key");
		String ip = getIpAddr(request);

		String outputType = p.getString("outputType");
		if (Common.isEmpty(outputType))
			outputType = "pdf";

		TypesetTemplate typesetTemplate = getTemplate(p);
		if (typesetTemplate == null)
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "no template");

		long t = System.currentTimeMillis();

		try (OutputStream os = response.getOutputStream())
		{
			LexDocument doc = printer.build(typesetTemplate, p.getJSONObject("content"));

			int t1 = (int)(System.currentTimeMillis() - t);

			if ("pdf".equalsIgnoreCase(outputType))
			{
				doc.export(printer.getPdfSignPainter(typesetTemplate), os, Painter.STREAM);
			}

			int t2 = (int)(System.currentTimeMillis() - t) - t1;

			printer.log(key, typesetTemplate.getId(), outputType, 2, 1, ip, t1, t2, doc.size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}

		System.out.println("stream in " + (System.currentTimeMillis() - t) + "ms");
	}

	@RequestMapping("/print.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject print(@RequestBody JSONObject p, HttpServletRequest request) throws Exception
	{
		String key = p.getString("key");
		String ip = getIpAddr(request);

		String outputType = p.getString("outputType");
		if (Common.isEmpty(outputType))
			outputType = "pdf";

		TypesetTemplate typesetTemplate = getTemplate(p);

		if (typesetTemplate == null)
			throw new RuntimeException("no template");

		JSONObject res = new JSONObject();
		res.put("result", "fail");

		long t = System.currentTimeMillis();

		try
		{
			LexDocument doc = printer.build(typesetTemplate, p.getJSONObject("content"));

			int t1 = (int)(System.currentTimeMillis() - t);

			if ("pdf".equalsIgnoreCase(outputType))
			{
				String fileName = Common.nextId() + ".pdf";
				String today = Common.getString(new Date());

				File dir = new File(Common.pathOf(tempPath, today));
				if (!dir.exists())
					dir.mkdirs();

				File file = new File(Common.pathOf(tempPath, today, fileName));
				try (FileOutputStream fos = new FileOutputStream(file))
				{
					doc.export(printer.getPdfSignPainter(typesetTemplate), fos, Painter.STREAM);
				}

				JSONObject mail = p.getJSONObject("email");
				if (mail != null && !mail.isEmpty())
				{
					List list = new ArrayList();
					list.add(new String[]{file.getAbsolutePath(), fileName});

					mailServer.send(mail.getString("address"), mail.getString("subject"), mail.getString("content"), list);
				}
				else
				{
					res.put("content", urlPrefix + "/" + today + "/" + fileName);
				}

				res.put("result", "success");
			}
			else if ("data".equalsIgnoreCase(outputType))
			{
				JSON data = new JSONArray();
				doc.export(new ObjectPainter(), data, Painter.OBJECT);

				res.put("result", "success");
				res.put("content", data);
			}
			else
			{
				throw new RuntimeException("outputType<" + outputType + "> not support");
			}

			int t2 = (int)(System.currentTimeMillis() - t) - t1;

			printer.log(key, typesetTemplate.getId(), outputType, 1, 1, ip, t1, t2, doc.size());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw e;
		}

		return res;
	}

	@RequestMapping("/test.do")
	public void test(@RequestParam("template") String templateCode, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		TypesetTemplate template = printer.getTypesetTemplate(templateCode);
		LexDocument doc = printer.build(template, JSON.parseObject(Disk.load(template.getTest(), "utf-8")));

		OutputStream os = response.getOutputStream();
		doc.export(new PdfPainterNDF(), os, Painter.STREAM);

		os.flush();
		os.close();
	}

	@RequestMapping("/verify.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject verify(HttpServletRequest request)
	{
		JSONObject r = new JSONObject();

		try (InputStream is = request.getInputStream())
		{
			r.put("result", "success");
			r.put("content", printer.verify(is));
		}
		catch (Exception e)
		{
			r.put("result", "fail");
			r.put("reason", e.getMessage());
		}

		return r;
	}

	@RequestMapping("/list.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject list(@RequestBody JSONObject json) throws Exception
	{
		int from = json.getIntValue("from");
		int number = json.getIntValue("number");

		List<TypesetTemplate> list = printer.list(from, number);

		JSONArray ja = new JSONArray();
		for (TypesetTemplate tt : list)
		{
			JSONObject j = new JSONObject();
			j.put("id", tt.getId());
			j.put("code", tt.getCode());
			j.put("name", tt.getName());
			ja.add(j);
		}

		JSONObject r = new JSONObject();
		r.put("list", ja);
		r.put("total", printer.list.size());

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/test.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject test(@RequestBody JSONObject p, HttpServletRequest request) throws Exception
	{
		TypesetTemplate template = getTemplate(p);
		LexDocument doc = printer.build(template, JSON.parseObject(Disk.load(template.getTest(), "utf-8")));

		String outputType = p.getString("outputType");
		if (Common.isEmpty(outputType))
			outputType = "pdf";

		JSONObject res = new JSONObject();
		res.put("result", "fail");

		if ("pdf".equalsIgnoreCase(outputType))
		{
			String fileName = Common.nextId() + ".pdf";
			File file = new File(Common.pathOf(tempPath, fileName));
			try (FileOutputStream fos = new FileOutputStream(file))
			{
				doc.export(printer.getPdfSignPainter(template), fos, Painter.STREAM);
			}

			JSONObject mail = p.getJSONObject("email");
			if (mail != null && !mail.isEmpty())
			{
				List list = new ArrayList();
				list.add(new String[]{file.getAbsolutePath(), fileName});

				mailServer.send(mail.getString("address"), mail.getString("subject"), mail.getString("content"), list);
			}
			else
			{
				res.put("content", urlPrefix + "/" + fileName);
			}

			res.put("result", "success");
		}
		else if ("png".equalsIgnoreCase(outputType))
		{
			String fileName = Common.nextId();
			File file = new File(Common.pathOf(tempPath, fileName));
			file.mkdirs();

			doc.export(new PngPainter(), file);

			JSONArray list = new JSONArray();
			for (int i=0;i<doc.size();i++)
				list.add((i+1)+".png");

			res.put("content", list);
			res.put("result", "success");
		}
		else if ("data".equalsIgnoreCase(outputType))
		{
			JSON data = new JSONArray();
			doc.export(new ObjectPainter(), data, Painter.OBJECT);

			res.put("result", "success");
			res.put("content", data);
		}

		return res;
	}

	@RequestMapping("/test.stream.json")
	@CrossOrigin
	@Deprecated
	public void testStream(@RequestBody JSONObject p, HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		TypesetTemplate template = getTemplate(p);
		LexDocument doc = printer.build(template, JSON.parseObject(Disk.load(template.getTest(), "utf-8")));

		String outputType = p.getString("outputType");
		if (Common.isEmpty(outputType))
			outputType = "pdf";

		if ("pdf".equalsIgnoreCase(outputType))
		{
			String fileName = Common.nextId() + ".pdf";
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);

			try (OutputStream os = response.getOutputStream())
			{
				doc.export(printer.getPdfSignPainter(template), os, Painter.STREAM);
			}
		}
	}

	@RequestMapping("/view.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject view(@RequestBody JSONObject json) throws Exception
	{
		TypesetTemplate template = getTemplate(json);

		JSONArray fs = new JSONArray();
		if (!Common.isEmpty(template.getWorkDir()))
		{
			File dir = new File(Common.pathOf(TypesetUtil.getResourcePath(), template.getWorkDir()));
			File[] files = dir.listFiles();
			if (files != null) for (File f : files)
			{
				fs.add(f.getName());
			}
		}

		JSONObject r = new JSONObject();
		r.put("id", template.getId());
		r.put("code", template.getCode());
		r.put("name", template.getName());
		r.put("workDir", template.getWorkDir());
		r.put("files", fs);
		r.put("example", Disk.load(template.getTest(), "utf-8"));

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	private TypesetTemplate getTemplate(JSONObject p)
	{
		Long templateId = p.getLong("templateId");
		if (templateId != null)
			return printer.getTypesetTemplate(templateId);

		String templateCode = p.getString("templateCode");
		if (templateCode != null)
			return printer.getTypesetTemplate(templateCode);

		return null;
	}

	private String getIpAddr(HttpServletRequest request)
	{
		String ip = request.getHeader("x-forwarded-for");
		if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			int index = ip.indexOf(',');
			if (index != -1)
			{
				return ip.substring(0, index);
			}
			else
			{
				return ip;
			}
		}
		ip = request.getHeader("x-real-ip");
		if (ip != null && !"".equals(ip) && !"unknown".equalsIgnoreCase(ip))
		{
			return ip;
		}
		else
		{
			return request.getRemoteAddr();
		}
	}

	@RequestMapping("/{templateId}/upload")
	@ResponseBody
	@CrossOrigin
	public JSONObject file(@PathVariable Long templateId, @RequestParam("file") List<MultipartFile> files)
	{
		TypesetTemplate tt = printer.getTypesetTemplate(templateId);

		for (MultipartFile file : files)
		{
			File ff = new File(Common.pathOf(TypesetUtil.getResourcePath(), Common.pathOf(tt.getWorkDir(), file.getOriginalFilename())));

			try (InputStream is = file.getInputStream())
			{
				Disk.saveToDisk(is, ff);

				if (ff.getName().toLowerCase().endsWith(".xml"))
				{
					tt.setTemplateFile(ff.getName());
				}

				if (ff.getName().toLowerCase().equals("test.json"))
				{
					tt.setTestFile(ff.getName());
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		printer.save(tt);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/export.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject export(@RequestBody JSONObject json) throws Exception
	{
		TypesetTemplate template = getTemplate(json);

		JSONObject fs = new JSONObject();
		File dir = new File(Common.pathOf(TypesetUtil.getResourcePath(), template.getWorkDir()));
		for (File f : dir.listFiles())
		{
			byte[] fb = Disk.load(f);
			fs.put(f.getName(), Common.encodeBase64(fb));
		}

		JSONObject m = new JSONObject();
		m.put("templateId", template.getId());
		m.put("code", template.getCode());
		m.put("name", template.getName());
		m.put("templateFile", template.getTemplateFile());
		m.put("workDir", template.getWorkDir());
		m.put("testFile", template.getTestFile());
		m.put("signId", template.getSignId());
		m.put("files", fs);

		JSONObject res = new JSONObject();
		res.put("content", m);
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/delete.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject delete(@RequestBody JSONObject json) throws Exception
	{
		TypesetTemplate template = getTemplate(json);

		String file = json.getString("file");
		if (Common.isEmpty(file))
			throw new RuntimeException("no file");

		File abbrFile = new File(Common.pathOf(TypesetUtil.getResourcePath(), Common.pathOf(template.getWorkDir(), file)));
		if (!abbrFile.delete())
			throw new RuntimeException("fail");

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/create.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject create(@RequestBody JSONObject json) throws Exception
	{
		String code = json.getString("code");
		if (Common.isEmpty(code))
			code = null;

		if (printer.exists(code))
			throw new RuntimeException("code " + code + " exists");

		String name = json.getString("name");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", printer.create(code, name));

		return res;
	}

	@RequestMapping("/save.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject save(@RequestBody JSONObject json) throws Exception
	{
		Long templateId = json.getLong("templateId");

		String code = json.getString("code");
		if (Common.isEmpty(code))
			code = null;

		TypesetTemplate template = printer.getTypesetTemplate(templateId);
		if (template == null)
		{
			template = new TypesetTemplate();
			template.setId(templateId);
		}

		if ((code == null && template.getCode() != null) || !code.equals(template.getCode()))
		{
			if (printer.exists(code))
				throw new RuntimeException("code " + code + " exists");

			printer.resetCode(template, code);
		}

		if (json.containsKey("name"))
			template.setName(json.getString("name"));
		if (json.containsKey("templateFile"))
			template.setTemplateFile(json.getString("templateFile"));
		if (json.containsKey("workDir"))
			template.setWorkDir(json.getString("workDir"));
		if (json.containsKey("signId"))
			template.setSignId(json.getLong("signId"));

		String testStr = json.getString("test");
		if (!Common.isEmpty(testStr))
		{
			File testFile = new File(Common.pathOf(TypesetUtil.getResourcePath(), Common.pathOf(template.getWorkDir(), "test.json")));
			try (FileOutputStream fos = new FileOutputStream(testFile))
			{
				fos.write(testStr.getBytes("utf-8"));
				template.setTestFile("test.json");
			}
			catch (Exception e)
			{
				template.setTestFile(null);
				e.printStackTrace();
			}
		}
		else
		{
			template.setTestFile(json.getString("testFile"));
		}

		if (json.containsKey("files"))
		{
			if (Common.isEmpty(template.getWorkDir()))
				throw new RuntimeException("work dir is null");

			File dir = new File(Common.pathOf(TypesetUtil.getResourcePath(), template.getWorkDir()));
			dir.mkdirs();

			JSONObject fs = json.getJSONObject("files");
			for (String fileName : fs.keySet())
			{
				byte[] b = Common.decodeBase64ToByte(fs.getString(fileName));
				try (FileOutputStream fos = new FileOutputStream(new File(Common.pathOf(TypesetUtil.getResourcePath(), template.getWorkDir(), fileName))))
				{
					fos.write(b);
				}
			}
		}

		printer.save(template);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

}
