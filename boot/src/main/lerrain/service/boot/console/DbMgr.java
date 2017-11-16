package lerrain.service.boot.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lerrain.service.boot.ServiceMgr;
import lerrain.tool.Common;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by lerrain on 2017/7/31.
 */
public class DbMgr
{
    JSONObject db1, db2;
    JSONObject rule;

    int index = 0;

    JSONArray r;

    String uuid;

    public DbMgr(JSONObject rule, JSONObject db1, JSONObject db2)
    {
        this.db1 = db1;
        this.db2 = db2;
        this.rule = rule;

        uuid = UUID.randomUUID().toString();
    }

    public String getId()
    {
        return uuid;
    }

    public boolean isInst(String uuid)
    {
        return uuid.equals(uuid);
    }

    public List<String> run(JSONArray skip)
    {
        List<String> res = new ArrayList<>();

        Map<Integer, String> sqls = new LinkedHashMap<>();
        for (int i=0;i<r.size();i++)
        {
            JSONObject r1 = r.getJSONObject(i);
            JSONArray l2 = r1.getJSONArray("sqls");
            for (int j=0;j<l2.size();j++)
            {
                JSONObject r2 = l2.getJSONObject(j);
                sqls.put(r2.getInteger("id"), r2.getString("sql"));
            }
        }

        if (skip != null) for (int i=0;i<skip.size();i++)
        {
            Integer id = skip.getInteger(i);
            res.add(sqls.remove(id));
        }

        System.out.println(Common.getTimeString(new Date()) + " synch db: " + db2);

        try (Connection conn2 = DriverManager.getConnection(db2.getString("url"), db2.getString("username"), db2.getString("password"));
             Statement s2 = conn2.createStatement();)
        {
            for (String sql : sqls.values())
            {
                System.out.println(sql);
                if (s2.executeUpdate(sql) != 1)
                {
                    System.out.println("fail **********************************");
                    res.add(sql);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        }

        return res;
    }

    public JSONArray pretreat()
    {
        index = 0;
        r = new JSONArray();

        JSONArray tables = rule.getJSONArray("table");
        for (int i=0;i<tables.size();i++)
        {
            JSONObject table = tables.getJSONObject(i);
            List<String> sqls = pretreatTable(db1, db2, table);
            JSONArray dt = new JSONArray();

            for (String sql : sqls)
            {
                JSONObject ll = new JSONObject();
                ll.put("id", index++);
                ll.put("sql", sql);
                dt.add(ll);
            }

            if (!dt.isEmpty())
            {
                JSONObject l = new JSONObject();
                l.put("name", table.getString("name"));
                l.put("sqls", dt);
                r.add(l);
            }
        }

        return r;
    }

    public List<String> pretreatTable(JSONObject db, JSONObject db2, JSONObject table)
    {
        String tableName = table.getString("name");
        JSONArray keys = table.getJSONArray("key");
        String condition = table.getString("condition");
        String excludeStr = table.getString("exclude");

        String[] exclude = Common.isEmpty(excludeStr) ? null : excludeStr.split(",");

        if (Common.isEmpty(condition))
            condition = "1=1";

        String keyStr = null;
        for (int i=0;i<keys.size();i++)
        {
            keyStr = keyStr == null ? keys.getString(i) : keyStr + ", " + keys.getString(i);
        }

        String sql1 = String.format("select * from %s where %s order by %s", tableName, condition, keyStr);

        StringBuffer sql2 = new StringBuffer("select * from ");
        sql2.append(tableName);
        for (int i=0;i<keys.size();i++)
        {
            sql2.append(i == 0 ? " where " : " and ");
            sql2.append(keys.getString(i) + " = %s");
        }

        return query(db.getString("url"), db.getString("username"), db.getString("password"), sql1,
                db2.getString("url"), db2.getString("username"), db2.getString("password"), sql2.toString(), tableName, keys, exclude);
    }

    private List<String> query(String url1, String user1, String pwd1, String sql1, String url2, String user2, String pwd2, String sql2, String tabName, JSONArray keys, String[] exclude)
    {
        List<String> sqls = new ArrayList<>();

        try (Connection conn1 = DriverManager.getConnection(url1, user1, pwd1);
             Connection conn2 = DriverManager.getConnection(url2, user2, pwd2);
             Statement s1 = conn1.createStatement();
             Statement s2 = conn2.createStatement();)
        {
            ResultSet rs = s1.executeQuery(sql1);
            while (rs.next())
            {
                JSONObject src = getAll(rs);

                Object[] params = new Object[keys.size()];
                for (int i=0;i<keys.size();i++)
                    params[i] = strOf(src.get(keys.getString(i)));

                JSONObject diff = new JSONObject();

                int c = 0;
                ResultSet line = s2.executeQuery(String.format(sql2, params));
                while (line.next())
                {
                    if (c > 0)
                        throw new RuntimeException(tabName + "/" + keys + "/" + src.toString());

                    JSONObject dst = getAll(line);

                    for (Map.Entry<String, Object> e : src.entrySet())
                    {
                        boolean pass = false;
                        if (exclude != null)
                            for (String col : exclude)
                                if (col.equalsIgnoreCase(e.getKey()))
                                {
                                    pass = true;
                                    break;
                                }

                        if (pass) continue;

                        Object val = dst.get(e.getKey());
                        if (!((val == null && e.getValue() == null) || (val != null && val.equals(e.getValue()))))
                        {
//                            System.out.println(e.getValue());
//                            System.out.println(val);
//                            if (val != null)
//                            {
//                                System.out.println(val.getClass());
//                                System.out.println(e.getValue().getClass());
//                                System.out.println(((String)val).length());
//                                System.out.println(((String)e.getValue()).length());
//                                System.out.println(val.equals(e.getValue()));
//
//                                String ss1 = (String)val;
//                                String ss2 = (String)e.getValue();
//                                for (int i=0;i<ss1.length();i++)
//                                {
//                                    if (ss1.charAt(i) != ss2.charAt(i))
//                                        System.out.println(i + ", " + ss1.charAt(i) + ", " + ss2.charAt(i));
//                                }
//                            }

                            diff.put(e.getKey(), e.getValue());
                        }
                    }

                    c++;
                }

                if (c == 0)
                {
                    //insert
                    String kk1 = null, kk2 = null;
                    for (Map.Entry<String, Object> e : src.entrySet())
                    {
                        if (kk1 == null)
                        {
                            kk1 = "`" + e.getKey() + "`";
                            kk2 = strOf(e.getValue());
                        }
                        else
                        {
                            kk1 += ", " + "`" + e.getKey() + "`";
                            kk2 += ", " + strOf(e.getValue());
                        }
                    }

                    sqls.add(String.format("insert into %s (%s) values (%s)", tabName, kk1, kk2));
                }
                else if (!diff.isEmpty())
                {
                    //update
                    String kk1 = null, kk2 = null;
                    for (Map.Entry<String, Object> e : diff.entrySet())
                    {
                        if (kk1 == null)
                        {
                            kk1 = "`" + e.getKey() + "`" + " = " + strOf(e.getValue());
                        }
                        else
                        {
                            kk1 += ", " + "`" + e.getKey() + "`" + " = " + strOf(e.getValue());
                        }
                    }

                    for (int i=0;i<keys.size();i++) //keys不能为null
                    {
                        if (kk2 == null)
                        {
                            kk2 = "`" + keys.get(i) + "`" + " = " + params[i];
                        }
                        else
                        {
                            kk2 += " and " + "`" + keys.get(i) + "`" + " = " + params[i];
                        }
                    }
                    sqls.add(String.format("update %s set %s where %s", tabName, kk1, kk2));
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
        }

        return sqls;
    }

    private JSONObject getAll(ResultSet rs) throws Exception
    {
        ResultSetMetaData rsm = rs.getMetaData();
        int num = rsm.getColumnCount();

        JSONObject r = new JSONObject();
        for (int i=0;i<num;i++)
        {
            String label = rsm.getColumnLabel(i + 1);
            r.put(label.toLowerCase(), rs.getString(label));
        }

        return r;
    }

    private String strOf(Object val)
    {
        if (val == null)
            return "null";

        String str = val.toString();
        str = str.replaceAll("[\\\\]", "\\\\\\\\");
        str = str.replaceAll("[\r]", "\\\\r");
        str = str.replaceAll("[\n]", "\\\\n");
        str = str.replaceAll("[\']", "\\\\\'");
        str = str.replaceAll("[\"]", "\\\\\"");

        return "'" + str + "'";
    }

    public static void main2(String[] s)
    {
        JSONObject db1, db2;

//        db1 = new JSONObject();
//        db1.put("url", "jdbc:mysql://10.253.102.74:3306/iyb-lifeins");
//        db1.put("username", "iyb");
//        db1.put("password", "Abcd2017");
//
//        db2 = new JSONObject();
//        db2.put("url", "jdbc:mysql://120.27.167.233:2892/vie_biz");
//        db2.put("username", "iyb_life");
//        db2.put("password", "Iyblife2018!");

        db1 = new JSONObject();
        db1.put("url", "jdbc:mysql://10.253.8.218:3306/iyb-data-test");
        db1.put("username", "iyb");
        db1.put("password", "Abcd2017");

        db2 = new JSONObject();
        db2.put("url", "jdbc:mysql://10.253.8.218:3306/iyb-data");
        db2.put("username", "iyb");
        db2.put("password", "Abcd2017");

        JSONObject tab = new JSONObject();
        tab.put("name", "iyb_activity_listener");
        tab.put("key", new String[] {"id"});
        tab.put("condition", null);

        DbMgr dm = new DbMgr(null, null, null);
        List<String> r = dm.pretreatTable(db1, db2, tab);

        System.out.println(r);
    }
}
