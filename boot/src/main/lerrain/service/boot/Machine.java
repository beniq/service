package lerrain.service.boot;

import ch.ethz.ssh2.*;
import lerrain.service.boot.bat.MachineCmd;
import lerrain.tool.Common;

import java.io.*;

public class Machine
{
    String root = "./webapp/";
    String javaBin;
    String logPath;

    String name;
    String host;
    String user;
    String pwd;

    int index;

    public Machine(String host, String user, String pwd)
    {
        this.host = host;
        this.user = user;
        this.pwd = pwd;
    }

    public String getLogPath(String logFile)
    {
        if (logPath == null)
            return root + "log/" + logFile;

        return root + logPath + logFile;
    }

    public void setLogPath(String logPath)
    {
        this.logPath = logPath;
    }

    public String getJavaBin()
    {
        return javaBin;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setJavaBin(String javaBin)
    {
        this.javaBin = javaBin;
    }

    public String getRoot()
    {
        return root;
    }

    public void setRoot(String root)
    {
        this.root = root;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public File getFile(String path)
    {
        return new File(root + path);
    }

    public String getPath()
    {
        return root;
    }

    public String getServicePath(String path)
    {
        return root + "service/" + path;
    }

    public String getPath(String path)
    {
        return root + path;
    }

    public String getHost()
    {
        return host;
    }

    public synchronized String run(String cmd) throws Exception
    {
        Connection conn = new Connection(host);

        try
        {
            conn.connect();

            if (!conn.authenticateWithPassword(user, pwd))
                throw new RuntimeException("Authentication failed.");

            System.out.println("machine<" + this.getHost() + "> login");
            System.out.println("$ " + cmd);

            Session session = conn.openSession();
            session.execCommand(cmd);

            StringBuffer sb = new StringBuffer(cmd);
            sb.append("\n");

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout()))))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                    sb.append("\n");

                    System.out.println(line);
                }
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStderr()))))
            {
                String line;
                while ((line = br.readLine()) != null)
                {
                    sb.append(line);
                    sb.append("\n");

                    System.out.println(line);
                }
            }

            session.close();

            System.out.println("machine<" + this.getHost() + "> logout");

            return sb.toString();
        }
        finally
        {
            if (conn != null)
                conn.close();
        }
    }

    public synchronized String upload(InputStream is, String name, long length, String dir) throws Exception
    {
        Connection connection = new Connection(host);
        connection.connect();

        if (!connection.authenticateWithPassword(user, pwd))
            throw new RuntimeException("Authentication failed.");

        SCPClient sCPClient = connection.createSCPClient();
        SCPOutputStream scpOutputStream = sCPClient.put(name, length, getPath(dir).replaceAll("\\\\", "/"), "0600");

        byte[] b = new byte[1024];
        int c = 0;
        while ((c = is.read(b)) > 0)
            scpOutputStream.write(b, 0, c);

        scpOutputStream.flush();
        scpOutputStream.close();

        connection.close();

        System.out.println("upload to " + Machine.pathOf(dir, name) + " success");

        return Machine.pathOf(dir, name);
    }

    public void initiate() throws Exception
    {
        File dir = new File("file/lib");
        if (!dir.isDirectory())
            throw new RuntimeException("no lib");

        initFile(new File("file/lib"), "lib");
        initFile(new File("file/soft"), "soft");

        StringBuffer s = new StringBuffer();
        s.append("mkdir -p " + getPath("temp") + ";");
        s.append("mkdir -p " + getPath("data") + ";");
        s.append("mkdir -p " + getPath("log") + ";");
        s.append("chmod 777 " + getPath("soft/jdk1.8.0_151/bin/java") + ";");
        run(s.toString());
    }

    public void initFile(File dir, String path) throws Exception
    {
        for (File f : dir.listFiles())
        {
            if (f.isFile())
            {
                try (InputStream is = new FileInputStream(f))
                {
                    run("mkdir -p " + getPath(path));
                    upload(is, f.getName(), f.length(), path);
                }
            }
            else if (f.isDirectory())
            {
                initFile(f, pathOf(path, f.getName()));
            }
        }
    }

    public static String pathOf(String path1, String path2)
    {
        if (Common.isEmpty(path1))
        {
            return path2;
        }
        else if (Common.isEmpty(path2))
        {
            return path1;
        }
        else
        {
            if (!path1.endsWith("/") && !path1.endsWith("\\") && !path1.equals(File.separator))
            {
                path1 = path1 + "/";
            }

            if (path2.startsWith("/") || path2.startsWith("\\") || path2.startsWith(File.separator))
            {
                path2 = path2.substring(1);
            }

            return path1 + path2;
        }
    }
}
