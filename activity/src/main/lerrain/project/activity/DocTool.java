package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;

import java.util.ArrayList;
import java.util.List;

public class DocTool
{
    public static JSONObject toJson(ActivityDoc doc)
    {
        JSONObject j = new JSONObject();
        j.put("actId", doc.getActId());
        j.put("code", doc.getCode());
        j.put("name", doc.getName());

        JSONArray list = new JSONArray();
        for (Page page : doc.getList())
        {
            JSONObject pageObj = new JSONObject();
            pageObj.put("background", page.getBackground());
            pageObj.put("w", page.getW());
            pageObj.put("h", page.getH());
            pageObj.put("elements", toJson(page.getList(), 0, 0));

            list.add(pageObj);
        }
        j.put("pages", list);

        return j;
    }

    private static JSONArray toJson(List<Element> list, float x, float y)
    {
        JSONArray elements = new JSONArray();

        for (Element element : list)
        {
            JSONObject e = new JSONObject();
            e.put("id", element.getId());
            e.put("x", element.getX());
            e.put("y", element.getY());
            e.put("z", element.getZ());
            e.put("w", element.getW());
            e.put("h", element.getH());
            e.put("image", element.getFile());
            e.put("bgColor", element.getBgColor());
            e.put("action", element.getAction());
            e.put("param", element.getActionParam());
            e.put("color", element.getColor());
            e.put("text", element.getText());
            e.put("fontSize", element.getFontSize());

            e.put("cx", x + element.getX());
            e.put("cy", y + element.getY());

            if (!element.getChildren().isEmpty())
                e.put("children", toJson(element.getChildren(), x + element.getX(), y + element.getY()));

            elements.add(e);
        }

        return elements;
    }

    public static ActivityDoc toDoc(JSONObject json)
    {
        ActivityDoc doc = new ActivityDoc();
        doc.setActId(json.getLong("actId"));
        doc.setName(json.getString("name"));
        doc.setCode(json.getString("code"));

        JSONArray list = json.getJSONArray("pages");
        for (int i=0;i<list.size();i++)
        {
            JSONObject pageObj = list.getJSONObject(i);

            Page page = new Page();
            page.setW(pageObj.getIntValue("w"));
            page.setH(pageObj.getIntValue("h"));
            page.setBackground(pageObj.getString("background"));
            page.setList(toElements(pageObj.getJSONArray("elements")));

            doc.getList().add(page);
        }

        return doc;
    }

    private static List<Element> toElements(JSONArray elements)
    {
        List<Element> list = new ArrayList<>();

        for (int j=0;j<elements.size();j++)
        {
            JSONObject e = elements.getJSONObject(j);

            Element element = new Element();
            element.setId(e.getString("id"));
            element.setX(e.getFloat("x"));
            element.setY(e.getFloat("y"));
            element.setW(e.getFloat("w"));
            element.setH(e.getFloat("h"));
            element.setFile(e.getString("image"));
            element.setBgColor(e.getString("bgColor"));
            element.setAction(e.getString("action"));
            element.setActionParam(e.getString("param"));
            element.setFontSize(e.getString("fontSize"));
            element.setText(e.getString("text"));
            element.setColor(e.getString("color"));

            if (e.containsKey("children"))
                element.setChildren(toElements(e.getJSONArray("children")));

            list.add(element);
        }

        return list;
    }
}
