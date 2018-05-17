package lerrain.project.activity.base;

import java.util.*;

public class Element
{
    String id;

    float x, y;
    float w, h;

    int z = 1;

    String bgColor;

    List<Event> events = new ArrayList<>();

    String file;

    List<Element> children = new ArrayList<>();

    String action;
    String actionParam;

    String fontSize;
    String text;
    String color;

    Map style = new HashMap();

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

    public String getFile()
    {
        return file;
    }

    public void setFile(String file)
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

    public String getAction()
    {
        return action;
    }

    public void setAction(String action)
    {
        this.action = action;
    }

    public String getActionParam()
    {
        return actionParam;
    }

    public void setActionParam(String actionParam)
    {
        this.actionParam = actionParam;
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
}
