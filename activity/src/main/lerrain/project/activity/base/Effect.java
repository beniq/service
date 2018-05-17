package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONObject;

public class Effect
{
    String type;

    JSONObject param;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public JSONObject getParam()
    {
        return param;
    }

    public void setParam(JSONObject param)
    {
        this.param = param;
    }
}
