package lerrain.service.filemgr;

import com.alibaba.fastjson.JSONObject;
import lerrain.service.common.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by lerrain on 2017/5/19.
 */
@Controller
public class Network
{
    @RequestMapping("/request")
    @ResponseBody
    @CrossOrigin
    public String request(@RequestBody JSONObject json)
    {
        return Network.request(json.getString("url"), json.getString("content"), json.getInteger("time"), json.getString("method"));
    }

    public static String request(String urlstr, String req, int timeout, String method)
    {
        String res = null;

        Log.debug("req: " + urlstr + " << " + req);

        HttpURLConnection conn = null;
        try
        {
            URL url = new URL(urlstr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setReadTimeout(timeout);
            conn.setConnectTimeout(timeout);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");

            if (conn instanceof HttpsURLConnection)
            {
                SSLContext sslContext = SSLContext.getInstance("SSL");
                TrustManager[] tm = {new MyX509TrustManager()};
                sslContext.init(null, tm, new java.security.SecureRandom());;
                SSLSocketFactory ssf = sslContext.getSocketFactory();
                ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
            }

            byte[] info = req == null ? null : req.getBytes("UTF-8");
            if (info != null)
                conn.setRequestProperty("Content-Length", String.valueOf(info.length));

            conn.connect();

            if(!"GET".equalsIgnoreCase(method)){
                try (OutputStream out = conn.getOutputStream())
                {
                    if (info != null)
                        out.write(info);
                    out.flush();
                }
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try (InputStream in = conn.getInputStream())
            {
                byte[] b = new byte[1024];
                int c = 0;
                while ((c = in.read(b)) >= 0)
                {
                    baos.write(b, 0, c);
                }
            }
            baos.close();

            res = baos.toString("utf-8");

            Log.debug("res: " + urlstr + " >> " + res);
        }
        catch (Exception e)
        {
            Log.debug("res: " + urlstr + " >> " + e.getMessage());

            throw new RuntimeException(e);
        }
        finally
        {
            if (conn != null)
                conn.disconnect();
        }

        return res;
    }

    private static class MyX509TrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)  throws CertificateException
        {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
        {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }
    }
}
