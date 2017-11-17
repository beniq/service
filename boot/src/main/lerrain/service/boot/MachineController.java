package lerrain.service.boot;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import lerrain.service.boot.console.DbMgr;
import lerrain.tool.Disk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MachineController
{
	@Autowired
	MachineMgr machineMgr;

	@RequestMapping("/machine/list.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject list(@RequestBody JSONObject json)
	{
		Collection<Machine> coll = machineMgr.getAllMachines();

		JSONArray rl = new JSONArray();
		for (Machine sp : coll)
		{
			JSONObject rs = new JSONObject();
			rs.put("index", sp.getIndex());
			rs.put("name", sp.getName());
			rs.put("host", sp.getHost());
			rl.add(rs);
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", rl);

		return res;
	}

	@RequestMapping("/machine/process.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject listProcess(@RequestBody JSONObject json) throws Exception
	{
		String cmd = "ps -C java -o 'pid,comm,pcpu,pmem,rsz,args' | grep \"\"";
		String str = machineMgr.getMachine(json.getIntValue("index")).run(cmd);

		JSONArray ja = new JSONArray();
		String[] strcc = str.split("\n");
		for (int m = 2; m < strcc.length; m++)
		{
			String cc = strcc[m];
			cc = cc.trim();
			for (int i=0;i<30;i++)
				cc = cc.replaceAll("  ", " ");

			String[] s = new String[6];

			int len = cc.length();
			for (int i=0,j=0,k=0;i<len;i++)
			{
				char c = cc.charAt(i);
				if (c == ' ')
				{
					if (k == 5)
						i = len;
					s[k] = cc.substring(j, i);
					j = i + 1;
					k++;
				}
			}

			JSONObject ll = new JSONObject();
			ll.put("pid", s[0]);
			ll.put("cpu", s[2]);
			ll.put("mem", s[3]);
			ll.put("memkb", s[4]);
			ll.put("cmd", s[5]);
			ja.add(ll);
		}

		JSONObject r = new JSONObject();
		r.put("list", ja);
		r.put("total", ja.size());

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", r);

		return res;
	}

	@RequestMapping("/machine/disk_space.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject diskSpace(@RequestBody JSONObject json) throws Exception
	{
		Machine machine = machineMgr.getMachine(json.getIntValue("index"));
		String cmd = String.format("df %s -h", machine.getRoot());

		String str = machine.run(cmd);

		JSONObject ja = new JSONObject();
		String[] strcc = str.split("\n");
		int sizeIdx = 0;	// 总空间
		int usedIdx = 0;	// 已使用
		int availIdx = 0;	// 剩余
		int proIdx = 0;		// 占比
		for (int m = 1; m < strcc.length; m++)
		{
			String cc = strcc[m];
			cc = cc.trim();
			for (int i=0;i<30;i++)
				cc = cc.replaceAll("  ", " ");

			if(m == 1){
				String[] c = cc.split(" ");
				for (int i=0;i<c.length;i++)
				{
					if("size".equalsIgnoreCase(c[i])){
						sizeIdx = i;
					}else if("used".equalsIgnoreCase(c[i])){
						usedIdx = i;
					}else if("avail".equalsIgnoreCase(c[i])){
						availIdx = i;
					}else if("use%".equalsIgnoreCase(c[i])){
						proIdx = i;
					}
				}
				continue;
			}

			String[] c = cc.split(" ");
			ja.put("total", c[sizeIdx]);
			ja.put("used", c[usedIdx]);
			ja.put("avail", c[availIdx]);
			ja.put("proportion", c[proIdx]);
			break;
		}

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", ja);

		return res;
	}

	@RequestMapping("/machine/run.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject run(@RequestBody JSONObject json) throws Exception
	{
		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", machineMgr.runCommand(json.getIntValue("index"), json.getLong("commandId")));

		return res;
	}

	@RequestMapping("/machine/kill_process.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject killProcess(@RequestBody JSONObject json) throws Exception
	{
		String cmd = "kill -9 " + json.getString("pid");
		String str = machineMgr.getMachine(json.getIntValue("index")).run(cmd);

		JSONObject res = new JSONObject();
		res.put("result", "success");
		res.put("content", str);

		return res;
	}

	@RequestMapping("/machine/initiate.json")
	@ResponseBody
	@CrossOrigin
	public JSONObject initiate(@RequestBody JSONObject json) throws Exception
	{
		Machine machine = machineMgr.getMachine(json.getIntValue("index"));
		machine.initiate();

		JSONObject res = new JSONObject();
		res.put("result", "success");

		return res;
	}
}
