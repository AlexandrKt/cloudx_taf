package aws.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class AbstractTest {
    protected static S3Client s3Client;
    protected static final Region REGION = Region.EU_CENTRAL_1;
    final static Logger logger = LoggerFactory.getLogger(aws.ec2.AbstractTest.class);

    static {
        s3Client = S3Client.builder()
                .region(REGION)
                .build();
    }
}
