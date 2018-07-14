package lerrain.project.activity;

import com.alibaba.fastjson.JSON;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.tool.Common;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ActivityDao
{
	Long idSeq;

	@Autowired
	JdbcTemplate jdbc;

	public List<Long> list(int from, int num)
	{
		String sql = "select id from t_activity_editor order by id desc limit ?, ?";
		return jdbc.queryForList(sql, Long.class, from, num);
	}

	public ActivityDoc load(Long actId)
	{
		String sql = "select detail from t_activity_editor where id = ?";
		String json = jdbc.queryForObject(sql, String.class, actId);

		return DocTool.toDoc(JSON.parseObject(json));
	}

	public void save(ActivityDoc doc)
	{
		String sql = "replace into t_activity_editor(id, detail) values(?, ?)";
		jdbc.update(sql, doc.getActId(), DocTool.toJson(doc).toJSONString());
	}

	public long maxId()
	{
		String sql = "select max(id) from t_activity_editor";
		return jdbc.queryForObject(sql, Long.class);
	}

	@PostConstruct
	private void initPosterId()
	{
		idSeq = jdbc.queryForObject("select max(id) from t_activity_poster", Long.class);

		if (idSeq == null)
			idSeq = 10000L;
	}

	public List<Map> listPoster(int from, int num)
	{
		String sql = "select * from t_activity_poster order by id desc limit ?, ?";
		return jdbc.query(sql, new Object[]{from, num}, new RowMapper<Map>()
		{
			@Override
			public Map mapRow(ResultSet rs, int rowNum) throws SQLException
			{
				Map m = new HashMap();
				m.put("id", rs.getLong("id"));
				m.put("name", rs.getString("name"));
				m.put("imgUrl", rs.getString("img_url"));
				m.put("qrx", Common.toDouble(rs.getObject("qr_x")));
				m.put("qry", Common.toDouble(rs.getObject("qr_y")));
				m.put("qrw", Common.toDouble(rs.getObject("qr_w")));
				m.put("qrUrl", rs.getString("qr_url"));
				m.put("namex", Common.toDouble(rs.getObject("name_x")));
				m.put("namey", Common.toDouble(rs.getObject("name_y")));
				m.put("nameFontSize", rs.getString("name_font_size"));

				return m;
			}
		});
	}

	public synchronized Map savePoster(Map poster)
	{
		if (!poster.containsKey("id"))
			poster.put("id", ++idSeq);

		String sql = "replace into t_activity_poster(id, name, img_url, qr_x, qr_y, qr_w, qr_url, name_x, name_y, name_font_size) values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		jdbc.update(sql,
				poster.get("id"),
				poster.get("name"),
				poster.get("imgUrl"),
				poster.get("qrx"),
				poster.get("qry"),
				poster.get("qrw"),
				poster.get("qrUrl"),
				poster.get("namex"),
				poster.get("namey"),
				poster.get("nameFontSize")
		);

		return poster;
	}
}
