package aws.vpc;

import aws.ec2.AbstractTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.model.*;

public class VpcConfigurationTest extends AbstractTest{
    final static Logger logger = LoggerFactory.getLogger(AbstractTest.class);

    @Test
    @Description("Verify VPC configuration")
    public void testVpcConfiguration() {
        Vpc vpc = getNonDefaultVpc();
        validateVpcConfiguration(vpc);
    }
    @Step("Get non-default VPC")
    private Vpc getNonDefaultVpc() {
        DescribeVpcsRequest describeVpcsRequest = DescribeVpcsRequest.builder().build();
        DescribeVpcsResponse describeVpcsResponse = ec2Client.describeVpcs(describeVpcsRequest);

        return describeVpcsResponse.vpcs().stream()
                .filter(vpc -> !vpc.isDefault())
                .findFirst()
                .orElseThrow(() -> new AssertionError("Non-default VPC not found"));
    }

    @Step("Validate VPC Configuration")
    private void validateVpcConfiguration(Vpc vpc) {
        logger.info("VPC CIDR block: " + vpc.cidrBlock() +"\n");
        Assert.assertEquals("VPC CIDR block mismatch", "10.0.0.0/16", vpc.cidrBlock());
        logger.info("VPC tags contains: " + vpc.tags().stream().filter(tag -> tag.key().equals("cloudx")).toList());
        Assert.assertTrue( "VPC tags mismatch", vpc.tags().stream().anyMatch(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")));
    }
    @Step("Validate VPC tags Configuration")
    private void validateVpcTag(Vpc vpc) {
        logger.info("VPC CIDR block: " + vpc.tags() +"\n");
        boolean hasCloudxTag =vpc.tags().stream().anyMatch(tag -> "cloudx".equals(tag.key()) && "qa".equals(tag.value()));
        Assert.assertTrue("Instance does not have the correct tags", hasCloudxTag);
        Assert.assertTrue( "VPC tags mismatch", vpc.tags().stream().anyMatch(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")));
    }
 }
