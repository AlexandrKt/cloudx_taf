package aws.aim;
import io.qameta.allure.AllureId;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Assert;
import org.junit.Test;

import software.amazon.awssdk.services.iam.model.*;
import java.util.List;
import static org.junit.Assert.assertTrue;

@DisplayName("IAM Users Group Validation Tests")
public class IamGroupTest extends AbstractTest{

    @Test
    @AllureId("001")
    @DisplayName("Validate FullAccessGroupEC2")
    @Description("Validate that FullAccessPolicyEC2 is in FullAccessGroupEC")
    public void testFullAccessGroupEC2() {
        validateGroupPolicy("FullAccessGroupEC2", "FullAccessPolicyEC2");
    }

    @Test
    @AllureId("002")
    @DisplayName("Validate FullAccessGroupS3")
    @Description("Validate that FullAccessPolicyS3 is in FullAccessGroupS3")
    public void testFullAccessGroupS3() {
        validateGroupPolicy("FullAccessGroupS3", "FullAccessPolicyS3");
    }

    @Test
    @AllureId("003")
    @DisplayName("Validate FullAccessGroupEC2")
    @Description("Validate that ReadAccessPolicyS3 is in ReadAccessGroupS3")
    public void testReadAccessGroupS3() {
        validateGroupPolicy("ReadAccessGroupS3", "ReadAccessPolicyS3");
    }

    private void validateGroupPolicy(String groupName, String expectedPolicyName) {

        ListAttachedGroupPoliciesRequest listAttachedGroupPoliciesRequest = ListAttachedGroupPoliciesRequest.builder()
                .groupName(groupName)
                .build();

        ListAttachedGroupPoliciesResponse listAttachedGroupPoliciesResponse = iamClient.listAttachedGroupPolicies(listAttachedGroupPoliciesRequest);
        List<AttachedPolicy> attachedPolicies = listAttachedGroupPoliciesResponse.attachedPolicies();

        Assert.assertEquals("Number of policies. Actual list is " + attachedPolicies, 1, attachedPolicies.size());

        boolean policyAttached = attachedPolicies.stream()
                .anyMatch(policy -> policy.policyName().equals(expectedPolicyName));
        System.out.println("\n" + attachedPolicies + "\n");

        assertTrue("Policy " + expectedPolicyName + " is not attached to group " + groupName, policyAttached);

        Assert.assertEquals("Policy fo group " + groupName, expectedPolicyName, attachedPolicies.get(0).policyName());
    }
}
