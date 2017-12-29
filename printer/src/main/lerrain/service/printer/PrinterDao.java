package lerrain.service.printer;

import lerrain.service.printer.util.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class PrinterDao
{
	@Autowired
	JdbcTemplate jdbc;
	
	public List<TypesetTemplate> loadAll()
	{
		List<TypesetTemplate> res = jdbc.query("select * from t_printer_template", new RowMapper<TypesetTemplate>()
		{
			@Override
			public TypesetTemplate mapRow(ResultSet tc, int arg1) throws SQLException
			{
				TypesetTemplate tt = new TypesetTemplate();
				tt.setId(tc.getLong("id"));
				tt.setCode(tc.getString("code"));
				tt.setName(tc.getString("name"));
				tt.setSignId(Common.toLong(tc.getString("sign")));

				tt.setWorkDir(tc.getString("work_dir"));
				tt.setTemplateFile(tc.getString("template_file"));
				tt.setTestFile(tc.getString("test_file"));

				return tt;
			}
		});

		return res;
	}

	public List<Sign> loadAllSign()
	{
		return jdbc.query("select * from t_printer_sign", new RowMapper<Sign>()
		{
			@Override
			public Sign mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				Sign s = new Sign();
				s.setId(rs.getLong("id"));
				s.setKeystore(rs.getString("keystore"));
				s.setPassword(rs.getString("password"));
				s.setScope(rs.getString("scope"));
				s.setReason(rs.getString("reason"));
				s.setLocation(rs.getString("location"));

				return s;
			}
		});
	}

	public void log(String client, Long templateId, String fileType, int outType, int result, String ip, int time1, int time2, Date operateTime, int pages)
	{
		String sql = "insert into t_printer_log (client_key, template_id, file_type, out_type, build_time, export_time, page, result, ip, operate_time) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbc.update(sql, client, templateId, fileType, outType, time1, time2, pages, result, ip, operateTime);
	}

	public Map<String, Long> loadAllClient()
	{
		final Map<String, Long> m = new HashMap<>();

		String sql = "select * from t_printer_client";
		jdbc.query(sql, new RowCallbackHandler()
		{
			@Override
			public void processRow(ResultSet rs) throws SQLException
			{
				m.put(rs.getString("key"), rs.getLong("id"));
			}
		});

		return m;
	}

	public TypesetTemplate create(String code, String name)
	{
		jdbc.update("insert into t_printer_template(code, name) values(?,?)", code, name);

		Long id = jdbc.queryForObject("select last_insert_id()", Long.class);

		TypesetTemplate tt = new TypesetTemplate();
		tt.setId(id);
		tt.setCode(code);
		tt.setName(name);
		tt.setWorkDir(id.toString());

		jdbc.update("update t_printer_template set work_dir = ? where id = ?", tt.getWorkDir(), tt.getId());

		return tt;
	}

	public void save(TypesetTemplate tt)
	{
		jdbc.update("replace into t_printer_template (id, code) values(?, ?)", tt.getId(), tt.getCode());
		jdbc.update("update t_printer_template set code = ?, name = ?, work_dir = ?, template_file = ?, test_file = ?, sign = ? where id = ?", tt.getCode(), tt.getName(), tt.getWorkDir(), tt.getTemplateFile(), tt.getTestFile(), tt.getSignId(), tt.getId());
	}

}
