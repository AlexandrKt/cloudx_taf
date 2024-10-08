package aws.databases;

import io.restassured.RestAssured;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rds.RdsClient;
import software.amazon.awssdk.services.s3.S3Client;

public class AbstractTest {
    protected static final Region REGION = Region.EU_CENTRAL_1;
    final static Logger logger = LoggerFactory.getLogger(aws.ec2.AbstractTest.class);
    protected static final String DB_INSTANCE_IDENTIFIER = "cloudximage-databasemysqlinstanced64026b2-ev3gwuqwjenw.crgam0u8sl3j.eu-central-1.rds.amazonaws.com";
    protected static final String KEY_FOR_PEM_FILE = "/ec2/keypair/key-0edd949fb6f67e824";
    protected static final String PATH_TO_PEM_FILE = "C:\\MyFolder\\AWS\\AWS_CloudX_TAF\\target\\myKey.pem";
    protected static final String INSTANCE_TYPE = "db.t3.micro";
    protected static final int STORAGE_SIZE = 100;
    protected static final String STORAGE_TYPE = "gp2";
    protected static final String DATABASE_TYPE = "mysql";
    protected static final String DATABASE_VERSION = "8.0.32";
    protected static final String TAG_KEY = "cloudx";
    protected static final String TAG_VALUE = "qa";
    protected static final String PUBLIC_IP = "3.120.146.113";
    protected static final String DB_USER_NAME = "mysql_admin";
    protected static final String DB_USER_PASSWORD = "6kEXMd8PwHnG3j2yI3FHV9uRb9tCkX1u";
    protected RdsClient rdsClient;
    protected static S3Client s3Client;
    @Before
    public void setup() {
        rdsClient = RdsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
    static {
        s3Client = S3Client.builder()
                .region(REGION)
                .build();
    }
}
