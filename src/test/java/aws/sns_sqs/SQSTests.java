package aws.sns_sqs;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sqs.model.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class SQSTests extends AbstractTest{
    private SqsClient sqsClient;
    private String queueUrl;

    @Before
    public void setup() {
        sqsClient = SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
  /*      CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("MyTestQueue")
                .build();
        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        queueUrl = createQueueResponse.queueUrl();*/
    }

/*    @Test
    public void testSendAndReceiveMessage() {
        String messageBody = "Test message";
        SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build();
        SendMessageResponse sendMessageResponse = sqsClient.sendMessage(sendMessageRequest);

        // Verify the message was sent
        assertTrue("SendMessageResponse should have a message ID", !sendMessageResponse.messageId().isEmpty());

        ReceiveMessageRequest receiveMessageRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .build();
        ReceiveMessageResponse receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
        List<Message> messages = receiveMessageResponse.messages();

        // Verify a message was received
        assertFalse("ReceiveMessageResponse should contain at least one message", messages.isEmpty());

        Message receivedMessage = messages.get(0);

        // Verify the message body
        assertEquals("The message body should match", messageBody, receivedMessage.body());

        // Delete the message from the queue
        DeleteMessageRequest deleteMessageRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receivedMessage.receiptHandle())
                .build();
        sqsClient.deleteMessage(deleteMessageRequest);

        // Verify the message was deleted
        receiveMessageResponse = sqsClient.receiveMessage(receiveMessageRequest);
        messages = receiveMessageResponse.messages();
        assertTrue("The queue should be empty after deleting the message", messages.isEmpty());
    }*/
    @Test
    public void verifySqsQueueAttributes() {

        String queueName = SQS_NAME;
        String queueUrl = getQueueUrl(queueName);
        logger.info(queueUrl + "\n");
        // Verify queue attributes
        GetQueueAttributesRequest attributesRequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNamesWithStrings("All")
                .build();

        GetQueueAttributesResponse attributesResponse = sqsClient.getQueueAttributes(attributesRequest);

        // Verify Encryption
        String kmsMasterKeyId = attributesResponse.attributesAsStrings().get("SqsManagedSseEnabled");
        logger.info("Encryption is: " + (kmsMasterKeyId.equals("true") ? "enabled" : "disabled")+ "\n");
        assertNotNull("Encryption should be enabled", kmsMasterKeyId);

        // Verify Queue Type (SQS doesn't have a "type" attribute, assuming "standard" is default)
        // No action needed for queue type

        // Verify Tags
        ListQueueTagsRequest tagsRequest = ListQueueTagsRequest.builder()
                .queueUrl(queueUrl)
                .build();

        ListQueueTagsResponse tagsResponse = sqsClient.listQueueTags(tagsRequest);
        Map<String, String> tags = tagsResponse.tags();

        logger.info("Tags: " + tags.get("cloudx") + "\n");
        assertEquals("qa", tags.get("cloudx"));

        // Verify Dead-letter queue (RedrivePolicy should not be set)
        String redrivePolicy = attributesResponse.attributesAsStrings().get("RedrivePolicy");
        logger.info("Dead-letter queue: " + redrivePolicy);
        assertNull("Dead-letter queue should not be set", redrivePolicy);
    }
    public String getQueueUrl(String queueName) {
        GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        GetQueueUrlResponse response = sqsClient.getQueueUrl(request);
        return response.queueUrl();
    }
    @After
    public void cleanup() {
       /* DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        sqsClient.deleteQueue(deleteQueueRequest);*/
        sqsClient.close();
    }
}
