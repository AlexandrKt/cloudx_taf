package aws.serverless_basics;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.*;

import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class LambdaTests{
    protected String LAMBDA_FUNCTION_NAME = "cloudxserverless-EventHandlerLambda";
    protected String SQS_QUEUE_ARN = "arn:aws:sqs:eu-central-1:533:cloudxserverless-QueueSQSQueue";
    protected String LOG_GROUP_NAME = "/aws/lambda/cloudxserverless-EventHandlerLambda";
    protected static final Logger logger = LoggerFactory.getLogger(LambdaTests.class);
    protected LambdaClient lambdaClient = LambdaClient.create();
    private final CloudWatchHelper cloudWatchHelper = new CloudWatchHelper();
    @Test
    public void testLambdaConfiguration() {
        // Get Lambda configuration
        GetFunctionConfigurationRequest request = GetFunctionConfigurationRequest.builder()
                .functionName(LAMBDA_FUNCTION_NAME)
                .build();

        GetFunctionConfigurationResponse response = lambdaClient.getFunctionConfiguration(request);

        // Verify Memory Size
        logger.info("Memory Size: " + response.memorySize());
        assertEquals("Lambda memory size is incorrect", Optional.of(128).get(), Optional.ofNullable(response.memorySize()).get());

        // Verify Ephemeral Storage
        logger.info("Ephemeral Storage: " + response.ephemeralStorage().size());
        assertEquals("Lambda ephemeral storage is incorrect", Optional.ofNullable(512).get(), Optional.ofNullable(response.ephemeralStorage().size()).get());

        // Verify Timeout
        logger.info("Timeout: " + response.timeout());
        assertEquals("Lambda timeout is incorrect", Optional.ofNullable(3), Optional.ofNullable(response.timeout()));

        // Verify Tags
       /* TagResourceRequest tagRequest = TagResourceRequest.builder()
                .resourceArn(response.functionArn())
                .build();

        TagResourceResponse tagResponse = lambdaClient.tagResource(tagRequest);
        assertTrue("Tag 'cloudx: qa' is missing from the Lambda function.", tagResponse.tags().containsKey("cloudx"));*/
    }
    @Test
    public void testLambdaTriggerSQS() {
        try {
            // List event source mappings for the Lambda function
            ListEventSourceMappingsRequest request = ListEventSourceMappingsRequest.builder()
                    .functionName(LAMBDA_FUNCTION_NAME)
                    .build();

            ListEventSourceMappingsResponse response = lambdaClient.listEventSourceMappings(request);

            // Check if the SQS queue is configured as an event source
            List<EventSourceMappingConfiguration> mappings = response.eventSourceMappings();
            System.out.println(mappings.toString());
            boolean sqsQueueFound = mappings.stream()
                    .anyMatch(mapping -> SQS_QUEUE_ARN.equals(mapping.eventSourceArn()));

            assertTrue( "SQS Queue is not set as an event source for the Lambda function.", sqsQueueFound);

            System.out.println("SQS Queue is correctly configured as an event source for the Lambda function.");

        } catch (LambdaException e) {
            e.printStackTrace();
            fail("Failed to list event source mappings for Lambda function: " + e.getMessage());
        }
    }
    @Test
    public void testLambdaLogsAreStoredInCloudWatch() {

        boolean logGroupExists = cloudWatchHelper.doesLogGroupExist(LOG_GROUP_NAME);

        assertTrue("The log group for the Lambda function does not exist.", logGroupExists);

        // Optionally, you can also verify that some logs exist
        var logEvents = cloudWatchHelper.getLogsForLambda(LOG_GROUP_NAME);
        assertFalse("No logs found in the CloudWatch log group for the Lambda function.",
                logEvents.events().isEmpty());
    }

}
