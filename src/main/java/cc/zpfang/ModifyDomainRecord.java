package cc.zpfang;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsRequest;
import com.aliyuncs.alidns.model.v20150109.DescribeDomainRecordsResponse;
import com.aliyuncs.alidns.model.v20150109.UpdateDomainRecordRequest;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.google.common.base.Strings;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fangzp on 2017-03-03.
 */
public class ModifyDomainRecord {

    private static final Logger logger = Logger.getLogger(ModifyDomainRecord.class);
    private static final String DOMAIN_NAME = "zpfang.cc";
    private static final String RECORD_TYPE_A = "A";
    private static final String RR = "www";
    private static final String ERROR_CODE_IP_NOTCAHNGE = "DomainRecordDuplicate";
    private static IAcsClient client = null;

    static {

    }

    private String ip = "";

    public ModifyDomainRecord(String accessKeyId, String accessKeySecret) {
        String regionId = "cn-hangzhou"; //必填固定值，必须为“cn-hanghou”
        IClientProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
        // 若报Can not find endpoint to access异常，请添加以下此行代码
        // DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Alidns", "alidns.aliyuncs.com");
        client = new DefaultAcsClient(profile);
    }

    public static boolean isboolIp(String ipAddress) {
        String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    boolean modifyRecord(String newIp) {
        if (Strings.isNullOrEmpty(newIp) || newIp.length() > 15 || newIp.length() < 7 || !isboolIp(newIp)) {
            logger.error("ip format is wrong: " + newIp);
            return false;
        }
        if (ip.equals(newIp)) {
            logger.info("ip is not changed...");
            return true;
        }
        try {
            String recordId = getRecordId();
            modifyRecordIp(recordId, newIp);
        } catch (ClientException exception) {
            if (ERROR_CODE_IP_NOTCAHNGE.equals(exception.getErrCode())) {
                logger.info(exception.getErrCode() + ", ip is not change.");
                ip = newIp;
                return true;
            }
            logger.info("modify record fail:" + "errorCode:" + exception.getErrCode()
                    + ", errorMsg: " + exception.getErrMsg() + " ", exception);
            return false;
        }
        logger.info("modify record success.");
        ip = newIp;
        return true;
    }

    private String getRecordId() throws ClientException {
        String recordId = "";
        DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest();
        request.setDomainName(DOMAIN_NAME);
        DescribeDomainRecordsResponse response;
        response = client.getAcsResponse(request);
        List<DescribeDomainRecordsResponse.Record> domainList = response.getDomainRecords();
        for (DescribeDomainRecordsResponse.Record record : domainList) {
            if (RECORD_TYPE_A.equals(record.getType())) {
                recordId = record.getRecordId();
            }
        }
        return recordId;
    }

    private void modifyRecordIp(String recordId, String ip) throws ClientException {
        UpdateDomainRecordRequest recordRequest = new UpdateDomainRecordRequest();
        recordRequest.setRecordId(recordId);
        recordRequest.setRR(RR);
        recordRequest.setValue(ip);
        recordRequest.setType(RECORD_TYPE_A);
        client.getAcsResponse(recordRequest);
    }
}
