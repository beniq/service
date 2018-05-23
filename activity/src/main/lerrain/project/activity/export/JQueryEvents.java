package lerrain.project.activity.export;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Event;

import java.io.File;
import java.util.List;

public class JQueryEvents
{
    ActivityDoc doc;

    String envJs = "";
    String envCss = "";

    public JQueryEvents(ActivityDoc doc)
    {
        this.doc = doc;
    }

    public void initiate(Event event)
    {
        if (event == null)
            return;

        String onFinish = getJs(event.getFinish());
        if (onFinish != null)
            envJs += "var finish" + event.getId() + " = function() {" + onFinish + "};";

        String id = event.getElement().getId();
        if ("tiger".equals(event.getType()))
        {
            envJs += "ENV.tiger = new Tiger1();\n";
        }
        else if ("sparks".equals(event.getType()))
        {
        }
        else if ("play".equals(event.getType()))
        {
            List<String> list = event.getElement().getFile();
            String line = "";
            for (int i=0;i<list.size();i++)
            {
                line += i*100/list.size() + "% {background-image: url("+uri(list.get(i))+");}\n";
            }
            String css = JQueryExport.playCss.replaceAll("<!-- EVENT_BG -->", line);
            css = css.replaceAll("<!-- EVENT_ID -->", event.getId());
            css = css.replaceAll("<!-- EVENT_TIME -->", "1");
            envCss += css;
        }
    }

    public String getJs(Event event)
    {
        if (event == null)
            return null;

        String id = event.getElement().getId();
        if ("tiger".equals(event.getType()))
        {
            int x = (int)event.getElement().getX();
            int y = (int)event.getElement().getY();
            int w = (int)event.getElement().getW();
            int h = (int)event.getElement().getH();

            return "        try{\n" +
                    "            gpo.post(\"/npo/temp.json\", {activity:'"+doc.getCode()+"', event:'tiger', account:gpo.accountId}, function(r){\n" +
                    "                if (r != null && r.result == \"success\") {\n" +
                    "                    var c = r.content;\n" +
                    "                    ENV.init.times = c.times;\n" +
                    "                    ENV.init.link = c.link;\n" +
                    "      ENV.tiger.go('" + id + "', 65+eval(c.result), finish" + event.getId() + ", "+x+", "+y+", "+w+", "+h+");\n" +
                    "                    refreshText();\n" +
                    "                }\n" +
                    "            });\n" +
                    "        }catch(e){console.log('refreshAcc-err', e);}";

        }
        else if ("open".equals(event.getType()))
        {
            return "$('#" + id + "').show();\n" + (event.getFinish() == null ? "" : "finish" + event.getId() + "();\n");
        }
        else if ("close".equals(event.getType()))
        {
            return "$('#" + id + "').hide();\n";
        }
        else if ("play".equals(event.getType()))
        {
            return null;
        }
        else if ("sparks".equals(event.getType()))
        {
            String js = "var canvas = document.getElementById('CV"+event.getElement().getId()+"');\n" +
                    "    canvas.width=canvas.clientWidth;\n" +
                    "    canvas.height=canvas.clientHeight;\n" +
                    "    var sp = new Sparks(canvas);\n" +
                    "    sp.start();";
            return js;
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

    private String uri(String path)
    {
        if (path == null) return null;
        return "./" + new File(path).getName();
    }
}
