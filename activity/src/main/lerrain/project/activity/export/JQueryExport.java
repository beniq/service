package lerrain.project.activity.export;

import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Event;
import lerrain.project.activity.base.Page;
import lerrain.tool.Common;

import java.io.File;
import java.util.List;

public class JQueryExport
{
    String server;

    String root = JQueryTemplate.root;
    String js1 = "", js2 = "";
    String pages = "";

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

        int i = 0;
        for (Page p : doc.getList())
        {
            String id = "p_" + i++;

            js1 += String.format("xx.style.height = xx.offsetWidth * %d / %d;", p.getH(), p.getW());

            String ph = JQueryTemplate.page;

            String style = "";
            if (p.getBackground() != null)
                style += String.format("background-image:url(%s);", uri(p.getBackground()));

            ph = ph.replace("<!-- STYLE -->", style);
            ph = ph.replace("<!-- ELEMENTS -->", build(tool, p.getList()));

            pages += ph;
        }

        js2 += tool.envJs;

        root = root.replace("<!-- TITLE -->", doc.getName());
        root = root.replace("<!-- PAGES -->", pages);
        root = root.replace("<!-- JS_START -->", js1);
        root = root.replace("<!-- JS_ENV -->", js2);
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

                String style = "";
                if (e.getFile() != null)
                    style += String.format("background-image:url(%s);", uri(e.getFile()));
                if (e.getFontSize() != null)
                    style += String.format("font-size:%s;", e.getFontSize());
                if (e.getColor() != null)
                    style += String.format("color:#%s;", e.getColor());
                if (e.getBgColor() != null)
                    style += String.format("background-color:#%s;", e.getBgColor());
                if (e.getStyle() != null)
                {
                    if (Common.boolOf(e.getStyle().get("hide"), false))
                        style += "display:none;";
                }

                String ex = "", ea = "";
                //生成所有事件的js
                List<Event> evs = e.getEvents();
                for (Event ev : evs)
                {
                    if ("tiger".equals(ev.getType()))
                    {
                        js2 += "ENV.tiger = new Tiger1();\n";
                    }
                    else if ("click".equals(ev.getType()))
                    {
                        ex += tool.getJs(ev.getFinish());
                    }
                }

                if (!"".equals(ex))
                {
                    js2 += "var click" + id + " = function(EVENT) {" + ex + "};";
                    ea = "onClick=\"click" + id + "()\"";
                }

                if (e.getFontSize() != null && e.getText() != null)
                    js1 += "$(\"#" + id + "\").html(" + e.getText() + ");\n";

                String es = build(tool, e.getChildren());
                String eh = String.format("<div id=\"%s\" style=\"%s\" %s>%s</div>", id, style, ea, es);
                elements += eh;

                js1 += String.format("pot(\"%s\", %.2f, %.2f, %.2f, %.2f);\n", id, e.getX(), e.getY(), e.getW(), e.getH());
            }
        }

        return elements;
    }

    private String uri(String path)
    {
        return "./" + new File(path).getName();
    }
}
