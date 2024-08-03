package aws.sns_sqs;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;

public class SQSHelper {
    protected final SqsClient sqsClient;

    public SQSHelper(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public String createQueue(String queueName) {
        CreateQueueRequest request = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();
        CreateQueueResponse result = sqsClient.createQueue(request);
        return result.queueUrl();
    }

    public void sendMessage(String queueUrl, String message) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
        sqsClient.sendMessage(request);
    }

    public List<Message> receiveMessages(String queueUrl) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(5)
                .build();
        return sqsClient.receiveMessage(request).messages();
    }

    public void deleteMessage(String queueUrl, String receiptHandle) {
        DeleteMessageRequest request = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(request);
    }
    public String getQueueUrl(String queueName) {
        GetQueueUrlRequest request = GetQueueUrlRequest.builder()
                .queueName(queueName)
                .build();

        GetQueueUrlResponse response = sqsClient.getQueueUrl(request);
        return response.queueUrl();
    }
}
