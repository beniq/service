package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.DocTool;

import java.util.List;
import java.util.Map;
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

    public Event copy(Element element, Map<String, String> mm, List<Map> cb)
    {
        Event ev = new Event();
        ev.element = element;
        ev.type = this.type;
        ev.param = DocTool.copy(param, null);
        ev.finish = DocTool.copy(finish, cb);

        if (mm != null)
            mm.put(this.id, ev.id);

        return ev;
    }
}
