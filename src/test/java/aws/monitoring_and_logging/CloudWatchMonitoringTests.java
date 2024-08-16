package aws.monitoring_and_logging;
import io.qameta.allure.Description;
import org.junit.Test;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CloudWatchMonitoringTests extends AbstractTest{
    CloudWatchLogsHelper cloudWatchLogsHelper = new CloudWatchLogsHelper();
    AbstractObject obj = jsonParser();

    @Test
    @Description("CXQA-MON-02: For log group {/var/log/cloud-init}")
    public void testCloudInitLogsInCloudWatch() {
        logger.info("");
        assertTrue("CloudInit log group does not exist in CloudWatch for EU_CENTRAL_1 Region.", cloudWatchLogsHelper.verifyLogGroupExists(initLogGroupName));
       /* assertTrue("Log stream for the instance ID does not exist in CloudInit log group.", cloudWatchLogsHelper.verifyLogStreamExists(initLogGroupName, "2024/08/15/[$LATEST]3f4893ec2c3648789f953a8217d5aa4c"));
        List<String> logEvents = cloudWatchLogsHelper.getLogEvents(initLogGroupName, obj.cloudxserverless.getAppInstanceInstanceId());
        assertFalse("No log events found in CloudInit log group for the instance ID.", logEvents.isEmpty());*/
    }
    @Test
    @Description("CXQA-MON-03 for log group {/var/log/cloudxserverless-app}")
    public void testApplicationLogsInCloudWatch() {

        logger.info("Application log group {/var/log/cloudxserverless-app} is exist in CloudWatch: " + cloudWatchLogsHelper.verifyLogGroupExists(logGroupName));
        assertTrue("Application log group does not exist in CloudWatch.", cloudWatchLogsHelper.verifyLogGroupExists(logGroupName));
        //logger.info("Log stream " + +"for the instance ID");
        assertTrue("Log stream for the instance ID does not exist in application log group.", cloudWatchLogsHelper.verifyLogStreamExists(logGroupName, obj.cloudxserverless.getAppInstanceInstanceId()));
        List<String> logEvents = cloudWatchLogsHelper.getLogEvents(logGroupName, obj.cloudxserverless.getAppInstanceInstanceId());
        for (String logEvent : logEvents) {
            logger.info(logEvent);
        }
        assertFalse("No log events found in application log group for the instance ID.", logEvents.isEmpty());
    }
 /*   @Test
    @Description("For log group {/aws/lambda/event-handler}")
    public void testEventHandlerLogsInCloudWatch() {

        assertTrue("Event handler Lambda log group does not exist in CloudWatch.", cloudWatchLogsHelper.verifyLogGroupExists("/aws/lambda/event-handler"));

        // Fetch LogStreams and ensure they are not empty
        List<String> logStreams = cloudWatchLogsHelper.listLogStreams("/aws/lambda/event-handler");
        logger.info("Log Streams: " + logStreams.toString());
        assertFalse("No log streams found for the event handler Lambda function in CloudWatch.", logStreams.isEmpty());

        // log events

            List<String> logEvents = cloudWatchLogsHelper.getLogEvents("/aws/lambda/event-handler", logStreams.get(0));
            for (String logEvent : logEvents) {
                logger.info(logEvent);
            }
            assertFalse("No log events found in the event handler Lambda function's log stream.", logEvents.isEmpty());

    }*/

    @Test
    @Description("CXQA-MON-04: For log group {/aws/lambda/cloudxserverless-EventHandlerLambda}")
    public void testEventHandlerLambdaLogsInCloudWatch(){
        assertTrue("Event handler Lambda log group does not exist in CloudWatch.", cloudWatchLogsHelper.verifyLogGroupExists("/aws/lambda/"+ obj.cloudxserverless.getEventHandlerLambdaName()));

        // Fetch LogStreams and ensure they are not empty
        List<String> logStreams = cloudWatchLogsHelper.listLogStreams("/aws/lambda/"+obj.cloudxserverless.getEventHandlerLambdaName());
        logger.info("Log Streams: " + logStreams.toString());
        assertFalse("No log streams found for the event handler Lambda function in CloudWatch.", logStreams.isEmpty());

        // log events
        for (int i = 0; i< logStreams.size(); i++) {
            List<String> logEvents = cloudWatchLogsHelper.getLogEvents("/aws/lambda/" + obj.cloudxserverless.getEventHandlerLambdaName(), logStreams.get(i));
            for (String logEvent : logEvents) {
                logger.info(logEvent);
            }
        }
        //assertFalse("No log events found in the event handler Lambda function's log stream.", logEvents.isEmpty());

    }
   /* @Test
    public void testEC2InstanceCloudWatchIntegration() {
        String instanceId = "i-0abcd1234efgh5678"; // EC2 instance ID
        boolean isMetricPresent = cloudWatchLogsHelper.verifyEC2MetricExists(instanceId, "CPUUtilization");

        assertTrue("EC2 instance does not have CloudWatch integration for CPUUtilization metric.", isMetricPresent);
    }*/
}
