package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

public class DocUtil
{
    public static JSONObject toJson(ActivityDoc doc)
    {
        JSONObject j = new JSONObject();
        j.put("actId", doc.getActId());

        JSONArray list = new JSONArray();
        for (Page page : doc.getList())
        {
            JSONObject pageObj = new JSONObject();
            pageObj.put("background", page.getBackground());
            pageObj.put("w", page.getW());
            pageObj.put("h", page.getH());

            JSONArray elements = new JSONArray();
            for (Element element : page.getList())
            {
                JSONObject e = new JSONObject();
                e.put("x", element.getX());
                e.put("y", element.getY());
                e.put("w", element.getW());
                e.put("h", element.getH());
                e.put("image", element.getFile());

                elements.add(e);
            }
            pageObj.put("elements", elements);

            list.add(pageObj);
        }
        j.put("pages", list);

        return j;
    }

    public static ActivityDoc toDoc(JSONObject json)
    {
        ActivityDoc doc = new ActivityDoc();
        doc.setActId(json.getLong("actId"));

        JSONArray list = json.getJSONArray("pages");
        for (int i=0;i<list.size();i++)
        {
            JSONObject pageObj = list.getJSONObject(i);

            Page page = new Page();
            page.setW(pageObj.getIntValue("w"));
            page.setH(pageObj.getIntValue("h"));
            page.setBackground(pageObj.getString("background"));

            JSONArray elements = pageObj.getJSONArray("elements");
            for (int j=0;j<elements.size();j++)
            {
                JSONObject e = elements.getJSONObject(j);

                Element element = new Element();
                element.setFile(e.getString("image"));
                element.setX(e.getFloat("x"));
                element.setY(e.getFloat("y"));
                element.setW(e.getFloat("w"));
                element.setH(e.getFloat("h"));

                page.addElement(element);
            }

            doc.getList().add(page);
        }

        return doc;
    }
}
