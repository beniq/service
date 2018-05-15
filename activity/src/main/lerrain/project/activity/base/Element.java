package lerrain.project.activity.base;

import java.util.List;

public class Element
{
    float x, y;
    float w, h;

    int z = 1;

    String bgColor;

    List<Object> effects;

    String file;

    List<Element> children;

    String action;
    String actionParam;

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
}
