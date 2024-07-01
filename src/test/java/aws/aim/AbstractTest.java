package aws.aim;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;

public abstract class AbstractTest {
    protected static final Region REGION = Region.EU_CENTRAL_1;
    protected static IamClient iamClient;

    @BeforeClass
    public static void setUp() {
        iamClient = IamClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .region(REGION)
                .build();
    }
    // Not sure if this method is needed. As I understand IamClient is auto closable.
    @AfterClass
    public static void close() {
        if (iamClient != null) {
            iamClient.close();
        }
    }
}
