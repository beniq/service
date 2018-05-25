package lerrain.project.activity.export;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lerrain.project.activity.base.ActivityDoc;
import lerrain.project.activity.base.Element;
import lerrain.project.activity.base.Event;
import lerrain.project.activity.base.Page;
import lerrain.tool.Common;

import java.io.File;
import java.util.*;

public class JQueryExport
{
    public static String rootHtml;
    public static String pageHtml;
    public static String popupCss;
    public static String playCss;
    public static String starCss;
    public static String textCss;

    ActivityDoc doc;

    JQueryEvents tool;

    String server;

    String root = JQueryExport.rootHtml;
    String js1 = "", js2 = "", js3 = "";
    String pages = "";
    String css = "";
    String images = null;

    Map<String, Map> input = new LinkedHashMap<>();
    Map<String, String> xcss = new HashMap();

    List<Runnable> finish = new ArrayList<>();

    public JQueryExport(ActivityDoc doc, String env)
    {
        this.doc = doc;

        this.tool = new JQueryEvents(this);

        if ("test".equalsIgnoreCase(env))
        {
            server = "https://gpo-test.iyunbao.com";
        }
        else if ("uat".equalsIgnoreCase(env))
        {
            server = "https://gpo-uat.iyunbao.com";
        }
        else if ("prd".equalsIgnoreCase(env))
        {
            server = "https://gpo.iyunbao.com";
        }
    }

    public String export()
    {
        js1 += "var xx = document.getElementById(\"ccc\");";

        for (Page p : doc.getList())
        {
            js1 += String.format("xx.style.height = xx.offsetWidth * %d / %d;", p.getH(), p.getW());

            String ph = JQueryExport.pageHtml;

            String style = "";
            if (p.getBackground() != null)
                style += String.format("background-image:url(%s);", uri(p.getBackground()));

            ph = ph.replace("<!-- STYLE -->", style);
            ph = ph.replace("<!-- ELEMENTS -->", build(p.getList()));

            pages += ph;
        }

        js2 += tool.envJs;

        for (String cssStr : xcss.values())
            css += cssStr;

        root = root.replace("<!-- TITLE -->", doc.getName());
        root = root.replace("<!-- PAGES -->", pages);
        root = root.replaceAll("<!-- HEIGHT -->", doc.getList().get(0).getH() + "");
        root = root.replace("<!-- JS_START -->", js1);
        root = root.replace("<!-- JS_ENV -->", js2);
        root = root.replace("<!-- JS_TEXT -->", js3);
        root = root.replace("<!-- CSS -->", css + tool.envCss);
        root = root.replaceAll("<!-- ACT_CODE -->", doc.getCode());
        root = root.replaceAll("<!-- SERVER -->", server);
        root = root.replaceAll("<!-- IMAGES -->", images);

        for (Runnable r : finish)
            r.run();

        return root;
    }

    private String build(List<Element> list)
    {
        String elements = "";

        if (list != null)
        {
            for (Element e : list)
            {
                final String id = e.getId();

                String pos = "null";
                if (e.getStyle() != null && e.getStyle().get("fixed") != null)
                    pos = "'fixed'";
                js1 += String.format("pot(\"%s\", %.4f, %.4f, %.4f, %.4f, %s);\n", id, e.getX(), (e.getYs() == 1 ? -10000 : e.getYs() == 2 ? -20000 : e.getY()), e.getW(), (e.getHs() == 1 ? -1 : e.getH()), pos);

                String es = build(e.getChildren());

                String style = "";
                String className = "";
                if (e.getFile().size() > 0)
                    style += String.format("background-image:url(%s);", uri(e.getFile().get(0)));
                if (e.getFontSize() != null)
                {
                    style += "text-align:center;";
                    js1 += "fnt('" + id + "'," + e.getH() * 2 / 3 + "," + e.getH() + ");";
                }
                if (e.getColor() != null)
                    style += String.format("color:%s;", e.getColor());
                if (e.getBgColor() != null)
                    style += String.format("background-color:%s;", e.getBgColor());
                if (e.getZ() > 0)
                    style += String.format("z-index:%d;", e.getZ());
                if (e.getStyle() != null) for (Map.Entry<String, Object> et : e.getStyle().entrySet())
                {
                    String key = et.getKey();
                    Object value = et.getValue();

                    if ("hide".equals(key))
                        style += "display:none; overflow:hidden;";

                    if ("shake1".equals(key))
                        className += "ani_shake1";
                    if ("shake2".equals(key))
                        className += "ani_shake2";
                    if ("rotate".equals(key))
                        className += "ani_rotate";

                    if ("textin".equals(key))
                    {
                        JSONObject val = (JSONObject)JSON.toJSON(value);

                        int d = Common.intOf(val.get("direct"), 1);
                        double s = Common.doubleOf(val.get("begin"), 0);

                        int x = 0, y = 0;
                        if (d == 1)
                        {
                            x = -Math.round(75000 / e.getW());
                            y = 0;
                        }
                        else if (d == 2)
                        {
                            x = 0;
                            y = -Math.round(75000 / e.getW());
                        }
                        else if (d == 3)
                        {
                            x = Math.round(75000 / e.getW());
                            y = 0;
                        }
                        else if (d == 4)
                        {
                            x = 0;
                            y = Math.round(75000 / e.getW());
                        }

                        String ncss = textCss.replaceAll("<!-- ID -->", id);
                        ncss = ncss.replaceAll("<!-- X -->",  x + "%");
                        ncss = ncss.replaceAll("<!-- Y -->", y + "%");
                        ncss = ncss.replaceAll("<!-- START -->", String.format("%.4f", s));

                        css += ncss;
                        className += "text_in" + id;
                    }

                    if ("popup".equals(key))
                    {
                        css += JQueryExport.popupCss;
                        className += "plat10_xz";
                    }

                    if ("canvas".equals(key))
                        es = "<canvas id='CV" + id + "' style='width:100%;height:100%;z-index:100'></canvas>" + es;

                    if ("alpha50".equals(key))
                        style += "opacity:0.5;";
                    if ("alpha".equals(key))
                        style += "opacity:"+(Common.doubleOf(e.getStyle().get("alpha"), 1))+";";

                    if ("stars".equals(key))
                    {
                        xcss.put("star", JQueryExport.starCss);
                        int sq = (int)(e.getW()*e.getH());
                        int size = sq > 250000 ? 100 : (int)(Math.sqrt(sq) / 5);
                        int num = (int)(e.getW()*e.getH()/size/size/2.2f);
                        for (int k=0;k<num;k++)
                            es += "<img class=\"plat10_xx"+(k%4+1)+"\" style=\"z-index: 0;\" id=\""+id+"_"+k+"\">\n";
                        js1 += "star1('"+id+"',"+num+","+e.getW()+","+e.getH()+","+size+");";
                    }
                }

                if (!Common.isEmpty(e.getInput()))
                {
                    String inputId = Common.trimStringOf(e.getInput());
                    es += "<input id='INPUT" + inputId + "'/>";
                    js3 += "$('#INPUT" + inputId + "').val(ENV.init." + inputId + ");";
                    input.put(inputId, e.getInputVerify());
                }

                //生成所有事件的js
                List<Event> evs = e.getEvents();
                for (Event ev : evs)
                {
                    tool.initiate(ev);

                    if ("play".equals(ev.getType()))
                        className += " play" + ev.getId();
                }

                if (e.getVisible() != null)
                {
                    style += "display:none;";
                    js3 += "if (" + e.getVisible() + ") { $(\"#" + id + "\").show(); }";
                }

                String ea = "";
                if (e.getAction().size() > 0)
                {
                    String ec = "";
                    for (int i=0;i<e.getAction().size();i++)
                    {
                        JSONObject act = e.getAction().getJSONObject(i);
                        String type = act.getString("type");
                        if ("redirect".equals(type))
                        {
                            ec += "document.location.href = " + act.getString("param") + ";\n";
                        }
                        else if ("submit".equals(type))
                        {
                            finish.add(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    String ec = "var formInput = {";
                                    for (String str : input.keySet())
                                        ec += str + ": document.getElementById('INPUT" + str + "').value,";
                                    ec += "formTag: null };";

                                    for (String str : input.keySet())
                                    {
                                        Map verify = input.get(str);
                                        if (verify != null && Common.boolOf(verify.get("require"), false))
                                            ec += "if (formInput." + str + " == '') { Life.Dialog.alert('部分字段必填，请补充'); return; }";
                                    }

                                    root = root.replace("<!-- SUBMIT" + id + " -->", ec);
                                }
                            });

                            String eventId = act.getString("param");
                            String str = "";
                            if (eventId != null)
                            {
                                Event event = tool.doc.findEvent(eventId);
                                str = tool.getJs(event);
                            }
                            ec += "<!-- SUBMIT" + id + " --> gpo.ask('submit', formInput, function(RES) { if (RES.flag == 'success') { "+str+" } else { Life.Dialog.alert(RES.reason) } });";
                        }
                        else if ("event".equals(type))
                        {
                            String eventId = act.getString("param");
                            Event event = tool.doc.findEvent(eventId);
                            ec += tool.getJs(event);
                        }
                    }

                    js2 += "var click" + id + " = function(EVENT) {" + ec + "};";
                    ea = "onClick=\"click" + id + "()\"";
                }

                if (e.getFontSize() != null && e.getText() != null)
                    js3 += "$(\"#" + id + "\").html(" + e.getText() + ");\n";

                String eh = String.format("<div id=\"%s\" class=\"%s\" style=\"%s\" %s>%s</div>", id, className, style, ea, es);
                elements += eh;
            }
        }

        return elements;
    }

    private String uri(String path)
    {
        if (path == null) return null;
        String uri = "./" + new File(path).getName();
        images = (images == null ? "" : images + ",") + "\"" + uri + "\"";
        return uri;
    }
}
