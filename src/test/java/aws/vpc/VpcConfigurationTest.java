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

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.tuple;

public class VpcConfigurationTest extends AbstractTest{
    final static Logger logger = LoggerFactory.getLogger(AbstractTest.class);
    private SoftAssertions softAssertions;

    @Test
    @Description("Verify VPC configuration")
    public void testVpcConfiguration() {
        Vpc vpc = getNonDefaultVpc();
        validateVpcConfiguration(vpc);
        //validateSubnetsAndRouting(vpc);
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
 /*   @Step("Validate Subnets and Routing Configuration")
    private void validateSubnetsAndRouting(Vpc vpc) {
        DescribeSubnetsRequest describeSubnetsRequest = DescribeSubnetsRequest.builder()
                .filters(Filter.builder().name("vpc-id").values(vpc.vpcId()).build())
                .build();
        DescribeSubnetsResponse describeSubnetsResponse = ec2Client.describeSubnets(describeSubnetsRequest);
        List<Subnet> subnets = describeSubnetsResponse.subnets();

        Assert.assertTrue("Public subnet not found", subnets.stream().anyMatch(this::isPublicSubnet));
        Assert.assertTrue("Private subnet not found", subnets.stream().anyMatch(this::isPrivateSubnet));

        subnets.forEach(this::validateRoutingConfiguration);
    }

    @Step("Check if Subnet is Public")
    private boolean isPublicSubnet(Subnet subnet) {
        return subnet.mapPublicIpOnLaunch();
    }

    @Step("Check if Subnet is Private")
    private boolean isPrivateSubnet(Subnet subnet) {
        return !subnet.mapPublicIpOnLaunch();
    }
    private void validatePublicIp(Instance instance) {
        if (instance.iamInstanceProfile().arn().contains("Public")) {
            logger.info("Public instance has public IP: " + instance.publicIpAddress());
            Assert.assertNotNull("Public instance does not have public IP", instance.publicIpAddress());

        } else if (instance.iamInstanceProfile().arn().contains("Private")) {
            logger.info("Private instance has public IP: " + instance.publicIpAddress());
            Assert.assertNull("Private instance has public IP", instance.publicIpAddress());
        }
        else {Assert.assertEquals("public or private contains instance", instance.iamInstanceProfile().arn());}
    }

    @Step("Validate Routing Configuration")
    private void validateRoutingConfiguration(Subnet subnet) {
        DescribeRouteTablesRequest describeRouteTablesRequest = DescribeRouteTablesRequest.builder()
                .filters(Filter.builder().name("association.subnet-id").values(subnet.subnetId()).build())
                .build();
        DescribeRouteTablesResponse describeRouteTablesResponse = ec2Client.describeRouteTables(describeRouteTablesRequest);
        List<RouteTable> routeTables = describeRouteTablesResponse.routeTables();

        for (RouteTable routeTable : routeTables) {
            for (Route route : routeTable.routes()) {
                if (isPublicSubnet(subnet) ) {
                    if(route.gatewayId().equals("local")){
                        logger.info(route.gatewayId() + " gateway for public subnet no need to be verified!" + "\n");
                    } else if (route.gatewayId() != null  && route.gatewayId().startsWith("igw-")) {
                        logger.info("gatewayId: " + route.gatewayId() + "\n");
                        Assert.assertTrue("Public subnet does not have Internet Gateway route", route.gatewayId() != null  && route.gatewayId().startsWith("igw-"));
                    }

                } else if (isPrivateSubnet(subnet)){
                     if (route.natGatewayId() != null || route.instanceId() != null){
                        logger.info("gatewayId: " + route.gatewayId() + "\n");
                        logger.info("natGatewayId: " + route.natGatewayId() + "\n");
                    Assert.assertTrue("Private subnet does not have NAT Gateway route", route.natGatewayId() != null && route.natGatewayId().startsWith("nat-"));
                }   else if ((route.gatewayId().equals("local")))
                    {
                        logger.info(route.gatewayId() + " gateway for private subnet no need to be verified!" + "\n");
                    }
                }
            }
        }
    }*/
}
