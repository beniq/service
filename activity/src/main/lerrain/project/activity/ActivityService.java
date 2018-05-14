package lerrain.project.activity;

import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.export.JQueryExport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ActivityService
{
	@Autowired
	ActivityDao actDao;

	Map<Long, ActivityDoc> map = new HashMap<>();

	long actIdSeq = -1;

	public ActivityDoc getAct(Long actId)
	{
		ActivityDoc doc = map.get(actId);

		if (doc == null)
		{
			doc = actDao.load(actId);
			map.put(actId, doc);
		}

		return doc;
	}

	public ActivityDoc newDoc()
	{
		if (actIdSeq < 0)
			actIdSeq = actDao.maxId() + 1;

		ActivityDoc doc = new ActivityDoc();
		doc.setActId(actIdSeq++);

		map.put(doc.getActId(), doc);

		return doc;
	}

	public String depoly(ActivityDoc doc, String env)
	{
		String url = null;
		String html = JQueryExport.export(doc);

		if ("test".equals(env))
		{

		}
		else if ("uat".equals(env))
		{

		}
		else if ("prd".equals(env))
		{

		}

		return url;
	}
}
