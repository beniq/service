package lerrain.project.activity.base;

import java.util.ArrayList;
import java.util.List;

public class ActivityDoc
{
    Long actId;

    List<Page> list = new ArrayList<>();

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
}
