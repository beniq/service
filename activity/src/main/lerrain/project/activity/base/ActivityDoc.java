package lerrain.project.activity.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityDoc
{
    Long actId;

    String code;
    String name;

    List<Page> list = new ArrayList<>();

    Map<String, Object> files = new HashMap(); //压缩后的文件与源文件的mapping

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Long getActId()
    {
        return actId;
    }

    public void setActId(Long actId)
    {
        this.actId = actId;
    }

    public List<Page> getList()
    {
        return list;
    }

    public void setList(List<Page> list)
    {
        this.list = list;
    }

    public Element find(String elementId)
    {
        for (Page p : list)
        {
            Element f = p.find(elementId);
            if (f != null)
                return f;
        }

        return null;
    }

    public Event findEvent(String eventId)
    {
        for (Page p : list)
        {
            Event ev = p.findEvent(eventId);
            if (ev != null)
                return ev;
        }

        return null;
    }

    public Map<String, Object> getFiles()
    {
        return files;
    }

    public void setFiles(Map<String, Object> files)
    {
        if (files == null)
            files = new HashMap<>();
        this.files = files;
    }
}
