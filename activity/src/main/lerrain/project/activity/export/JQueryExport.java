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

    String env;
    String server;
    String iybServer;

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
        this.env = env;
        this.tool = new JQueryEvents(this);

        if ("test".equalsIgnoreCase(env))
        {
            server = "https://gpo-test.iyunbao.com";
            iybServer = "https://test.iyunbao.com";
        }
        else if ("uat".equalsIgnoreCase(env))
        {
            server = "https://gpo-uat.iyunbao.com";
            iybServer = "https://uat.iyunbao.com";
        }
        else if ("prd".equalsIgnoreCase(env))
        {
            server = "https://gpo.iyunbao.com";
            iybServer = "https://www.iyunbao.com";
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
            ph = ph.replace("<!-- ELEMENTS -->", build(p.getList(), null));

            pages += ph;
        }

        js2 += tool.envJs;

        for (String cssStr : xcss.values())
            css += cssStr;

        root = root.replaceAll("<!-- ENV -->", env);
        root = root.replace("<!-- TITLE -->", doc.getName());
        root = root.replace("<!-- PAGES -->", pages);
        root = root.replaceAll("<!-- HEIGHT -->", doc.getList().get(0).getH() + "");
        root = root.replace("<!-- JS_START -->", js1);
        root = root.replace("<!-- JS_ENV -->", js2);
        root = root.replace("<!-- JS_TEXT -->", js3);
        root = root.replace("<!-- CSS -->", css + tool.envCss);
        root = root.replaceAll("<!-- ACT_CODE -->", doc.getCode());
        root = root.replaceAll("<!-- SERVER -->", server);
        root = root.replaceAll("<!-- IYB_SERVER -->", iybServer);
        root = root.replaceAll("<!-- IMAGES -->", images);

        for (Runnable r : finish)
            r.run();

        return root;
    }

    private String build(List<Element> list, String jsExp)
    {
        String elements = "";

        if (list != null)
        {
            for (Element e : list)
            {
                if (Common.isEmpty(e.getList()))
                {
                    elements += stringOf(e, jsExp);
                }
                else
                {
                    js3 += "ENV.LIST" + e.getId() + " = " + e.getList() + ";";

                    List<String> ids = new ArrayList<>();
                    for (int i=0;i<10;i++)
                    {
                        Element ee = e.copy();
                        ee.setY(e.getH() * i);
                        ee.getStyle().put("hide", null);

                        elements += stringOf(ee, "(ENV.LIST" + e.getId() + "&&ENV.LIST" + e.getId() + ".length > "+i+" ?" + "ENV.LIST" + e.getId() + "[" + i + "] : {})");
                        ids.add(ee.getId());
                    }

                    js3 += "var IDS" + e.getId() + " = " + JSON.toJSONString(ids) + ";";
                    js3 += String.format("if (ENV.LIST%s) for (var i=0;i<ENV.LIST%s.length;i++) { $('#' + IDS%s[i]).show(); }", e.getId(), e.getId(), e.getId());
                }
            }
        }

        return elements;
    }

    private String stringOf(Element e, String exp)
    {
        final String id = e.getId();

        String pos = "null";
        if (e.getStyle() != null && e.getStyle().get("fixed") != null)
            pos = "'fixed'";
        float ry = (e.getYs() == 1 ? -10000 : e.getYs() == 2 ? -20000 : e.getY());
        js1 += String.format("pot(\"%s\", %.4f, %.4f, %.4f, %.4f, %s);\n", id, e.getX(), ry, e.getW(), (e.getHs() == 1 ? -1 : e.getH()), pos);

        if (e.getFontSize() != null && e.getText() != null)
        {
            js3 += "var TX" + id + "=" + expOf(e.getText(), exp) + ";";
            js3 += "if (TX" + id + " instanceof Array) { ";
            js3 += "var text='';";
            js3 += "for (var i=0;i<9;i++) {";
            js3 += "text+='<div>'+TX"+id+"[i % TX" + id + ".length]+'</div>'; }";
            js3 += "text+='<div>'+TX"+id+"[0]+'</div>';";
            js3 += "$('#" + id + "').html('<div class=\"ani_scroll\">'+text+'</div>'); }";
            js3 += "else { $('#" + id + "').html('<div>'+TX" + id + "+'</div>'); }\n";
        }

        String es = build(e.getChildren(), exp);

        String style = "";
        String className = "";
        if (e.getFile().size() > 0)
            style += String.format("background-image:url(%s);", uri(e.getFile().get(0)));
        if (e.getFontSize() != null)
        {
            int lh = Common.intOf(e.getLineHeight(), 0);
            js1 += "fnt('" + id + "'," + e.getFontSize()+ "," + (lh <= 0 ? e.getFontSize() : lh) + ");";
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
            JSONObject val = et.getValue() instanceof Map ? (JSONObject)JSON.toJSON(et.getValue()) : null;

            if ("hide".equals(key))
                style += "display:none; overflow:hidden;";
            if ("scroll".equals(key) || "autoScroll".equals(key))
                style += "overflow-y:scroll;";

            if (key.startsWith("shake") || "rotate".equals(key) || "float".equals(key))
            {
                double begin = val == null ? 0 : Common.doubleOf(val.get("begin"), 0);
                if (begin > 0)
                    js1 += "setTimeout(function() { $('#" + id + "').removeClass(); $('#" + id + "').addClass('ani_" + key + "'); }, " + begin * 1000 + ");";
                else
                    className += " ani_" + key;
            }

            if ("bgSwitch".equals(key))
            {
                String bg = null;
                for (String f : e.getFile())
                    bg = (bg == null ? "" : bg + ",") + "\"" + uri(f) + "\"";
                js3 += "var bg = ["+bg+"][" + val.getString("index") + "];";
                js3 += "$('#" + id + "').css('background-image', 'url(' + bg + ')');";
            }

            if ("bgUrl".equals(key))
            {
                js3 += "$('#" + id + "').css('background-image', 'url(' + " + expOf(val.getString("url"), exp) + " + ')');";
            }

            if ("textin".equals(key))
            {
                int d = Common.intOf(val.get("direct"), 1);
                double s = Common.doubleOf(val.get("begin"), 0);
                String spd = val.getString("spd");
                if (Common.isEmpty(spd)) spd = "ease-out";

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
                ncss = ncss.replaceAll("<!-- SPEED -->", spd);

                css += ncss;
                className += " text_in" + id;
            }

            if ("popup".equals(key))
            {
                css += JQueryExport.popupCss;
                className += " plat10_xz";
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

        if (e.getAlign() == 1 || e.getAlign() == 4 || e.getAlign() == 7)
            style += "text-align:left;";
        else if (e.getAlign() == 3 || e.getAlign() == 6 || e.getAlign() == 9)
            style += "text-align:right;";
        else
            style += "text-align:center;";

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
            js3 += "if (" + expOf(e.getVisible(), exp) + ") { $(\"#" + id + "\").show(); }";
        }

        if (!Common.isEmpty(e.getVideo()))
        {
            String v = e.getVideo();
//            if (v.startsWith("http:") || v.startsWith("https:"))
//                v = "\"" + v + "\"";
//            es += "<video src="+v+" style='width:100%;height:100%'></video>";
            es += v;
        }

        String ea = "";
        if (e.getAction().size() > 0)
        {
            String ec = "";
            for (int i=0;i<e.getAction().size();i++)
            {
                JSONObject act = e.getAction().getJSONObject(i);
                String type = act.getString("type");

//                System.out.println(id);
//                System.out.println(act);

                if ("redirect".equals(type)) //作废
                {
                    ec += "document.location.href = " + act.getString("param") + ";\n";
                }
                else if ("submit".equals(type)) //作废
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
                        str = tool.getJs(event, exp);
                    }
                    ec += "<!-- SUBMIT" + id + " --> gpo.ask('submit', formInput, function(RES) { if (RES.flag == 'success') { "+str+" } else { Life.Dialog.alert(RES.reason) } });";
                }
                else if ("event".equals(type))
                {
                    String eventId = act.getString("param");
                    Event event = tool.doc.findEvent(eventId);
                    ec += tool.getJs(event, exp);
                }
            }

            js2 += "var click" + id + " = function(EVENT) {" + ec + "};";
            ea = "onClick=\"click" + id + "()\"";
        }

        return String.format("<div id=\"%s\" class=\"%s\" style=\"%s\" %s>%s</div>", id, className, style, ea, es);
    }

    protected String expOf(String exp, String pExp)
    {
        if (pExp == null || exp == null)
            return exp;

        return exp.replaceAll("SELF", pExp);
    }

    protected String uri(String path)
    {
        if (path == null) return null;
        String uri = "./" + new File(path).getName();
        images = (images == null ? "" : images + ",") + "\"" + uri + "\"";
        return uri;
    }
}
