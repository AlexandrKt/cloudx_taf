package aws.sns_sqs;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;

public class SNSHelper extends AbstractTest{

    public SNSHelper(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public String createTopic(String topicName) {
        CreateTopicRequest request = CreateTopicRequest.builder()
                .name(topicName)
                .build();
        CreateTopicResponse response = snsClient.createTopic(request);
        return response.topicArn();
    }

    public String subscribeEmail(String topicArn, String emailAddress) {
        SubscribeRequest request = SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("email")
                .endpoint(emailAddress)
                .returnSubscriptionArn(true)
                .build();
        SubscribeResponse response = snsClient.subscribe(request);
        return response.subscriptionArn();
    }

    public void confirmSubscription(String topicArn, String token) {
        ConfirmSubscriptionRequest request = ConfirmSubscriptionRequest.builder()
                .topicArn(topicArn)
                .token(token)
                .build();
        snsClient.confirmSubscription(request);
    }

    public void publishMessage(String topicArn, String message/*, String subject*/) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                //.subject(subject)
                .build();
        snsClient.publish(request);
    }

    public List<Subscription> listSubscriptionsByTopic(String topicArn) {
        ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(topicArn)
                .build();
        return snsClient.listSubscriptionsByTopic(request).subscriptions();
    }

    public void unsubscribe(String subscriptionArn) {
        UnsubscribeRequest request = UnsubscribeRequest.builder()
                .subscriptionArn(subscriptionArn)
                .build();
        snsClient.unsubscribe(request);
    }

    public static String getTopicArn(String topicNamePrefix) {
        try {
            String nextToken = null;
            do {
                ListTopicsRequest request = ListTopicsRequest.builder()
                        .nextToken(nextToken)
                        .build();

                ListTopicsResponse response = snsClient.listTopics(request);

                for (Topic topic : response.topics()) {
                    String topicArn = topic.topicArn();
                    String topicName = topicArn.split(":")[5]; // Extract the topic name from the ARN
                    if (topicName.startsWith(topicNamePrefix)) {
                        return topicArn;
                    }
                }

                nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (SnsException e) {
            e.printStackTrace();
        }

        // If no topic is found with the specified prefix, return null or handle as needed
        return null;
    }
}
