package lerrain.project.activity.export;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Event;
import lerrain.project.activity.base.Page;
import lerrain.tool.Common;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JQueryExport
{
    public static String rootHtml;
    public static String pageHtml;
    public static String popupCss;
    public static String playCss;
    public static String starCss;

    String server;

    String root = JQueryExport.rootHtml;
    String js1 = "", js2 = "", js3 = "";
    String pages = "";
    String css = "";

    Map<String, String> xcss = new HashMap();

    public JQueryExport(String env)
    {
        if ("test".equalsIgnoreCase(env))
        {
            server = "https://gpo-test.iyunbao.com";
        }
        else if ("uat".equalsIgnoreCase(env))
        {
            server = "https://gpo-uat.iyunbao.com";
        }
        else if ("prd".equalsIgnoreCase(env))
        {
            server = "https://gpo.iyunbao.com";
        }
    }

    public String export(ActivityDoc doc)
    {
        JQueryEvents tool = new JQueryEvents(doc);

        js1 += "var xx = document.getElementById(\"ccc\");";

        for (Page p : doc.getList())
        {
            js1 += String.format("xx.style.height = xx.offsetWidth * %d / %d;", p.getH(), p.getW());

            String ph = JQueryExport.pageHtml;

            String style = "";
            if (p.getBackground() != null)
                style += String.format("background-image:url(%s);", uri(p.getBackground()));

            ph = ph.replace("<!-- STYLE -->", style);
            ph = ph.replace("<!-- ELEMENTS -->", build(tool, p.getList()));

            pages += ph;
        }

        js2 += tool.envJs;

        for (String cssStr : xcss.values())
            css += cssStr;

        root = root.replace("<!-- TITLE -->", doc.getName());
        root = root.replace("<!-- PAGES -->", pages);
        root = root.replaceAll("<!-- HEIGHT -->", doc.getList().get(0).getH() + "");
        root = root.replace("<!-- JS_START -->", js1);
        root = root.replace("<!-- JS_ENV -->", js2);
        root = root.replace("<!-- JS_TEXT -->", js3);
        root = root.replace("<!-- CSS -->", css + tool.envCss);
        root = root.replaceAll("<!-- ACT_CODE -->", doc.getCode());
        root = root.replaceAll("<!-- SERVER -->", server);

        return root;
    }

    private String build(JQueryEvents tool, List<Element> list)
    {
        String elements = "";

        if (list != null)
        {
            for (Element e : list)
            {
                String id = e.getId();
                String es = build(tool, e.getChildren());

                String pos = null;
                String style = "";
                String className = "";
                if (e.getFile().size() > 0)
                    style += String.format("background-image:url(%s);", uri(e.getFile().get(0)));
                if (e.getFontSize() != null)
                {
                    style += "text-align:center;";
                    js1 += "fnt('" + id + "'," + e.getH() * 2 / 3 + "," + e.getH() + ");";
                }
                if (e.getColor() != null)
                    style += String.format("color:%s;", e.getColor());
                if (e.getBgColor() != null)
                    style += String.format("background-color:%s;", e.getBgColor());
                if (e.getZ() > 0)
                    style += String.format("z-index:%d;", e.getZ());
                if (e.getStyle() != null)
                {
                    if (e.getStyle().get("hide") != null)
                        style += "display:none; overflow:hidden;";
                    if (e.getStyle().get("popup") != null)
                    {
                        css += JQueryExport.popupCss;
                        //style += String.format("transform: translateZ(%dpx);", 400);
                        className += "plat10_xz";
                    }
                    if (e.getStyle().get("sparks") != null) {
                        es += "<canvas id='CV" + id + "' style='width:100%;height:100%;'></canvas>";
                    }
                    if (e.getStyle().get("alpha50") != null)
                        style += "opacity:0.5;";
                    if (e.getStyle().get("alpha") != null)
                        style += "opacity:"+(Common.doubleOf(e.getStyle().get("alpha"), 1))+";";
                    if (e.getStyle().get("stars") != null)
                    {
                        xcss.put("star", JQueryExport.starCss);
                        int sq = (int)(e.getW()*e.getH());
                        int size = sq > 250000 ? 100 : (int)(Math.sqrt(sq) / 5);
                        int num = (int)(e.getW()*e.getH()/size/size/2.2f);
                        for (int k=0;k<num;k++)
                            es += "<img class=\"plat10_xx"+(k%4+1)+"\" style=\"z-index: 0;\" id=\""+id+"_"+k+"\">\n";
                        js1 += "star1('"+id+"',"+num+","+e.getW()+","+e.getH()+","+size+");";
                    }
                    if (e.getStyle().get("fixed") != null)
                        pos = "fixed";
                }

                //生成所有事件的js
                List<Event> evs = e.getEvents();
                for (Event ev : evs)
                {
                    tool.initiate(ev);

                    if ("play".equals(ev.getType()))
                        className += " play" + ev.getId();
                }

                String ea = "";
                if (e.getAction().size() > 0)
                {
                    String ec = "";
                    for (int i=0;i<e.getAction().size();i++)
                    {
                        JSONObject act = e.getAction().getJSONObject(i);
                        String type = act.getString("type");
                        if ("redirect".equals(type))
                        {
                            ec += "document.location.href = " + act.getString("param") + ";\n";
                        }
                        else if ("event".equals(type))
                        {
                            String eventId = act.getString("param");
                            Event event = tool.doc.findEvent(eventId);
                            ec += tool.getJs(event);
                        }
                    }

                    js2 += "var click" + id + " = function(EVENT) {" + ec + "};";
                    ea = "onClick=\"click" + id + "()\"";
                }

                if (e.getFontSize() != null && e.getText() != null)
                    js3 += "$(\"#" + id + "\").html(" + e.getText() + ");\n";

                String eh = String.format("<div id=\"%s\" class=\"%s\" style=\"%s\" %s>%s</div>", id, className, style, ea, es);
                elements += eh;

                js1 += String.format("pot(\"%s\", %.4f, %.4f, %.4f, %.4f, %s);\n", id, e.getX(), e.getY(), e.getW(), e.getH(), pos == null ? "null" : "'" + pos + "'");
            }
        }

        return elements;
    }

    private String uri(String path)
    {
        if (path == null) return null;
        return "./" + new File(path).getName();
    }
}
