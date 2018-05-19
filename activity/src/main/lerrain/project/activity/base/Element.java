package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONArray;

import java.util.*;

public class Element
{
    String id;

    float x, y;
    float w, h;

    int z = 1;

    String bgColor;

    List<Event> events = new ArrayList<>();

    List<String> file = new ArrayList<>();

    List<Element> children = new ArrayList<>();

    JSONArray action = new JSONArray();

    String fontSize;
    String text;
    String color;

    Map style = new HashMap();

    int display = 1;

    public Element()
    {
        setId(UUID.randomUUID().toString());
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        if (id != null)
            this.id = id.replaceAll("[-]", "");;
    }

    public List<String> getFile()
    {
        return file;
    }

    public void setFile(String file)
    {
        this.file.clear();
        this.file.add(file);
    }

    public void addFile(String file)
    {
        this.file.add(file);
    }

    public void setFile(List<String> file)
    {
        this.file = file;
    }

    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public float getW()
    {
        return w;
    }

    public void setW(float w)
    {
        this.w = w;
    }

    public float getH()
    {
        return h;
    }

    public void setH(float h)
    {
        this.h = h;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public String getBgColor()
    {
        return bgColor;
    }

    public void setBgColor(String bgColor)
    {
        this.bgColor = bgColor;
    }

    public List<Element> getChildren()
    {
        return children;
    }

    public void setChildren(List<Element> children)
    {
        this.children = children;
    }

    public JSONArray getAction()
    {
        return action;
    }

    public void setAction(JSONArray action)
    {
        if (action == null)
            action = new JSONArray();
        this.action = action;
    }

    public String getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(String fontSize)
    {
        this.fontSize = fontSize;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public String getColor()
    {
        return color;
    }

    public void setColor(String color)
    {
        this.color = color;
    }

    public void addEvent(Event e)
    {
        e.setElement(this);
        events.add(e);
    }

    public List<Event> getEvents()
    {
        return events;
    }

    public void setEvents(List<Event> events)
    {
        this.events = events;
    }

    public Map getStyle()
    {
        return style;
    }

    public void setStyle(Map style)
    {
        if (style == null)
            style = new HashMap();
        this.style = style;
    }

    public Event findEvent(String eventId)
    {
        for (Event e : this.getEvents())
        {
            if (eventId.equals(e.getId()))
                return e;
        }

        return null;
    }

    public int getDisplay()
    {
        return display;
    }

    public void setDisplay(int display)
    {
        this.display = display;
    }
}
