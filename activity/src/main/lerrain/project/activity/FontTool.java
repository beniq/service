package lerrain.project.activity;

import lerrain.tool.Common;
import lerrain.tool.Disk;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class FontTool {
    private static String ROOT = "./static";
    private static Map<String, Font> m = new HashMap<String, Font>();

    @PostConstruct
    public void init(){
        Map<String, String> kv = new HashMap<>();
        kv.put("msyh,微软雅黑", "fonts/msyh.ttf");
        kv.put("msyhbd", "fonts/msyhbd.ttf");

        System.out.println("init font begin......");
        for(String key : kv.keySet()){
            if(!loadFont(key, kv.get(key)))
                if(!loadFont(key, kv.get(key)))
                    if(!loadFont(key, kv.get(key)))
                        break;
        }
        System.out.println("init font end......");
    }

    /**
     * 加载字体
     * @param fontType
     * @param fileName
     * @return
     */
    private boolean loadFont(String fontType, String fileName){
        try {
            if(!Disk.fileOf(ROOT, fileName).exists()){
                return false;
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, Disk.fileOf(ROOT, fileName));
            if(font == null){
                return false;
            }
            for(String key : fontType.split(","))
                if(!Common.isEmpty(key))
                    m.put(key, font);

            return true;
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取字体，若没有指定的，则返回默认字体
     * @param fontType 字体类型（msyh|微软雅黑，msyhbd）
     * @param def 默认字体
     * @return
     */
    public static Font getFont(String fontType, Font def){
        Font font = m.get(fontType);

        if(font == null){
            font = def;
        }

        return font;
    }

}
