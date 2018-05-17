package lerrain.project.activity.export;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Event;

public class JQueryEvents
{
    ActivityDoc doc;

    String envJs = "";

    public JQueryEvents(ActivityDoc doc)
    {
        this.doc = doc;
    }

    public String getJs(Event event)
    {
        if (event == null)
            return null;

        String onFinish = getJs(event.getFinish());
        if (onFinish != null)
            envJs += "var finish" + event.getId() + " = function() {" + onFinish + "};";

        String id = event.getElement().getId();
        if ("tiger".equals(event.getType()))
        {
            return "ENV.tiger.go('" + id + "', 100, finish" + event.getId() + ", 91, 3699, 193, 193);\n";
        }
        else if ("open".equals(event.getType()))
        {
            return "$('#" + id + "').show();\n";
        }
        else if ("close".equals(event.getType()))
        {
            return "$('#" + id + "').hide();\n";
        }

        return null;
    }

    public String getJs(JSONObject of)
    {
        if (of == null)
            return null;

        if ("event".equals(of.getString("type"))) //调用该事件关联的event
        {
            String eventId = of.getString("eventId");
            Event event = doc.findEvent(eventId);
            return getJs(event);
        }

        return null;
    }
}
