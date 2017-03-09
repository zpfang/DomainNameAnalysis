package cc.zpfang;

import com.aliyuncs.exceptions.ClientException;
import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by fangzp on 2017-03-03.
 */
public class AnalysisTask {
    private static final Logger logger = Logger.getLogger(AnalysisTask.class);

    public static void main(String[] args) throws ClientException {
        //args[0] accessKeyId, args[1] accessKeySecret
        final ModifyDomainRecord domainRecord = new ModifyDomainRecord(args[0],
                args[1]);
        final OuterNetIp outerNetIp = new OuterNetIp();

        Runnable runnable = new Runnable() {
            public void run() {
                logger.info("-------------开始执行---------------");
                String ip = outerNetIp.getOuterNetIp();
                domainRecord.modifyRecord(ip);
                logger.info("-------------结束执行---------------");
            }
        };

        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
    }
}