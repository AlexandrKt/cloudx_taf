package aws.serverless_basics;

import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;
import software.amazon.awssdk.services.cloudwatchlogs.model.DescribeLogGroupsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsRequest;
import software.amazon.awssdk.services.cloudwatchlogs.model.FilterLogEventsResponse;
import software.amazon.awssdk.services.cloudwatchlogs.model.ResourceNotFoundException;

public class CloudWatchHelper {

    private final CloudWatchLogsClient cloudWatchLogsClient;

    public CloudWatchHelper() {
        this.cloudWatchLogsClient = CloudWatchLogsClient.builder().build();
    }

    public boolean doesLogGroupExist(String logGroupName) {
        try {
            cloudWatchLogsClient.describeLogGroups(DescribeLogGroupsRequest.builder()
                    .logGroupNamePrefix(logGroupName)
                    .build());
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public FilterLogEventsResponse getLogsForLambda(String logGroupName) {
        return cloudWatchLogsClient.filterLogEvents(FilterLogEventsRequest.builder()
                .logGroupName(logGroupName)
                .build());
    }
}
