package aws.serverless_basics;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public abstract class AbstractTest {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractTest.class);
    protected DynamoDbClient dynamoDbClient;
    protected String TABLE_NAME = "cloudxserverless-DatabaseImagesTable";
    protected String LAMBDA_FUNCTION_NAME = "cloudxserverless-EventHandlerLambda";

    @Before
    public void setUp() {
        dynamoDbClient = DynamoDbClient.builder()
                .region(Region.EU_CENTRAL_1)
                .build();
        logger.info("DynamoDB client initialized.");
    }

    @After
    public void tearDown() {
        if (dynamoDbClient != null) {
            dynamoDbClient.close();
        }
        logger.info("DynamoDB client closed.");
    }
}
