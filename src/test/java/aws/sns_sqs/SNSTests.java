package aws.sns_sqs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SNSTests extends AbstractTest{
    private SnsClient snsClient;
    private SNSHelper snsHelper;
    private String topicArn;
    private String subscriptionArn;

    @Before
    public void setup() {
        snsClient = SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        snsHelper = new SNSHelper(snsClient);

        // Create a new SNS topic
       // topicArn = snsHelper.createTopic("cloudximage-TopicSNSTopic" + System.currentTimeMillis());
        topicArn = "arn:aws:sns:eu-";
    }

    @Test
    public void testSNSTopicAttributes() {
        // Assuming SNSHelper is your utility class to interact with SNS
        String topicArn = SNSHelper.getTopicArn("cloudximage-TopicSNSTopic");

        // Get topic attributes
        GetTopicAttributesResponse response = snsClient.getTopicAttributes(GetTopicAttributesRequest.builder()
                .topicArn(topicArn)
                .build());

        // Verify topic name
        String expectedName = "cloudximage-TopicSNSTopic";
        String actualName = response.attributes().get("TopicArn").split(":")[5];
        logger.info("The topic name is: " + actualName + "\n");
        assertTrue(actualName.startsWith(expectedName));

        // Verify topic type
        String topicType = response.attributes().get("FifoTopic");
        logger.info("The topic type is: " + (topicType == null ? "standard" : "fifo") + "\n");
        assertEquals("standard", topicType == null ? "standard" : "fifo");

        // Verify encryption is disabled
        String kmsMasterKeyId = response.attributes().get("KmsMasterKeyId");
        logger.info("Encryption is: " + (kmsMasterKeyId  == null ? "disabled" : "enabled")+ "\n");
        assertNull("Encryption should be disabled", kmsMasterKeyId);

        // Verify tags
        ListTagsForResourceResponse tagResponse = snsClient.listTagsForResource(ListTagsForResourceRequest.builder()
                .resourceArn(topicArn)
                .build());
        logger.info("Tags: " + tagResponse.tags().stream().filter(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")).collect(Collectors.toList()) + "\n");
        assertTrue(tagResponse.tags().stream().anyMatch(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")));
    }

    @Test
    public void testSubscribeEmail() {
        String emailAddress = "myemail@email.com";
        subscriptionArn = snsHelper.subscribeEmail(topicArn, emailAddress);

        // Simulate subscription confirmation step if needed
        // snsHelper.confirmSubscription(topicArn, "subscriptionToken");

        List<Subscription> subscriptions = snsHelper.listSubscriptionsByTopic(topicArn);
        boolean isSubscribed = subscriptions.stream()
                .anyMatch(subscription -> subscription.endpoint().equals(emailAddress) && subscription.protocol().equals("email"));

        assertTrue(isSubscribed);
    }

    @Test
    public void testPublishMessage() {
       // String emailAddress = "test@example.com";
       // subscriptionArn = snsHelper.subscribeEmail(topicArn, emailAddress);

        // Simulate subscription confirmation step if needed
        // snsHelper.confirmSubscription(topicArn, "subscriptionToken");

        String message = "This is a test message";
        String subject = "Test Subject";
        snsHelper.publishMessage(topicArn, message/*, subject*/);

    }

    @Test
    public void cleanup() {
        snsClient = SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        snsHelper = new SNSHelper(snsClient);
        if (subscriptionArn != null && !subscriptionArn.isEmpty() && subscriptionArn.contains(":")) {
            snsHelper.unsubscribe(subscriptionArn);
        }
        snsClient.deleteTopic(request -> request.topicArn(topicArn));
        snsClient.close();
    }
}
