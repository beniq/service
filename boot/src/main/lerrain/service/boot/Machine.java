package lerrain.service.boot;

import ch.ethz.ssh2.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Machine
{
    String root = "./webapps/";
    String javaBin;

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

    public String getJavaBin()
    {
        return javaBin;
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

    public String upload(InputStream is, String name, long length) throws Exception
    {
        Connection connection = new Connection(host);
        connection.connect();

        if (!connection.authenticateWithPassword(user, pwd))
            throw new RuntimeException("Authentication failed.");

        SCPClient sCPClient = connection.createSCPClient();
        SCPOutputStream scpOutputStream = sCPClient.put(name, length, root + "temp", "0600");

        byte[] b = new byte[1024];
        int c = 0;
        while ((c = is.read(b)) > 0)
            scpOutputStream.write(b, 0, c);

        scpOutputStream.flush();
        scpOutputStream.close();

        connection.close();

        return "temp/" + name;
    }
}
