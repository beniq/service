package lerrain.project.activity.base;

import java.util.ArrayList;
import java.util.List;

public class ActivityDoc
{
    Long actId;

    String code;
    String name;

    List<Page> list = new ArrayList<>();

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

}
