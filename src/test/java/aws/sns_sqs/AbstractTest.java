package aws.sns_sqs;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;

public class AbstractTest {
    protected static SnsClient snsClient;
    protected SNSHelper snsHelper;
    protected String topicArn;
    protected String subscriptionArn;
    protected String SQS_NAME = "cloudximage-QueueSQSQueue";
    final static Logger logger = LoggerFactory.getLogger(aws.sns_sqs.AbstractTest.class);
    @Before
    public void setup() {
        snsClient = SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        snsHelper = new SNSHelper(snsClient);

        // Create a new SNS topic
       // topicArn = snsHelper.createTopic("cloudximage-TopicSNSTopic" + System.currentTimeMillis());


    }
    @After
    public void cleanup() {
   /*     if (subscriptionArn != null && !subscriptionArn.isEmpty() && subscriptionArn.contains(":")) {
            snsHelper.unsubscribe(subscriptionArn);
        }
        snsClient.deleteTopic(request -> request.topicArn(topicArn));*/
        snsClient.close();
    }
}
