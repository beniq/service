package lerrain.service.boot.base;

import java.util.Map;

/**
 * Created by lerrain on 2017/11/3.
 */
public class ServiceEnv
{
    String jdkCmd;
    String code;

    Map<String, Object> env;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public Map<String, Object> getEnv()
    {
        return env;
    }

    public void setEnv(Map<String, Object> env)
    {
        this.env = env;
    }

    public String getJdkCmd()
    {
        return jdkCmd;
    }

    public void setJdkCmd(String jdkCmd)
    {
        this.jdkCmd = jdkCmd;
    }
}
