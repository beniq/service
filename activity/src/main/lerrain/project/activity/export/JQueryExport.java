package lerrain.project.activity.export;

import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

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
}
