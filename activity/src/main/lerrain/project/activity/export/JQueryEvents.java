package lerrain.project.activity.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Event;
import lerrain.tool.Common;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JQueryEvents
{
    JQueryExport jqe;
    ActivityDoc doc;

    String envJs = "";
    String envCss = "";
    String envPrds = ",";

    public JQueryEvents(JQueryExport jqe)
    {
        this.jqe = jqe;
        this.doc = jqe.doc;
    }

    public void initiate(Event event)
    {
        if (event == null)
            return;

        String onFinish = getJs(event.getFinish());
        if (onFinish != null)
            envJs += "var finish" + event.getId() + " = function() {" + onFinish + "};";

        String id = event.getElement().getId();
        if ("init".equals(event.getType()))
        {
            envJs += "ENV.reqInit = true;";
            if (onFinish != null)
                envJs += "ENV.afterInit = finish" + event.getId() + ";";
        }
        else if ("tiger".equals(event.getType()))
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

    public String getJs(Event event, String exp)
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
            //return "$('#" + id + "').hide();\n";
            return "document.getElementById('" + id + "').style.display = 'none';" + (event.getFinish() == null ? "" : "finish" + event.getId() + "();\n");
        }
        else if ("play".equals(event.getType()))
        {
            return null;
        }
        else if ("sparks".equals(event.getType()))
        {
            String js = "";
            String c = event.getParam() != null ? event.getParam().getString("value") : null;
            if (c != null)
                js += "if ("+c+") {";
            js += "var canvas = document.getElementById('CV" + id + "');\n" +
                    "    canvas.width=canvas.clientWidth;\n" +
                    "    canvas.height=canvas.clientHeight;\n" +
                    "    ENV.sparks" + id + " = new Sparks(canvas);\n";
            js += "ENV.sparks" + event.getElement().getId() + ".start();";
            if (c != null)
                js += "}";

            return js;
        }
        else if ("stopSparks".equals(event.getType()))
        {
            return "ENV.sparks" + event.getElement().getId() + ".stop();";
        }
        else if ("scroll".equals(event.getType()))
        {
            return "document.location.href= '#" + event.getElement().getId() + "';";
        }
        else if ("submit".equals(event.getType()))
        {
            jqe.finish.add(new Runnable()
            {
                @Override
                public void run()
                {
                    String ec = "var formInput = {";
                    for (String str : jqe.input.keySet())
                        ec += str + ": document.getElementById('INPUT" + str + "').value,";
                    ec += "formTag: null };";

                    for (String str : jqe.input.keySet())
                    {
                        Map verify = jqe.input.get(str);
                        if (verify != null && Common.boolOf(verify.get("require"), false))
                            ec += "if (formInput." + str + " == '') { Life.Dialog.alert('部分字段必填，请补充'); return; }";
                    }

                    jqe.root = jqe.root.replace("<!-- SUBMIT" + id + " -->", ec);
                }
            });

            String finishJs = event.getFinish() == null ? "" : "finish" + event.getId() + "()";
            return "<!-- SUBMIT" + id + " --> gpo.ask('submit', formInput, function(RES) { if (RES.flag == 'success') { " + finishJs + " } else { Life.Dialog.alert(RES.reason) } });";
        }
        else if ("redirect".equals(event.getType()))
        {
            if (event.getParam() == null)
                return null;

            String express = Common.trimStringOf(event.getParam().getString("value"));
            String url = Common.trimStringOf(event.getParam().getString("url"));
            boolean account = Common.boolOf(event.getParam().get("accountId"), false);

            if (Common.isEmpty(express))
            {
                return "goUrl(\"" + url + "\", "+account+");\n";
            }
            else
            {
                return "goUrl(" + jqe.expOf(express, exp) + ", "+account+");\n";
            }
        }
        else if ("toProduct".equals(event.getType()))
        {
            if (event.getParam() == null)
                return null;

            String text = event.getParam().getString("value");
            if (envPrds.indexOf("," + text + ",") < 0)
                envPrds += text + ",";
            return "gotoPrd('"+text+"');\n";
        }
        else if ("shareProduct".equals(event.getType()))
        {
            if (event.getParam() == null)
                return null;

            String text = event.getParam().getString("productId");
            if (!Common.isEmpty(text) && envPrds.indexOf("," + text + ",") < 0)
                envPrds += text + ",";

            String t = JSON.toJSONString(event.getParam());
            return "shareProduct("+t+");\n";
        }
        else if ("js".equals(event.getType()))
        {
            if (event.getParam() == null)
                return null;

            String text = event.getParam().getString("value");
            return "eval("+jqe.expOf(text, exp)+");";
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
            return getJs(event, null);
        }

        return null;
    }

    private String uri(String path)
    {
        if (path == null) return null;
        return "./" + new File(path).getName();
    }
}
