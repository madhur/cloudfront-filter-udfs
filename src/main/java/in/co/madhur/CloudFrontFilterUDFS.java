package in.co.madhur;

import com.amazonaws.athena.connector.lambda.handlers.UserDefinedFunctionHandler;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.SubnetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class CloudFrontFilterUDFS extends UserDefinedFunctionHandler {

    private static final String SOURCE_TYPE = "Custom";
    static ObjectMapper mapper = new ObjectMapper();
    static List<SubnetUtils.SubnetInfo> subnetUtils;

    static {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("cloudfront.json");
        CloudFront cloudfront = null;
        try {
            cloudfront = mapper.readValue(in, CloudFront.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        subnetUtils = cloudfront.getPrefixes().stream().map(prefix -> new SubnetUtils(prefix.getIp_prefix()).getInfo()).collect(Collectors.toList());
    }

    public CloudFrontFilterUDFS() {
        super(SOURCE_TYPE);
    }

    public String iscloudfrontip(String ipAddress) {
        if(StringUtils.isEmpty(ipAddress)) {
            return String.valueOf(false);
        }
        return String.valueOf(subnetUtils.stream().anyMatch(s-> s.isInRange(ipAddress)));
    }

    public String iscloudfrontipport(String ipAddressAndPort) {
        if(StringUtils.isEmpty(ipAddressAndPort)) {
            return String.valueOf(false);
        }
        return String.valueOf(subnetUtils.stream().anyMatch(s-> s.isInRange(ipAddressAndPort.split(":")[0])));
    }

    public static void main(String[] args) {
        CloudFrontFilterUDFS cloudFrontFilterUDFS = new CloudFrontFilterUDFS();
        String ans = cloudFrontFilterUDFS.iscloudfrontip("130.176.104.53");
        System.out.println(ans);
        ans = cloudFrontFilterUDFS.iscloudfrontipport("130.176.104.53:8080");
        System.out.println(ans);
        ans = cloudFrontFilterUDFS.iscloudfrontip("49.15.229.57");
        System.out.println(ans);
        ans = cloudFrontFilterUDFS.iscloudfrontip("103.171.187.220");
        System.out.println(ans);
    }
}
