package lerrain.project.activity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Event;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Page;
import lerrain.tool.Common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocTool
{
    public static final int DEVICE_H = 1200;

    public static JSONObject toJson(ActivityDoc doc)
    {
        JSONObject j = new JSONObject();
        j.put("actId", doc.getActId());
        j.put("code", doc.getCode());
        j.put("name", doc.getName());
        j.put("files", doc.getFiles());

        JSONArray list = new JSONArray();
        for (Page page : doc.getList())
        {
            JSONObject pageObj = new JSONObject();
            pageObj.put("background", page.getBackground());
            pageObj.put("w", page.getW());
            pageObj.put("h", page.getH());
            pageObj.put("mode", page.getMode());
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
            e.put("h", element.getRealH());
            e.put("ys", element.getYs());
            e.put("hs", element.getHs());
            e.put("name", element.getName());
            e.put("display", element.getDisplay());
            e.put("image", element.getFile());
            e.put("bgColor", element.getBgColor());
            e.put("action", element.getAction());
            e.put("color", element.getColor());
            e.put("text", element.getText());
            e.put("fontSize", element.getFontSize());
            e.put("lineHeight", element.getLineHeight());
            e.put("align", element.getAlign());
            e.put("style", element.getStyle());
            e.put("input", element.getInput());
            e.put("inputVerify", element.getInputVerify());
            e.put("visible", element.getVisible());
            e.put("list", element.getList());
            e.put("video", element.getVideo());

            e.put("cx", x + element.getX());
            e.put("cy", y + element.getRealY());

            if (element.getChildren() != null && !element.getChildren().isEmpty())
                e.put("children", toJson(element.getChildren(), x + element.getX(), y + element.getRealY()));

            if (element.getEvents() != null && !element.getEvents().isEmpty())
                e.put("events", toJson(element.getEvents()));

            elements.add(e);
        }

        return elements;
    }

    private static JSONArray toJson(List<Event> list)
    {
        JSONArray effects = new JSONArray();

        for (Event e : list)
        {
            JSONObject effect = new JSONObject();
            effect.put("id", e.getId());
            effect.put("type", e.getType());
            effect.put("param", e.getParam());
            effect.put("onFinish", e.getFinish());

            effects.add(effect);
        }

        return effects;
    }

    public static ActivityDoc toDoc(JSONObject json)
    {
        ActivityDoc doc = new ActivityDoc();
        doc.setActId(json.getLong("actId"));
        doc.setName(json.getString("name"));
        doc.setCode(json.getString("code"));
        doc.setFiles(json.getJSONObject("files"));

        JSONArray list = json.getJSONArray("pages");
        for (int i=0;i<list.size();i++)
        {
            JSONObject pageObj = list.getJSONObject(i);

            Page page = new Page();
            page.setW(pageObj.getIntValue("w"));
            page.setH(pageObj.getIntValue("h"));
            page.setMode(pageObj.getIntValue("mode"));
            page.setBackground(pageObj.getString("background"));
            page.setList(toElements(page, null, pageObj.getJSONArray("elements")));

            doc.getList().add(page);
        }

        return doc;
    }

    private static List<Element> toElements(Page page, Element parent, JSONArray elements)
    {
        List<Element> list = new ArrayList<>();

        for (int j=0;j<elements.size();j++)
        {
            JSONObject e = elements.getJSONObject(j);

            Element element = new Element();
            element.setPage(page);
            element.setParent(parent);
            element.setId(e.getString("id"));
            element.setX(e.getFloat("x"));
            element.setY(e.getFloat("y"));
            element.setZ(Common.intOf(e.get("z"), 1));
            element.setW(e.getFloat("w"));
            element.setH(e.getFloat("h"));
            element.setYs(Common.intOf(e.get("ys"), 0));
            element.setHs(Common.intOf(e.get("hs"), 0));
            element.setName(e.getString("name"));
            element.setDisplay(Common.intOf(e.getInteger("display"), 1));
            element.setBgColor(e.getString("bgColor"));
            try
            {
                element.setAction(e.getJSONArray("action"));
            }
            catch (Exception e1)
            {
            }
            element.setFontSize(e.getString("fontSize"));
            element.setLineHeight(e.getString("lineHeight"));
            element.setAlign(Common.intOf(e.get("align"), 5));
            element.setText(e.getString("text"));
            element.setColor(e.getString("color"));
            element.setStyle(e.getJSONObject("style"));
            element.setVisible(e.getString("visible"));
            element.setInput(e.getString("input"));
            element.setInputVerify(e.getJSONObject("inputVerify"));
            element.setList(e.getString("list"));
            element.setVideo(e.getString("video"));

            if (e.containsKey("image"))
            {
                element.getFile().clear();
                JSONArray ja = e.getJSONArray("image");
                for (int k=0;k<ja.size();k++)
                    element.getFile().add(ja.getString(k));
            }

            if (e.containsKey("children"))
                element.setChildren(toElements(page, element, e.getJSONArray("children")));

            if (e.containsKey("events"))
                element.setEvents(toEvents(element, e.getJSONArray("events")));

            list.add(element);
        }

        return list;
    }

    private static List<Event> toEvents(Element element, JSONArray events)
    {
        List<Event> r = new ArrayList<>();

        for (int i = 0; i < events.size(); i++)
        {
            JSONObject effect = events.getJSONObject(i);

            Event e = new Event();
            e.setId(effect.getString("id"));
            e.setType(effect.getString("type"));
            e.setParam(effect.getJSONObject("param"));
            e.setFinish(effect.getJSONObject("onFinish"));
            e.setElement(element);

            r.add(e);
        }

        return r;
    }

    public static JSONArray copy(List list, List<Map> resetEvent)
    {
        if (list == null)
            return null;

        JSONArray r = new JSONArray();
        for (Object o : list)
        {
            if (o instanceof Map)
                r.add(copy((Map<String, Object>)o, resetEvent));
            else if (o instanceof List)
                r.add(copy((List)o, resetEvent));
            else
                r.add(o);
        }
        return r;
    }

//    public static JSONObject copy(Map<String, Object> map)
//    {
//        return copy(map, null);
//    }

    public static JSONObject copy(Map<String, Object> map, List<Map> resetEvent)
    {
        if (map == null)
            return null;

        JSONObject m = new JSONObject();
        for (Map.Entry<String, Object> e : map.entrySet())
        {
            if (e.getValue() instanceof Map)
            {
                m.put(e.getKey(), copy((Map<String, Object>)e.getValue(), resetEvent));
            }
            else if (e.getValue() instanceof List)
            {
                m.put(e.getKey(), copy((List)e.getValue(), resetEvent));
            }
            else
            {
                m.put(e.getKey(), e.getValue());

                if ("eventId".equals(e.getKey()) && resetEvent != null)
                    resetEvent.add(m);
            }
        }

        return m;
    }
}
