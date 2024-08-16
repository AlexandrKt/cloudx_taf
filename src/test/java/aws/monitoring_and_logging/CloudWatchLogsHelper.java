package aws.monitoring_and_logging;

import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.*;

import java.util.List;

public class CloudWatchLogsHelper {
    private final CloudWatchLogsClient cloudWatchLogsClient;

    public CloudWatchLogsHelper() {
        this.cloudWatchLogsClient = CloudWatchLogsClient.create();
    }

    public boolean verifyLogGroupExists(String logGroupName) {
        DescribeLogGroupsRequest request = DescribeLogGroupsRequest.builder()
                .logGroupNamePrefix(logGroupName)
                .build();

        DescribeLogGroupsResponse response = cloudWatchLogsClient.describeLogGroups(request);
        return response.logGroups().stream().anyMatch(lg -> lg.logGroupName().equals(logGroupName));
    }

    public boolean verifyLogStreamExists(String logGroupName, String logStreamName) {
        DescribeLogStreamsRequest request = DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamNamePrefix(logStreamName)
                .build();

        DescribeLogStreamsResponse response = cloudWatchLogsClient.describeLogStreams(request);
        return response.logStreams().stream().anyMatch(ls -> ls.logStreamName().equals(logStreamName));
    }

    public List<String> listLogStreams(String logGroupName) {
        DescribeLogStreamsRequest request = DescribeLogStreamsRequest.builder()
                .logGroupName(logGroupName)
                .build();

        DescribeLogStreamsResponse response = cloudWatchLogsClient.describeLogStreams(request);
        return response.logStreams().stream()
                .map(LogStream::logStreamName)
                .toList();
    }

    public List<String> getLogEvents(String logGroupName, String logStreamName) {
        GetLogEventsRequest request = GetLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .logStreamName(logStreamName)
                .build();

        GetLogEventsResponse response = cloudWatchLogsClient.getLogEvents(request);
        return response.events().stream()
                .map(OutputLogEvent::message)
                .toList();
    }

    public boolean verifyEC2MetricExists(String instanceId, String metricName) {
        // Implement logic to verify that a CloudWatch metric for an EC2 instance exists
        // (e.g., by using the CloudWatchClient to describe metrics)
        return true; // Placeholder, actual implementation required
    }
}
