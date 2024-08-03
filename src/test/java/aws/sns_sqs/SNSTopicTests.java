package aws.sns_sqs;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.CreateTopicResponse;
import software.amazon.awssdk.services.sns.model.GetTopicAttributesRequest;
import software.amazon.awssdk.services.sns.model.GetTopicAttributesResponse;
import software.amazon.awssdk.services.sns.model.SetTopicAttributesRequest;
import software.amazon.awssdk.services.sns.model.Tag;
import software.amazon.awssdk.services.sns.model.TagResourceRequest;
import software.amazon.awssdk.services.sns.model.UntagResourceRequest;
import software.amazon.awssdk.services.sns.model.DeleteTopicRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class SNSTopicTests {
    private SnsClient snsClient;
    private String topicArn;

    @Before
    public void setup() {
        snsClient = SnsClient.builder()
                .region(Region.EU_CENTRAL_1)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        topicArn = "arn:aws:sns:eu";

/*        CreateTopicRequest createTopicRequest = CreateTopicRequest.builder()
                .name("cloudximage-TopicSNSTopic" + System.currentTimeMillis())
                .build();
        CreateTopicResponse createTopicResponse = snsClient.createTopic(createTopicRequest);
        topicArn = createTopicResponse.topicArn();

        // Adding tags
        TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                .resourceArn(topicArn)
                .tags(Tag.builder().key("cloudx").value("qa").build())
                .build();
        snsClient.tagResource(tagResourceRequest);*/
    }

    @Test
    public void testSNSTopicAttributes() {
        GetTopicAttributesRequest getTopicAttributesRequest = GetTopicAttributesRequest.builder()
                .topicArn(topicArn)
                .build();
        GetTopicAttributesResponse getTopicAttributesResponse = snsClient.getTopicAttributes(getTopicAttributesRequest);
        Map<String, String> attributes = getTopicAttributesResponse.attributes();

        // Verify the topic name
        assertTrue(attributes.get("TopicArn").contains("cloudximage-TopicSNSTopic"));

        // Verify the topic type (standard topic)
        assertEquals("standard", attributes.get("FifoTopic"));

        // Verify encryption is disabled
        assertTrue(attributes.get("KmsMasterKeyId") == null || attributes.get("KmsMasterKeyId").isEmpty());

        // Verify the topic has the correct tags
        List<Tag> tags = snsClient.listTagsForResource(r -> r.resourceArn(topicArn)).tags();
        assertTrue(tags.stream().anyMatch(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")));
    }

    @After
    public void cleanup() {
        // Remove tags
 /*       UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                .resourceArn(topicArn)
                .tagKeys("cloudx")
                .build();
        snsClient.untagResource(untagResourceRequest);

        // Delete the topic
        DeleteTopicRequest deleteTopicRequest = DeleteTopicRequest.builder()
                .topicArn(topicArn)
                .build();
        snsClient.deleteTopic(deleteTopicRequest);*/
        snsClient.close();
    }
}
