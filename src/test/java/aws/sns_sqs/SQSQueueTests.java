package aws.sns_sqs;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
public class SQSQueueTests {
    private SqsClient sqsClient;
    private String queueUrl;
    private String queueArn;

    @Before
    public void setup() {
        sqsClient = SqsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
/*
        Map<QueueAttributeName, String> attributes = new HashMap<>();
        attributes.put(QueueAttributeName.valueOf("VisibilityTimeout"), "60");
        attributes.put(QueueAttributeName.valueOf("KmsMasterKeyId"), "alias/aws/sqs"); // Encryption enabled

        CreateQueueRequest createQueueRequest = CreateQueueRequest.builder()
                .queueName("cloudximage-QueueSQSQueue" + System.currentTimeMillis())
                .attributes(attributes)
                .build();

        CreateQueueResponse createQueueResponse = sqsClient.createQueue(createQueueRequest);
        queueUrl = createQueueResponse.queueUrl();

        // Adding tags
        TagQueueRequest tagQueueRequest = TagQueueRequest.builder()
                .queueUrl(queueUrl)
                .tags(Map.of("cloudx", "qa"))
                .build();
        sqsClient.tagQueue(tagQueueRequest);

        // Getting the queue ARN
        GetQueueAttributesResponse queueAttributesResponse = sqsClient.getQueueAttributes(GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN)
                .build());
        queueArn = queueAttributesResponse.attributes().get(QueueAttributeName.QUEUE_ARN);*/
    }

    @Test
    public void testSQSQueueAttributes() {
        GetQueueAttributesRequest getQueueAttributesRequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.ALL)
                .build();
        GetQueueAttributesResponse getQueueAttributesResponse = sqsClient.getQueueAttributes(getQueueAttributesRequest);
        Map<QueueAttributeName, String> attributes = getQueueAttributesResponse.attributes();

        // Verify the queue name
        assertTrue(attributes.get(QueueAttributeName.QUEUE_ARN).contains("cloudximage-QueueSQSQueue"));

        // Verify encryption is enabled
        assertEquals("alias/aws/sqs", attributes.get(QueueAttributeName.KMS_MASTER_KEY_ID));

        // Verify the queue type (standard queue)
        assertNull(attributes.get(QueueAttributeName.FIFO_QUEUE));

        // Verify the queue has the correct tags
        ListQueueTagsResponse listQueueTagsResponse = sqsClient.listQueueTags(ListQueueTagsRequest.builder()
                .queueUrl(queueUrl)
                .build());
        Map<String, String> tags = listQueueTagsResponse.tags();
        assertTrue(tags.containsKey("cloudx") && tags.get("cloudx").equals("qa"));

        // Verify the queue does not have a dead-letter queue
        assertNull(attributes.get(QueueAttributeName.REDRIVE_POLICY));
    }

    @After
    public void cleanup() {
        // Remove tags
      /*  UntagQueueRequest untagQueueRequest = UntagQueueRequest.builder()
                .queueUrl(queueUrl)
                .tagKeys("cloudx")
                .build();
        sqsClient.untagQueue(untagQueueRequest);

        // Delete the queue
        DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                .queueUrl(queueUrl)
                .build();
        sqsClient.deleteQueue(deleteQueueRequest);*/
        sqsClient.close();
    }
}
