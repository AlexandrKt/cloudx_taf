package aws.ec2;
import io.qameta.allure.Description;
import org.junit.Assert;
import org.junit.Test;
import software.amazon.awssdk.services.ec2.model.DescribeSecurityGroupsResponse;
import software.amazon.awssdk.services.ec2.model.IpPermission;
import software.amazon.awssdk.services.ec2.model.SecurityGroup;

public class SecurityGroupTest extends AbstractTest{

    @Test
    @Description("Validate EC2 security groups configuration")
    public void testSecurityGroupsConfiguration() {
        DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups();

        for (SecurityGroup sg : response.securityGroups()) {
            validateSecurityGroup(sg);
        }
    }

    private void validateSecurityGroup(SecurityGroup securityGroup) {

        logger.info("Now we will check: " + securityGroup.groupName() + "\n");

        if (securityGroup.groupName().contains("Public")) {
            validatePublicSecurityGroup(securityGroup);
        } else if (securityGroup.groupName().contains("Private")) {
            validatePrivateSecurityGroup(securityGroup);
        } else if (securityGroup.groupName().equals("default"))
        {
            logger.info("This security group doesn't need to verify");

        } else
        Assert.assertTrue("Does not contain any Public or Private security group", false);
    }

    private void validatePublicSecurityGroup(SecurityGroup securityGroup) {
        boolean ssh = false, http = false;
        for (IpPermission permission : securityGroup.ipPermissions()) {
            if (permission.fromPort() == 80 && permission.toPort() == 80) {

                logger.info("The security group: " + securityGroup.groupName() + " has From port: " + permission.fromPort() + " To Port: " + permission.toPort());
                http = true;

            } else if (permission.fromPort() == 22 && permission.toPort() == 22) {

                logger.info("The security group: " + securityGroup.groupName() +" has From port: " + permission.fromPort() + " To Port: " + permission.toPort());
                ssh = true;
            }
        }
        Assert.assertTrue("Public security group does not have SSH access", ssh);
        Assert.assertTrue("Public security group does not have HTTP access", http);
    }

    private void validatePrivateSecurityGroup(SecurityGroup securityGroup) {
        boolean ssh = false, http = false;
        for (IpPermission permission : securityGroup.ipPermissions()) {
            if (permission.fromPort() == 22 && permission.toPort() == 22) {
                logger.info("The security group: " + securityGroup.groupName() +" has From port: " + permission.fromPort() +
                        " To Port: " + permission.toPort());
                ssh = true;
            } else if (permission.fromPort() == 80 && permission.toPort() == 80) {
                logger.info("The security group: " + securityGroup.groupName() +" has From port: " + permission.fromPort() +
                        " To Port: " + permission.toPort());
                http = true;
            }
        }
        Assert.assertTrue("Private security group does not have SSH access", ssh);
        Assert.assertTrue("Private security group does not have HTTP access", http);
    }
}
