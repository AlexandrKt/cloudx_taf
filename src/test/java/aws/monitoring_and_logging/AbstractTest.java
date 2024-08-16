package aws.monitoring_and_logging;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;

import java.io.File;
import java.io.IOException;

public class AbstractTest {
    protected static final Region REGION = Region.EU_CENTRAL_1;
    final static Logger logger = LoggerFactory.getLogger(aws.monitoring_and_logging.AbstractTest.class);
    protected final CloudTrailClient cloudTrailClient = CloudTrailClient.builder().build();
    protected static final String trailName = "cloudxserverless-Trail";
    protected static final String logGroupName = "/var/log/cloudxserverless-app";
    protected static final String initLogGroupName = "/var/log/cloud-init";

    public AbstractObject jsonParser() {
        ObjectMapper mapper = new ObjectMapper();
        AbstractObject obj = null;
        try {
            obj = mapper.readValue(new File("C:\\MyFolder\\CloudX_AWS_QA\\aws-associate-training\\courses\\CloudX_Associate_AWS_QA\\applications\\cdk-outputs-cloudxserverless.json"), AbstractObject.class);
            //System.out.println(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
