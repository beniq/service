package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONArray;
import lerrain.project.activity.DocTool;
import lerrain.tool.Common;

import java.util.*;

public class Element
{
    String id;
    String name;

    Element parent;
    Page page;

    float x, y;
    float w, h;

    int z = 1;
    int xs, ys, ws, hs;

    String bgColor;

    String visible;

    List<Event> events = new ArrayList<>();

    List<String> file = new ArrayList<>(); //固定背景

    List<Element> children = new ArrayList<>();

    JSONArray action = new JSONArray();

    String video;

    String fontSize;
    String lineHeight;
    String text;
    String color;

    int align;

    Map style = new HashMap();

    int display = 1;

    String input; //是否为输入框
    Map inputVerify;

    String list; //把该元素复制N份列表排列

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

    public int getAlign()
    {
        return align;
    }

    public void setAlign(int align)
    {
        this.align = align;
    }

    public String getVideo()
    {
        return video;
    }

    public void setVideo(String video)
    {
        this.video = video;
    }

    public String getList()
    {
        return list;
    }

    public void setList(String list)
    {
        this.list = list;
    }

    public String getVisible()
    {
        return visible;
    }

    public void setVisible(String visible)
    {
        if (Common.isEmpty(visible))
            visible = null;
        this.visible = visible;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getInput()
    {
        return input;
    }

    public void setInput(String input)
    {
        this.input = input;
    }

    public Map getInputVerify()
    {
        return inputVerify;
    }

    public void setInputVerify(Map inputVerify)
    {
        this.inputVerify = inputVerify;
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

    public int getXs()
    {
        return xs;
    }

    public void setXs(int xs)
    {
        this.xs = xs;
    }

    public int getYs()
    {
        return ys;
    }

    public void setYs(int ys)
    {
        this.ys = ys;
    }

    public int getWs()
    {
        return ws;
    }

    public void setWs(int ws)
    {
        this.ws = ws;
    }

    public Element getParent()
    {
        return parent;
    }

    public void setParent(Element parent)
    {
        this.parent = parent;
    }

    public Page getPage()
    {
        return page;
    }

    public void setPage(Page page)
    {
        this.page = page;
    }

    public int getHs()
    {
        return hs;
    }

    public void setHs(int hs)
    {
        this.hs = hs;
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

    public String getLineHeight()
    {
        return lineHeight;
    }

    public void setLineHeight(String lineHeight)
    {
        this.lineHeight = lineHeight;
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

    public void addElement(Element e)
    {
        e.setParent(this);
        e.setPage(this.getPage());
        children.add(e);
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

    public Map<String, Object> getStyle()
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

    public float getRealH()
    {
        float h;
        if (this.getHs() == 1) //满屏
            h = DocTool.DEVICE_H;
        else
            h = this.getH();

        return h;
    }

    public float getRealY()
    {
        float y = 0;

        if (this.getYs() == 1) //居中
        {
            if (this.getParent() != null)
                y = (this.getParent().getRealH() - this.getRealH()) / 2;
            else if (this.getPage() != null)
                y = (this.getPage().getH() - this.getRealH()) / 2;
        }
        else if (this.getYs() == 2) //居底
        {
            if (this.getParent() != null)
                y = this.getParent().getRealH() - this.getRealH();
            else if (this.getPage() != null)
                y = this.getPage().getH() - this.getRealH();
        }
        else
            y = this.getY();

        return y;
    }

    public Element copy()
    {
        return copy(page, null, null, null);
    }

    private Element copy(Page p, Element parent, Map<String, String> mm, List<Map> cb)
    {
        Element e = this;
        Element c = new Element();

        c.setId(UUID.randomUUID().toString());
        c.name = e.name;

        c.parent = parent == null ? e.parent : parent;
        c.page = p;

        c.x = e.x;
        c.y = e.y;
        c.z = e.z;
        c.w = e.w;
        c.h = e.h;
        c.xs = e.xs;
        c.ys = e.ys;
        c.ws = e.ws;
        c.hs = e.hs;

        c.bgColor = e.bgColor;
        c.visible = e.visible;

        c.events = new ArrayList<>();
        for (Event ev : e.events)
            c.events.add(ev.copy(c, mm, cb));

        c.file = new ArrayList<>();
        c.file.addAll(e.file);

        c.children = new ArrayList<>();
        for (Element child : e.children)
            c.children.add(child.copy(p, c, mm, cb));

        c.action = DocTool.copy(e.action, cb);

        c.video = e.video;
        c.fontSize = e.fontSize;
        c.lineHeight = e.lineHeight;
        c.text = e.text;
        c.color = e.color;
        c.align = e.align;

        c.style = DocTool.copy(e.style, null);

        c.display = e.display;

        c.input = e.input;
        c.inputVerify = DocTool.copy(e.inputVerify, null);

        c.list = e.list;

        return c;
    }

    public Element copy(Page p, Map<String, String> mm, List<Map> cb)
    {
        return copy(p, null, mm, cb);
    }
}
