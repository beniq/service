package lerrain.project.activity.export;

import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

import java.util.List;

public class JQueryExport
{
    public static String export(ActivityDoc doc)
    {
        String root = JQueryTemplate.root;

        String pagesHtml = "";
        for (Page page : doc.getList())
        {
            String ph = JQueryTemplate.page;

            String elementsHtml = "";
            for (Element e : page.getList())
            {
                String eh = JQueryTemplate.element;

                elementsHtml += eh;
            }

            ph = ph.replace("<!-- ELEMENTS -->", elementsHtml);
            pagesHtml += ph;
        }

        root = root.replace("<!-- PAGES -->", pagesHtml);

        return root;
    }

    private static String export(List<Element> elements)
    {
        String elementsHtml = "";
        for (Element e : elements)
        {
            String eh = JQueryTemplate.element;

            String es = export(e.getChildren());

            elementsHtml += eh;
        }

        return elementsHtml;
    }
}
