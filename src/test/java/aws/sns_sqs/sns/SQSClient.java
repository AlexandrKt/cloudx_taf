package aws.sns_sqs.sns;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SQSClient {
    protected final SqsClient client;
    protected final String queueUrl;

    public SQSClient(String queueUrl, String regionName) {
        this.client = SqsClient.builder()
                .region(Region.of(regionName))
                .build();
        this.queueUrl = queueUrl;
    }

    public SendMessageResponse sendMessage(String eventType, String objectKey,
                                           String objectType, String lastModified,
                                           int objectSize, String downloadLink) {
        String messageBody = String.format("event_type: %s\nobject_key: %s\nobject_type: %s\nlast_modified: %s\nobject_size: %d\ndownload_link: %s",
                eventType, objectKey, objectType, lastModified, objectSize, downloadLink);

        SendMessageResponse response = client.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(messageBody)
                .build());

        System.out.println("SQS Client: Sent message: id " + response.messageId());
        System.out.println("SQS Client: Sent message: body " + messageBody);
        return response;
    }

    public Optional<List<Message>> receiveMessages() {
        ReceiveMessageResponse response = client.receiveMessage(ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(10)
                .waitTimeSeconds(15)
                .build());

        List<Message> messages = response.messages();
        if (!messages.isEmpty()) {
            System.out.println("SQS Client: received " + messages.size());
        } else {
            System.out.println("SQS Client: no messages received");
        }
        return Optional.ofNullable(messages.isEmpty() ? null : messages);
    }

    public DeleteMessageBatchResponse deleteMessageBatch(List<Message> messages) {
        List<DeleteMessageBatchRequestEntry> entries = messages.stream()
                .map(m -> DeleteMessageBatchRequestEntry.builder()
                        .id(m.messageId())
                        .receiptHandle(m.receiptHandle())
                        .build())
                .collect(Collectors.toList());

        DeleteMessageBatchResponse response = client.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(entries)
                .build());

        if (!response.failed().isEmpty()) {
            System.out.println("SQS Client: unable to delete " + messages.size() + " messages");
        } else {
            System.out.println("SQS Client: " + messages.size() + " messages deleted");
        }
        return response;
    }

}
