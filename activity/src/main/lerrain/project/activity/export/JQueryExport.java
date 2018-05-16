package lerrain.project.activity.export;

import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

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
            ph = ph.replace("<!-- ELEMENTS -->", build(p.getList(), id + "_"));

            pages += ph;
        }

        root = root.replace("<!-- TITLE -->", doc.getName());
        root = root.replace("<!-- PAGES -->", pages);
        root = root.replace("<!-- JS_START -->", js1);
        root = root.replace("<!-- JS_ENV -->", js2);
        root = root.replaceAll("[<][!][-][-] ACT_CODE [-][-][>]", doc.getCode());
        root = root.replaceAll("[<][!][-][-] SERVER [-][-][>]", server);

        return root;
    }

    private String build(List<Element> list, String pre)
    {
        String elements = "";

        if (list != null)
        {
            int i = 0;
            for (Element e : list)
            {
                String id = pre + i++;

                String style = "";
                if (e.getFile() != null)
                    style += String.format("background-image:url(%s);", uri(e.getFile()));
                if (e.getFontSize() != null)
                    style += String.format("font-size:%s;", e.getFontSize());
                if (e.getColor() != null)
                    style += String.format("color:#%s;", e.getColor());
                if (e.getBgColor() != null)
                    style += String.format("background-color:#%s;", e.getBgColor());

                String ea = "";
                if (e.getAction() != null)
                {
                    if ("1".equals(e.getAction()))
                    {
                        ea = "onClick=\"click" + id + "()\"";
                        js2 += "var click" + id + " = function(event) { document.location.href='" + e.getActionParam() + "' }\n";
                    }
                    else if ("2".equals(e.getAction()))
                    {
                        ea = "onClick=\"click" + id + "()\"";
                        js2 += "var click" + id + " = function(event) { " + e.getActionParam() + " }\n";
                    }
                }

                if (e.getFontSize() != null && e.getText() != null)
                    js1 += "$(\"#" + id + "\").html(" + e.getText() + ");\n";

                String es = build(e.getChildren(), id + "_");
                String eh = String.format("<div id=\"%s\" style=\"%s\" %s %s></div>", id, style, es, ea);
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
