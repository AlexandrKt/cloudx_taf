package aws.sns_sqs.sns;
import aws.sns_sqs.SNSHelper;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Test;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;
import software.amazon.awssdk.services.sns.model.SubscribeResponse;
import software.amazon.awssdk.services.sns.model.Subscription;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
public class SNSClientTest {
    SNSHelper snsHelper;
    EmailHelper emailHelper;
    String topicArn = "arn:aws:sns:Topic";
    String emailAddress = "myemail@email.com";
    SNSClient snsClient = new SNSClient(topicArn, "email", "eu-central-1");
    @Test
    public void testSubscription() {

        SubscribeResponse subscribeResponse = snsClient.subscribe(emailAddress);
        assertNotNull(subscribeResponse.subscriptionArn());

        System.out.println(snsClient.listSubscriptions().stream().toList());

    }
    @Test
    public void testUnsubscribe() {

        snsClient.unsubscribe(emailAddress);
    }
}
