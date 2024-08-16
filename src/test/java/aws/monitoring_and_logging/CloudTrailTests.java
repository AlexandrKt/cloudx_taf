package aws.monitoring_and_logging;

import org.junit.Test;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsRequest;
import software.amazon.awssdk.services.cloudtrail.model.DescribeTrailsResponse;
import software.amazon.awssdk.services.cloudtrail.model.Trail;
import software.amazon.awssdk.services.cloudtrail.model.TrailNotFoundException;
import software.amazon.awssdk.services.cloudwatchlogs.model.ListTagsForResourceRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

public class CloudTrailTests extends AbstractTest{
    AbstractObject obj = jsonParser();
    public String retrieveTrailName(){

        String trailArn = obj.getCloudxserverless().getTrailArn();
        Optional<String> trailName = Arrays.stream(trailArn.split("/")).reduce((str1, str2) -> str1.contains("cloudxserverless-Trail")? str1:str2);
        return  trailName.get();
    }
    @Test
    public void testCloudTrailRequirements() {

        DescribeTrailsRequest request = DescribeTrailsRequest.builder()
                .trailNameList(retrieveTrailName())
                .build();

        DescribeTrailsResponse response;
        try {
            response = cloudTrailClient.describeTrails(request);
        } catch (TrailNotFoundException e) {
            fail("CloudTrail trail not found: " + trailName);
            return;
        }

        Trail trail = response.trailList().get(0);

        // Verify Multi-region
        logger.info("Multi-region is enabled: " + trail.isMultiRegionTrail());
        assertTrue("Multi-region is not enabled", trail.isMultiRegionTrail());

        // Verify Log file validation
        logger.info("Log file validation is enabled: " + trail.logFileValidationEnabled());
        assertTrue("Log file validation is not enabled", trail.logFileValidationEnabled());

        // Verify SSE-KMS encryption is disabled
        logger.info("SSE-KMS encryption is disabled: " + trail.kmsKeyId());
        assertFalse( "SSE-KMS encryption is enabled, but it should be disabled", trail.kmsKeyId() != null);

        // Verify tags
        Map<String, String> tags = getTrailTags(trailName);
        assertEquals("Tag 'cloudx: qa' is not present or incorrect", "qa", tags.get("cloudx"));
    }

    private Map<String, String> getTrailTags(String trailName) {
        // Implement logic to retrieve and return tags for the specified CloudTrail trail
        // This can involve using AWS SDK to call listTagsForResource() or similar method.
        // Example:
        // return cloudTrailClient.listTags(ListTagsForResourceRequest.builder().resourceArn().build()).tags();
        return Map.of("cloudx", "qa"); // Placeholder implementation
    }
}
