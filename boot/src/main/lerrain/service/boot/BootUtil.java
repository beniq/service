package lerrain.service.boot;

import lerrain.tool.Common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lerrain on 2017/7/17.
 */
public class BootUtil
{
    public static Map<String, String> convertParams(Map<String, Object> src)
    {
        if (src == null)
            return null;

//        System.out.println("src: " + src);

        Map<String, String> r = new HashMap<>();
        for (Map.Entry<String, Object> e : src.entrySet())
        {
            String key = e.getKey();
            Object val = e.getValue();

            if (val == null)
                continue;

            if (val instanceof Map)
            {
                key = Common.isEmpty(key) ? "" : key + ".";
                Map<String, String> map = convertParams((Map<String, Object>)val);
                for (Map.Entry<String, String> c : map.entrySet())
                    r.put(key + c.getKey(), c.getValue());
            }
            else if (key != null)
            {
                r.put(key, val.toString());
            }
        }

//        System.out.println("dst: " + r);

        return r;
    }
}
