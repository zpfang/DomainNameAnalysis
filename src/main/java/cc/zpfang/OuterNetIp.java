package cc.zpfang;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by fangzp on 2017-03-03.
 */
public class OuterNetIp {
    private static final String QUERY_ADDRESS = "http://www.icanhazip.com";
    private static final Logger logger = Logger.getLogger(ModifyDomainRecord.class);


    public static void main(String[] args) {
        OuterNetIp outerNetIp = new OuterNetIp();
        outerNetIp.getOuterNetIp();
    }

    public String getOuterNetIp() {
        String result = "";
        URLConnection connection;
        BufferedReader in = null;
        try {
            URL url = new URL(QUERY_ADDRESS);
            connection = url.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "KeepAlive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            connection.connect();

            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            logger.info("get ip result: " + result);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
