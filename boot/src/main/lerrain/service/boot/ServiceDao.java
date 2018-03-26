package lerrain.service.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ServiceDao
{
    @Autowired
    JdbcTemplate jdbc;

    public void saveService(Long serviceId, int env, int port, List<Integer> machine)
    {
        String delSQL = "delete from s_service_instance where service_id = ? and env = ? and param is null";
        jdbc.update(delSQL, serviceId, env);

        String insert = "insert into s_service_instance(service_id, env, port, machine) values(?, ?, ?, ?)";
        for (int m : machine)
            jdbc.update(insert, serviceId, env, port, m);
    }

    public List<ServicePack> loadService()
    {
        return jdbc.query("select * from s_service where valid is null order by workspace, id", new RowMapper<ServicePack>()
        {
            @Override
            public ServicePack mapRow(ResultSet m, int arg1) throws SQLException
            {
                final ServicePack srv = new ServicePack();
                srv.setId(m.getLong("id"));
                srv.setCode(m.getString("code"));
                srv.setName(m.getString("name"));
                srv.setGroup(m.getString("workspace"));
                srv.setCurrentJar(m.getString("jar_file"));
                srv.setStartClass(m.getString("class_name"));
                srv.setJvmOption(m.getString("jvm_option"));

                String dbRule = m.getString("db_rule");
                if (!Common.isEmpty(dbRule))
                    srv.setDbRule(JSONObject.parseObject(dbRule));

                String libStr = m.getString("lib");
                if (!Common.isEmpty(libStr))
                    srv.setLibs(libStr.split(","));

                String config = m.getString("config");
                if (!Common.isEmpty(config))
                {
                    List<String[]> conf = new ArrayList<>();
                    String[] pp = config.split(",");
                    for (String p : pp)
                    {
                        String[] x = p.split("=");
                        if (x.length == 2)
                            conf.add(x);
                        else
                            conf.add(new String[] {"", x[0]});
                    }
                    srv.setConfig(conf);
                }

                String deployScript = m.getString("deploy_script");
                if(!Common.isEmpty(deployScript))
                {
                    srv.setDeployScript(JSONObject.parseObject(deployScript));
                }

                srv.setInstance(jdbc.query("select * from s_service_instance where service_id = ?", new Object[] {srv.getId()}, new RowMapper<ServiceInstance>()
                {
                    @Override
                    public ServiceInstance mapRow(ResultSet m, int rowNum) throws SQLException
                    {
                        ServiceInstance inst = new ServiceInstance(srv);
                        inst.setId(m.getLong("id"));
                        inst.setEnv(m.getInt("env"));
                        inst.setPort(m.getInt("port"));
                        inst.setMacIndex(m.getInt("machine"));
                        inst.setDeployTime(m.getTimestamp("deploy_time"));
                        inst.setRestartTime(m.getTimestamp("restart_time"));

                        String paramStr = m.getString("param");
                        if (!Common.isEmpty(paramStr))
                            inst.setParam(JSON.parseObject(paramStr));

                        return inst;
                    }
                }));

                return srv;
            }
        });
    }

    public void saveCurrentJar(ServicePack sp, String jar)
    {
        jdbc.update("update s_service set jar_file = ? where id = ?", jar, sp.getId());
    }

    public void saveRestartTime(ServiceInstance si)
    {
        jdbc.update("update s_service_instance set restart_time = ? where id = ?", si.getRestartTime(), si.getId());
    }

    public void saveDeployTime(ServiceInstance si)
    {
        jdbc.update("update s_service_instance set deploy_time = ? where id = ?", si.getDeployTime(), si.getId());
    }

    public void saveReleaseInfo(Long instanceId, String opt, String log, Date time)
    {
        jdbc.update("insert into s_service_release(instance_id, opt, log, time) values(?, ?, ?, ?)", instanceId, opt, log, time);
    }

    public List<String[]> loadReleaseInfo(Long instanceId)
    {
        return jdbc.query("select * from s_service_release where instance_id = ? order by id limit 0, 100", new Object[]{instanceId}, new RowMapper<String[]>()
        {
            @Override
            public String[] mapRow(ResultSet rs, int rowNum) throws SQLException
            {
                return new String[]{Common.getString(rs.getTimestamp("time"), null), rs.getString("opt"), rs.getString("log")};
            }
        });
    }

    public Map<Integer, Map<String, Object>> loadServiceEnv()
    {
        final Map<Integer, Map<String, Object>> r = new HashMap<>();
        jdbc.query("select * from s_service_env", new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                JSONObject env = JSON.parseObject(rs.getString("constant"));
                r.put(rs.getInt("env_id"), env);
            }
        });

        return r;
    }
}
