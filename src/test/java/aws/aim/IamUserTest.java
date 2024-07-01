package aws.aim;
import org.junit.Assert;
import org.junit.Test;
import software.amazon.awssdk.services.iam.model.*;
import java.util.List;


public class IamUserTest extends AbstractTest{

    @Test
    public void testFullAccessUserEC2() {
        validateUserInGroup("FullAccessUserEC2", "FullAccessGroupEC2");
    }

    @Test
    public void testFullAccessUserS3() {
        validateUserInGroup("FullAccessUserS3", "FullAccessGroupS3");
    }

    @Test
    public void testReadAccessUserS3() {
        validateUserInGroup("ReadAccessUserS3", "ReadAccessGroupS3");
    }

    private void validateUserInGroup(String userName, String groupName) {
        List<Group> groups = iamClient.listGroupsForUser(
                ListGroupsForUserRequest.builder()
                        .userName(userName)
                        .build()
        ).groups();

        System.out.println(groups);

        Assert.assertEquals("Number of groups. Actual list is " + groups, 1, groups.size());
        Assert.assertEquals("Group for user " + userName, groupName, groups.get(0).groupName());
    }
}
