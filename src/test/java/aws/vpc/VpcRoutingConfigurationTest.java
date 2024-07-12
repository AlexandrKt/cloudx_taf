package aws.vpc;

import aws.ec2.AbstractTest;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.ec2.model.*;
import java.util.List;

public class VpcRoutingConfigurationTest extends AbstractTest{
    final static Logger logger = LoggerFactory.getLogger(AbstractTest.class);
    private String publicInstanceIp;
    @Test
    @Description("Verify routing configuration")
    public void testRoutingConfiguration() {
        Vpc vpc = getNonDefaultVpc();
        validateSubnetsAndRouting(vpc);
        testEc2InstancesConfiguration();
     }

    public void testEc2InstancesConfiguration() {
        DescribeInstancesResponse response = ec2Client.describeInstances();
        if(response.reservations().size() == 2) {
            for (Reservation reservation : response.reservations()) {
                for (Instance instance : reservation.instances()) {
                    validatePublicIp(instance);
                }
            }
        } else Assert.assertTrue("No any instances for this user are present", false);
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
    @Step("Validate Subnets and Routing Configuration")
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
            publicInstanceIp = instance.publicIpAddress();
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
    }

}
