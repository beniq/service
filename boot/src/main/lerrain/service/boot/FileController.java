package lerrain.service.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import lerrain.service.boot.console.DbMgr;
import lerrain.tool.Common;
import lerrain.tool.Disk;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class FileController
{
	@Autowired
	ServiceMgr serviceMgr;

	@Autowired
	MachineMgr machineMgr;

	@RequestMapping("/filemgr/tree.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject tree(@RequestBody JSONObject json)
	{
		return fileMgr("tree.json", json);
	}

	@RequestMapping("/filemgr/list.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject list(@RequestBody JSONObject json)
	{
		return fileMgr("list.json", json);
	}

	@RequestMapping("/filemgr/history.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject history(@RequestBody JSONObject json)
	{
		return fileMgr("history.json", json);
	}

	@RequestMapping("/filemgr/delete.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject delete(@RequestBody JSONObject json)
	{
		return fileMgr("delete.json", json);
	}

	@RequestMapping("/filemgr/file.do")
	@ResponseBody
	@CrossOrigin
	public JSONObject file(@RequestParam("file") List<MultipartFile> files, @RequestParam("index") int index, @RequestParam("path") String path) throws Exception
	{
		Machine mach = machineMgr.getMachine(index);

		JSONArray list = new JSONArray();

		for (MultipartFile file : files)
		{
			File ff = new File("./tmp/" + file.getOriginalFilename());

			try (InputStream is = file.getInputStream())
			{
				Disk.saveToDisk(is, ff);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			System.out.println("uploading... " + ff);

			if (!Common.isEmpty(path))
			{
				String fullPath = Machine.pathOf(mach.getPath("temp"), path).replaceAll("\\\\", "/");
				mach.run(String.format("mkdir -p %s", fullPath));
			}

			try (InputStream is = new FileInputStream(ff))
			{
				String tempFile = mach.upload(is, file.getOriginalFilename(), file.getSize(), Machine.pathOf("temp", path));
				list.add(new String[] {tempFile, Machine.pathOf(path, file.getOriginalFilename())});
			}
		}

		JSONObject json = new JSONObject();
		json.put("index", index);
		json.put("files", list);

		return fileMgr("copy.json", json);
	}

	private JSONObject fileMgr(String action, JSONObject json)
	{
		Machine mach = machineMgr.getMachine(json.getIntValue("index"));
		json.put("root", mach.getRoot());

		/*
		json.put("root", "X:/boot/webapp");

		String res = Network.request("http://localhost:6001/" + action, json.toJSONString());
		return JSON.parseObject(res);
		*/

		ServicePack sp = serviceMgr.getService("filemgr");

		if (sp != null)
		{
			List<ServiceInstance> list = sp.getInstance();
			if (list != null)
			{
				for (ServiceInstance si : list)
				{
					if (si.getMachine().equals(mach))
					{
						String res = Network.request("http://" + mach.getHost() + ":" + si.getPort() + "/" + action, json.toJSONString(), 10000);
						return JSON.parseObject(res);
					}
				}
			}
		}

		throw new RuntimeException("no filemgr");
	}
}
