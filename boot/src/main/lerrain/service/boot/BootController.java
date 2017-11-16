package lerrain.service.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import lerrain.service.boot.console.DbMgr;
import lerrain.tool.Disk;
import lerrain.tool.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Controller
public class BootController
{
	DbMgr dbMgr;

	@Autowired
	ServiceMgr serviceMgr;

	@Autowired
	MachineMgr machineMgr;

	@RequestMapping("/reset")
	@ResponseBody
	@CrossOrigin
	public String reset()
	{
		serviceMgr.reset();
		return "success";
	}

	@RequestMapping("/list.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject list(@RequestBody JSONObject json)
	{
		List<ServicePack> list = serviceMgr.getServiceList();

		JSONObject r = new JSONObject();
		if (list != null)
		{
			JSONArray rl = new JSONArray();
			for (ServicePack sp : list)
			{
				JSONObject rs = new JSONObject();
				rs.put("id", sp.getId());
				rs.put("code", sp.getCode());
				rs.put("name", sp.getName());
				rs.put("group", sp.getGroup());
				rs.put("jar", sp.getCurrentJar());
				rl.add(rs);
			}
			r.put("list", rl);
			r.put("total", rl.size());
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/deploy.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject deploy(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		serviceMgr.deploy(instanceId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/restart.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject restart(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		serviceMgr.restart(instanceId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/stop.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject stop(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		serviceMgr.stop(instanceId);

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}

	@RequestMapping("/state.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject state(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		ServiceInstance sp = serviceMgr.getServiceInstance(instanceId);

		JSONObject r = new JSONObject();
		r.put("state", sp.state());
		r.put("deploy", sp.getDeployTime());
		r.put("restart", sp.getRestartTime());

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/reset.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject reset(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		ServiceInstance sp = serviceMgr.getServiceInstance(instanceId);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", sp.reset());

		return res;
	}

	@RequestMapping("/health.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject health(@RequestBody JSONObject json)
	{
		Long serviceId = json.getLong("serviceId");
		ServicePack sp = serviceMgr.getService(serviceId);

		int[] c = new int[4];
		int[] t = new int[4];
		for (ServiceInstance si : sp.getInstance())
		{
			if (si.getService().getCode().equals(si.state()) || "success".equals(si.state()))
				c[si.getEnv()]++;
			t[si.getEnv()]++;
		}

		JSONObject r = new JSONObject();
		r.put("test", new Object[] {c[ServicePack.TEST], t[ServicePack.TEST]});
		r.put("uat", new Object[] {c[ServicePack.UAT], t[ServicePack.UAT]});
		r.put("prd", new Object[] {c[ServicePack.PRD], t[ServicePack.PRD]});

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/log.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject log(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");
		int line = json.getInteger("line");
		ServiceInstance sp = serviceMgr.getServiceInstance(instanceId);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", sp.viewLog(line));

		return res;
	}

	@RequestMapping("/info.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject info(@RequestBody JSONObject json)
	{
		Long instanceId = json.getLong("instanceId");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", serviceMgr.viewReleaseInfo(instanceId));

		return res;
	}

	@RequestMapping("/view.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject view(@RequestBody JSONObject json)
	{
		JSONObject r = new JSONObject();

		JSONArray[] l = new JSONArray[4];
		l[ServicePack.TEST] = new JSONArray();
		l[ServicePack.UAT] = new JSONArray();
		l[ServicePack.PRD] = new JSONArray();

		JSONArray all = json.getJSONArray("serviceId");
		for (int i=0;i<all.size();i++)
		{
			Long srvId = all.getLong(i);
			ServicePack sp = serviceMgr.getService(srvId);

			for (ServiceInstance si : sp.getInstance())
			{
				JSONObject s = new JSONObject();
				s.put("serviceId", sp.getId());
				s.put("instanceId", si.getId());
				s.put("name", sp.getName());
				s.put("code", sp.getCode());
				s.put("machineId", si.getMacIndex());
				s.put("machineHost", machineMgr.getMachine(si.getMacIndex()).getHost());
				s.put("port", si.getPort());
				s.put("deploy", si.getDeployTime());
				s.put("restart", si.getRestartTime());

				l[si.getEnv()].add(s);
			}
		}

		r.put("test", l[ServicePack.TEST]);
		r.put("uat", l[ServicePack.UAT]);
		r.put("prd", l[ServicePack.PRD]);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

//	@RequestMapping("/refresh.json")
//	@ResponseBody
//	@CrossOrigin
//	public JSONObject refresh(@RequestBody JSONObject json)
//	{
//		JSONObject res = new JSONObject();
//		res.put("result", "success");
//		res.put("content", serviceMgr.refresh());
//
//		return res;
//	}

	@RequestMapping("/file.do")
	@ResponseBody
	@CrossOrigin
	public JSONObject file(@RequestParam("file") List<MultipartFile> files)
	{
		Map<Long, String> cc = new HashMap();

		for (MultipartFile file : files)
		{
			ServicePack cs = null;

			double rate = 0;
			for (ServicePack sp : serviceMgr.getServiceList())
			{
				double r = file.getOriginalFilename().indexOf(sp.getCode()) < 0 ? 0 : (double)sp.getCode().length() / file.getOriginalFilename().length();
				if (r > rate)
				{
					rate = r;
					cs = sp;
				}
			}

			if (cs != null)
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

				for (Machine mach : machineMgr.getAllMachines())
				{
					try (InputStream is = new FileInputStream(ff))
					{
						String srcFile = mach.upload(is, file.getOriginalFilename(), file.getSize(), "temp");
						cc.put(cs.getId(), file.getOriginalFilename());

						if (!srcFile.equals(cs.getCurrentJar()))
							serviceMgr.setCurrentJar(cs, srcFile);
					}
					catch (Exception e)
					{
						System.out.println("upload " + ff + " to " + mach.getHost() + " failed, cause by " + e.getMessage());
					}
				}
			}
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", cc);

		return res;
	}

	@RequestMapping("/presynch.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject presynch(@RequestBody JSONObject json)
	{
		JSONArray all = json.getJSONArray("serviceId");

		Long serviceId = all.getLong(0);
		ServicePack sp = serviceMgr.getService(serviceId);

		int step = json.getInteger("step");

		JSONObject res = new JSONObject();
		res.put("result", "fail");

		if (sp.getDbRule() != null)
		{
			int src = -1;
			int dst = -1;

			if (step == 1)
			{
				src = ServicePack.TEST;
				dst = ServicePack.UAT;
			}
			else if (step == 2)
			{
				src = ServicePack.UAT;
				dst = ServicePack.PRD;
			}
			else if (step == 91)
			{
				src = ServicePack.UAT;
				dst = ServicePack.TEST;
			}
			else if (step == 92)
			{
				src = ServicePack.PRD;
				dst = ServicePack.UAT;
			}

			if (src >= 0)
			{
				String dbName = sp.getDbRule().getString("db");
				JSONObject db1 = (JSONObject) serviceMgr.getParam(src, "db." + dbName);
				JSONObject db2 = (JSONObject) serviceMgr.getParam(dst, "db." + dbName);

				dbMgr = new DbMgr(sp.getDbRule(), db1, db2);

				JSONObject rr = new JSONObject();
				rr.put("key", dbMgr.getId());
				rr.put("list", dbMgr.pretreat());

				res.put("result", "success");
				res.put("content", rr);
			}
		}

		return res;
	}

	@RequestMapping("/synch.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject synch(@RequestBody JSONObject json)
	{
		System.out.println(json);

		String uuid = json.getString("key");
		if (dbMgr == null || !dbMgr.isInst(uuid))
			throw new RuntimeException("脚本对象不匹配，有其他人在同时操作");

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", dbMgr.run(json.getJSONArray("skip")));

		return res;
	}
}
