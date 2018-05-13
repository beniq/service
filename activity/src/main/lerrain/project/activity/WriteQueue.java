package lerrain.project.activity;

import lerrain.project.activity.base.ActivityDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class WriteQueue implements Runnable
{
	public static final int MAX	= 100000;

	@Autowired
	ActivityDao actDao;

	Map<Long, ActivityDoc> list = new HashMap<>();

	Thread thread = new Thread(this);

	public void add(ActivityDoc msg)
	{
		if (msg == null || msg.getActId() == null)
			return;

		synchronized (list)
		{
			if (list.size() < MAX)
				list.put(msg.getActId(), msg);
			
			list.notify();
		}
	}

	@PostConstruct
	public void start()
	{
		if (!thread.isAlive())
			thread.start();
	}

	public void run()
	{
		Map<Long, ActivityDoc> pack = new HashMap<>();
		
		while (true)
		{
			synchronized (list)
			{
				pack.putAll(list);
				list.clear();
			}
			
			if (pack.size() > 0)
			{
				for (ActivityDoc uw : pack.values())
				{
					try
					{
						actDao.save(uw);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				try
				{
					Thread.sleep(30000);
				}
				catch (InterruptedException e)
				{
				}
			}
			else synchronized (list) 
			{ 
				try
				{
					if (list.isEmpty())
						list.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
	
			pack.clear();
		}
	}
}
