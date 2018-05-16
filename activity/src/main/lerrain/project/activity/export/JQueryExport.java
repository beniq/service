package lerrain.project.activity.export;

import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

import java.io.File;
import java.util.List;

public class JQueryExport
{
    String root = JQueryTemplate.root;
    String js = "";
    String pages = "";

    public String export(ActivityDoc doc)
    {
        js += "var xx = document.getElementById(\"ccc\");";

        int i = 0;
        for (Page p : doc.getList())
        {
            String id = "p_" + i++;

            js += String.format("xx.style.height = xx.offsetWidth * %d / %d;", p.getH(), p.getW());

            String ph = JQueryTemplate.page;

            String style = "";
            if (p.getBackground() != null)
                style += String.format("background-image:url(%s);", uri(p.getBackground()));

            ph = ph.replace("<!-- STYLE -->", style);
            ph = ph.replace("<!-- ELEMENTS -->", build(p.getList(), id + "_"));

            pages += ph;
        }

        root = root.replace("<!-- PAGES -->", pages);
        root = root.replace("<!-- JS -->", js);

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

                String ea = "";
                if (e.getAction() != null)
                {
                    if ("1".equals(e.getAction()))
                    {
                        ea = "onClick=\"";
                        ea += "document.location.href='" + e.getActionParam() + "'";
                        ea += "\"";
                    }
                }

                String es = build(e.getChildren(), id + "_");
                String eh = String.format("<div id=\"%s\" style=\"%s\" %s %s></div>", id, style, es, ea);
                elements += eh;

                js += String.format("pot(\"%s\", %.2f, %.2f, %.2f, %.2f);", id, e.getX(), e.getY(), e.getW(), e.getH());
            }
        }

        return elements;
    }

    private String uri(String path)
    {
        return "./" + new File(path).getName();
    }
}
