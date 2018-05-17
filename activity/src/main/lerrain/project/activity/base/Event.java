package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONObject;

import java.util.UUID;

public class Event
{
    String id;
    String type;

    JSONObject param;
    JSONObject finish;

    Element element;

    public Event()
    {
        setId(UUID.randomUUID().toString());
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        if (id != null)
            this.id = id.replaceAll("[-]", "");;
    }

    public JSONObject getFinish()
    {
        return finish;
    }

    public void setFinish(JSONObject finish)
    {
        this.finish = finish;
    }

    public void setFinish(Event event)
    {
        JSONObject p = new JSONObject();
        p.put("type", "event");
        p.put("eventId", event.getId());

        setFinish(p);
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public JSONObject getParam()
    {
        return param;
    }

    public void setParam(JSONObject param)
    {
        this.param = param;
    }

    public Element getElement()
    {
        return element;
    }

    public void setElement(Element element)
    {
        this.element = element;
    }
}
