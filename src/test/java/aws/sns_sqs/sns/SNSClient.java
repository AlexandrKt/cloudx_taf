package aws.sns_sqs.sns;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.List;
    public class SNSClient {
        private SnsClient client;
        private String topicArn;
        private String protocol;

        public SNSClient(String topicArn, String protocol, String regionName) {
            this.client = SnsClient.builder().region(Region.of(regionName)).build();
            this.topicArn = topicArn;
            this.protocol = protocol;
        }

        public SubscribeResponse subscribe(String email) {
            try {
                SubscribeResponse response = client.subscribe(r -> r
                        .topicArn(topicArn)
                        .protocol(protocol)
                        .endpoint(email)
                        .returnSubscriptionArn(true)
                );

                System.out.println("SNS Client: subscribed " + email);
                System.out.println("SNS Client: subscription ID - " + response.subscriptionArn());
                return response;
            } catch (InvalidParameterException e) {
                throw new IllegalArgumentException("Invalid Email");
            }
        }

        public void unsubscribe(String email) {
            String subscriptionArn = getSubscriptionArn(email);
            if (subscriptionArn.equals("PendingConfirmation")) {
                throw new IllegalStateException("Pending Confirmation");
            }
            client.unsubscribe(u -> u.subscriptionArn(subscriptionArn));
            System.out.println("SNS Client: unsubscribed " + email);
        }

        private String getSubscriptionArn(String email) {
            List<Subscription> subscriptions = listSubscriptions();
            return subscriptions.stream()
                    .filter(x -> x.endpoint().equals(email))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Subscription Not Found"))
                    .subscriptionArn();
        }

        public List<Subscription> listSubscriptions() {
            ListSubscriptionsByTopicResponse response = client.listSubscriptionsByTopic(r -> r.topicArn(topicArn));
            return response.subscriptions();
        }

        public PublishResponse publish(String message) {
            PublishResponse response = client.publish(r -> r
                    .topicArn(topicArn)
                    .message(message));

            System.out.println("SNS Client: Published message id: " + response.messageId());
            System.out.println("SNS Client: Published message body: " + message);
            return response;
        }
    }
