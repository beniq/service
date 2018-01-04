package lerrain.service.boot.base;

import lerrain.service.boot.BootUtil;
import lerrain.service.boot.Machine;
import lerrain.service.boot.MachineMgr;
import lerrain.service.boot.ServiceMgr;
import lerrain.tool.Common;
import lerrain.tool.Network;

import java.util.Date;
import java.util.Map;

public class ServiceInstance
{
    ServicePack service;

    Long id;

    int env;

    int mac;
    int port;

    Machine machine;

    String state;

    String jarFile;
    String logFile;

    Date restartTime, deployTime;

    public ServiceInstance(ServicePack service)
    {
        this.service = service;
    }

    public ServicePack getService()
    {
        return service;
    }

    public void setService(ServicePack service)
    {
        this.service = service;
    }

    public int getEnv()
    {
        return env;
    }

    public void setEnv(int env)
    {
        this.env = env;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getLogFile()
    {
        return logFile;
    }

    public void setLogFile(String logFile)
    {
        this.logFile = logFile;
    }

    public int getMacIndex()
    {
        return mac;
    }

    public void setMacIndex(int machine)
    {
        this.mac = machine;
    }

    public Machine getMachine()
    {
        return machine;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void refresh(MachineMgr machineMgr)
    {
        machine = machineMgr.getMachine(mac);

        jarFile = service.getCode() + "/" + getEnvString() + "/" + service.getCode() + ".jar";
        logFile = service.getCode() + "/" + getEnvString() + "/" + service.getCode() + ".log";
    }

    public Date getRestartTime()
    {
        return restartTime;
    }

    public void setRestartTime(Date restartTime)
    {
        this.restartTime = restartTime;
    }

    public Date getDeployTime()
    {
        return deployTime;
    }

    public void setDeployTime(Date deployTime)
    {
        this.deployTime = deployTime;
    }

    public String getEnvString()
    {
        if (env == ServicePack.PRD)
            return "prd";
        if (env == ServicePack.UAT)
            return "uat";
        return "test";
    }

    public String copyJar()
    {
        try
        {
            this.setDeployTime(new Date());

            String command = String.format("mkdir -p %s; cp -f %s %s;", machine.getServicePath(service.getCode() + "/" + getEnvString()), machine.getPath(service.getCurrentJar()), machine.getServicePath(jarFile));
            return machine.run(command);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    public String start(Map<String, Object> srvs)
    {
        String lib = machine.getServicePath("lib/*") + ":" + machine.getPath("lib/*");

        if (service.getLibs() != null)
            for (String path : service.getLibs())
                lib += ":" + machine.getPath("lib/" + path);

        String javaBin = machine.getJavaBin();
        if (javaBin == null)
            javaBin = "";
        else
            javaBin = machine.getPath(javaBin);

        String command = String.format("cd %s; nohup %sjava %s-cp %s:%s %s --server.port=%d", machine.getServicePath(service.getCode() + "/" + getEnvString()), javaBin, service.getJvmOption() == null ? "" : service.getJvmOption() + " ", machine.getServicePath(jarFile), lib, service.getStartClass(), port);

        Map<String, String> params = BootUtil.convertParams(srvs);
        if (params != null)
        {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : params.entrySet())
                sb.append(" --" + entry.getKey() + "=\"" + entry.getValue() + "\"");

            sb.append(" --env=" + getEnvString());
            command += sb.toString();
        }

        String logPath = machine.getLogPath(service.getCode() + "-" + this.getEnvString() + ".log");
        command += " >> " + logPath + " 2>&1 &";

        try
        {
            this.setRestartTime(new Date());
            return machine.run(command);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    public String stop()
    {
        try
        {
            String cmd = String.format("ps -ef | grep \"java %s-cp %s\" | grep -v grep | awk '{print $2}' | xargs kill -9", service.getJvmOption() == null ? "" : service.getJvmOption() + " ", machine.getServicePath(jarFile));
//            String cmd = String.format("/usr/sbin/lsof -i:%d|grep java|awk '{print $2}'|xargs kill -9", port);
            return machine.run(cmd);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }

    public String state()
    {
        String loc = "http://" + machine.getHost() + ":" + port + "/health";
        return Network.request(loc, 300);
    }

    public String reset()
    {
        String loc = "http://" + machine.getHost() + ":" + port + "/reset";
        return Network.request(loc, 300);
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String viewLog(int line)
    {
        try
        {
            String logPath = machine.getLogPath(service.getCode() + "-" + this.getEnvString() + ".log");
            String cmd = String.format("tail -%d %s", line, logPath);
            return machine.run(cmd);
        }
        catch (Exception e)
        {
            return e.getMessage();
        }
    }
}
