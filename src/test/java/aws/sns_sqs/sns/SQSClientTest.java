package aws.sns_sqs.sns;

import org.junit.Test;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertNotNull;

public class SQSClientTest {
    @Test
    public void testSendMessage() {
        SQSClient sqsClient = new SQSClient("https://sqs.us-east-1.amazonaws.com/123456789012/MyQueue", "us-east-1");
        SendMessageResponse response = sqsClient.sendMessage(
                "upload",
                "file.txt",
                "file",
                DateTimeFormatter.ISO_INSTANT.format(java.time.Instant.now()),
                123456,
                "http://example.com/download/file.txt"
        );
        assertNotNull(response.messageId());
    }
}
