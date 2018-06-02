package lerrain.project.activity;

import com.alibaba.fastjson.JSON;
import lerrain.project.activity.base.ActivityDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivityDao
{
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
}
