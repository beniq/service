package lerrain.project.activity.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Page
{
    public static final int MODE_NORMAL  = 1; //常规，横向铺满，纵向可滑动
    public static final int MODE_FIXED   = 2; //固定，

    int w = 750, h = 1200;

    String background;

    int mode = 1;

    List<Element> list = new ArrayList<>();

    public void addElement(Element e)
    {
        e.setPage(this);
        list.add(e);
    }

    public String getBackground()
    {
        return background;
    }

    public void setBackground(String background)
    {
        this.background = background;
    }

    public int getMode()
    {
        return mode;
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public List<Element> getList()
    {
        return list;
    }

    public void setList(List<Element> list)
    {
        this.list = list;
    }

    public int getW()
    {
        return w;
    }

    public void setW(int w)
    {
        this.w = w;
    }

    public int getH()
    {
        if (mode == 2)
            h = 1400;

        return h;
    }

    public void setH(int h)
    {
        this.h = h;
    }

    public Element find(String elementId)
    {
        return find(list, elementId);
    }

    public Element remove(String elementId)
    {
        return remove(list, elementId);
    }

    private Element find(List<Element> list, String elementId)
    {
        for (Element e : list)
        {
            if (elementId.equals(e.getId()))
                return e;

            Element f = find(e.getChildren(), elementId);
            if (f != null)
                return f;
        }

        return null;
    }

    private Element remove(List<Element> list, String elementId)
    {
        for (Element e : list)
        {
            if (elementId.equals(e.getId()))
            {
                list.remove(e);
                return e;
            }

            Element f = remove(e.getChildren(), elementId);
            if (f != null)
                return f;
        }

        return null;
    }

    public Event findEvent(String eventId)
    {
        return findEvent(list, eventId);
    }

    private Event findEvent(List<Element> list, String eventId)
    {
        for (Element e : list)
        {
            Event ev = e.findEvent(eventId);
            if (ev != null)
                return ev;

            ev = findEvent(e.getChildren(), eventId);
            if (ev != null)
                return ev;
        }

        return null;
    }

    public Page copy()
    {
        Page p = new Page();

        p.w = this.w;
        p.h = this.h;
        p.background = this.background;
        p.mode = this.mode;

        Map<String, String> mm = new HashMap<>();
        List<Map> cb = new ArrayList<>();

        for (Element e : this.list)
            p.addElement(e.copy(p, mm, cb));

        for (Map m : cb)
            m.put("eventId", mm.get(m.get("eventId")));

        return p;
    }
}
