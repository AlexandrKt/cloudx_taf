package aws.aim;

import org.junit.Assert;
import org.junit.Test;
import software.amazon.awssdk.services.iam.model.*;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class IamRoleTest extends AbstractTest{

    @Test
    public void testFullAccessRoleEC2() {
        validateRolePolicy("FullAccessRoleEC2", "FullAccessPolicyEC2");
    }

    @Test
    public void testFullAccessRoleS3() {
        validateRolePolicy("FullAccessRoleS3", "FullAccessPolicyS3");
    }

    @Test
    public void testReadAccessRoleS3() {
        validateRolePolicy("ReadAccessRoleS3", "ReadAccessPolicyS3");
    }

    private void validateRolePolicy(String roleName, String expectedPolicyName) {

        ListAttachedRolePoliciesRequest listAttachedRolePoliciesRequest = ListAttachedRolePoliciesRequest.builder()
                .roleName(roleName)
                .build();

        ListAttachedRolePoliciesResponse listAttachedRolePoliciesResponse = iamClient.listAttachedRolePolicies(listAttachedRolePoliciesRequest);
        List<AttachedPolicy> attachedPolicies = listAttachedRolePoliciesResponse.attachedPolicies();

        boolean policyAttached = attachedPolicies.stream()
                .anyMatch(policy -> policy.policyName().equals(expectedPolicyName));

        List<String> policiesNamesList = iamClient.listAttachedRolePolicies(ListAttachedRolePoliciesRequest.builder().roleName(roleName).build()).attachedPolicies()
                .stream().map(a -> a.policyName()).toList();

        assertTrue("Policy " + expectedPolicyName + " is not attached to role " + roleName, policyAttached);
        Assert.assertEquals("Number of policies. Actual list is " + attachedPolicies, 1, attachedPolicies.size());

        Assert.assertEquals("Policy for role " + roleName, expectedPolicyName, policiesNamesList.get(0));

    }
}
