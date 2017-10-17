package lerrain.service.boot;

import lerrain.service.boot.base.ServiceInstance;
import lerrain.service.boot.base.ServicePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Repository
public class MachineDao
{
    @Autowired
    JdbcTemplate jdbc;

    public List<Machine> loadMachineList()
    {
        return jdbc.query("select * from s_machine where valid is null", new RowMapper<Machine>()
        {
            @Override
            public Machine mapRow(ResultSet m, int arg1) throws SQLException
            {
                final Machine srv = new Machine(m.getString("host"), m.getString("user"), m.getString("password"));
                srv.setIndex(m.getInt("index"));
                srv.setRoot(m.getString("root"));

                return srv;
            }
        });
    }
}
