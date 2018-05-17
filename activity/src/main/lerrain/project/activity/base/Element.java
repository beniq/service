package lerrain.project.activity.base;

import com.alibaba.fastjson.JSONObject;

import java.util.*;

public class Element
{
    String id;

    float x, y;
    float w, h;

    int z = 1;

    String bgColor;

    List<Effect> effects;

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
        id = UUID.randomUUID().toString();
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        if (id != null)
            this.id = id;
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

    public List<Effect> getEffects()
    {
        return effects;
    }

    public void setEffects(List<Effect> effects)
    {
        this.effects = effects;
    }

    public Map getStyle()
    {
        return style;
    }

    public void setStyle(Map style)
    {
        this.style = style;
    }
}
