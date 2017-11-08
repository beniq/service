package lerrain.service.boot;

import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceMgr
{
    @Autowired
    ServiceDao serviceDao;

    @Autowired
    MachineMgr machineMgr;

    Map<Integer, Map<String, Object>> env;

    Map<Long, ServicePack> m1;
    Map<Long, ServiceInstance> m2;
    Map<String, ServicePack> m3;

    List<ServicePack> prd;

    @PostConstruct
    public void reset()
    {
        machineMgr.reset();

        env = serviceDao.loadServiceEnv();

        m1 = new HashMap<>();
        m2 = new HashMap<>();
        m3 = new HashMap<>();

        prd = serviceDao.loadService();

        for (ServicePack sp : prd)
        {
            m1.put(sp.getId(), sp);
            m3.put(sp.getCode(), sp);

            if (sp.getInstance() != null) for (ServiceInstance si : sp.getInstance())
            {
                si.refresh(machineMgr);
                m2.put(si.getId(), si);
            }
        }
    }

    public Object getParam(int env, String name)
    {
        return getParam(this.env.get(env), name);
    }

    private Object getParam(Map env, String name)
    {
        int p = name.indexOf(".");
        if (p < 0)
            return env.get(name);
        else
            return getParam((Map)env.get(name.substring(0, p)), name.substring(p + 1));
    }

    public Map<String, String> getAllServiceAddress(int env, String group)
    {
        Map<String, String> addrs = new HashMap<>();

        for (ServicePack sp : prd)
        {
            if (sp.getGroup() == null || sp.getGroup().equals(group))
            {
                List<ServiceInstance> list = sp.getInstance(env);
                if (list != null && !list.isEmpty())
                {
                    ServiceInstance inst = list.get(0);
                    String addr = "http://" + inst.getMachine().getHost() + ":" + inst.getPort();
                    addrs.put(sp.getCode(), addr);
                }
            }
        }

        return addrs;
    }

    public List<ServicePack> getServiceList()
    {
        return prd;
    }

    public void setCurrentJar(ServicePack sp, String jar)
    {
        sp.setCurrentJar(jar);

        serviceDao.saveCurrentJar(sp, jar);
    }

    public void restart(Long instanceId)
    {
        ServiceInstance si = getServiceInstance(instanceId);

        Map<String, Object> env = new HashMap<>();
        env.put("service", this.getAllServiceAddress(si.getEnv(), si.getService().getGroup()));

        List<String[]> config = si.getService().getConfig();
        if (config != null) for (String[] ss : config)
        {
            if (ss[1] != null)
            {
                Object val;
                if (ss[1].startsWith("{"))
                    val = getParam(si.getEnv(), ss[1].substring(1, ss[1].length() - 1));
                else
                    val = ss[1];

                if (val instanceof String)
                    val = ((String)val).replaceAll("[%]ROOT[%]", si.getMachine().getRoot());

                env.put(ss[0], val);
            }
        }

        serviceDao.saveReleaseInfo(si.getId(), "stop", si.stop(), new Date());
        serviceDao.saveReleaseInfo(si.getId(), "restart", si.start(env), new Date());
        serviceDao.saveRestartTime(si);
    }

    public void stop(Long instanceId)
    {
        ServiceInstance si = getServiceInstance(instanceId);
        serviceDao.saveReleaseInfo(si.getId(), "stop", si.stop(), new Date());
    }

    public void deploy(Long instanceId)
    {
        ServiceInstance si = getServiceInstance(instanceId);

        serviceDao.saveReleaseInfo(si.getId(), "deploy", si.copyJar(), new Date());
        serviceDao.saveDeployTime(si);
    }

    public List<String[]> viewReleaseInfo(Long instanceId)
    {
        return serviceDao.loadReleaseInfo(instanceId);
    }

    public ServicePack getService(Long serviceId)
    {
        return m1.get(serviceId);
    }

    public ServiceInstance getServiceInstance(Long instanceId)
    {
        return m2.get(instanceId);
    }

    public ServicePack getService(String serviceCode)
    {
        return m3.get(serviceCode);
    }

//    public Map<Long, String> refresh()
//    {
//        try
//        {
//            Map<Long, String> rrr = new HashMap<>();
//
//            String cmd = "/usr/sbin/lsof -i|grep java|awk '{print $2,$9}'";
//            String res = MachineMgr.getMacIndex(0).run(cmd);
//
//            String[] line = res.split("[\n]");
//            for (ServiceInstance si : m2.values())
//            {
//                for (String l : line)
//                {
//                    if (l.endsWith(":" + si.getPort()))
//                    {
//                        si.setState(l.split(" ")[0]);
//                        rrr.put(si.getId(), si.state());
//                    }
//                }
//            }
//
//            return rrr;
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
}
