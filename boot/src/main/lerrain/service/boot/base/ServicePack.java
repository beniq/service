package lerrain.service.boot.base;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lerrain on 2017/7/11.
 */
public class ServicePack
{
    public static final int TEST = 1;
    public static final int UAT = 2;
    public static final int PRD = 3;

    Long id;

    String code;
    String name;
    String group;

    String currentJar;
    String startClass;
    String jvmOption;

    String[] libs;

    List<ServiceInstance> instance;

    List<String[]> config;

    JSONObject dbRule;

    public List<String[]> getConfig()
    {
        return config;
    }

    public void setConfig(List<String[]> config)
    {
        this.config = config;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getJvmOption()
    {
        return jvmOption;
    }

    public void setJvmOption(String jvmOption)
    {
        this.jvmOption = jvmOption;
    }

    public List<ServiceInstance> getInstance()
    {
        return instance;
    }

    public List<ServiceInstance> getInstance(int env)
    {
        List<ServiceInstance> r = new ArrayList<>();
        for (ServiceInstance ins : instance)
        {
            if (ins.getEnv() == env)
                r.add(ins);
        }
        return r;
    }

    public String[] getLibs()
    {
        return libs;
    }

    public void setLibs(String[] libs)
    {
        this.libs = libs;
    }

    public JSONObject getDbRule()
    {
        return dbRule;
    }

    public void setDbRule(JSONObject dbRule)
    {
        this.dbRule = dbRule;
    }

    public void setInstance(List<ServiceInstance> instance)
    {
        this.instance = instance;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCurrentJar()
    {
        return currentJar;
    }

    public void setCurrentJar(String currentJar)
    {
        this.currentJar = currentJar;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getStartClass()
    {
        return startClass;
    }

    public void setStartClass(String startClass)
    {
        this.startClass = startClass;
    }
}
