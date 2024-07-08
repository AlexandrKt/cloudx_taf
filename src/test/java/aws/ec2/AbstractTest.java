package aws.ec2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
public class AbstractTest {
    protected static final Ec2Client ec2Client;
    protected static final Region REGION = Region.EU_CENTRAL_1;
    final static Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    static {
        ec2Client = Ec2Client.builder()
                .region(REGION)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
