package aws.aim;

import io.restassured.path.json.JsonPath;
import org.junit.Assert;
import org.junit.Test;
import software.amazon.awssdk.services.iam.model.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

//CXQA-IAM-01: 3 IAM policies are created according to the following requirements:
    public class IamPolicyTest extends AbstractTest {

    @Test
    public void testFullAccessPolicyEC2() {
        validatePolicy("FullAccessPolicyEC2", "ec2:*", "*", "Allow");
    }

    @Test
    public void testFullAccessPolicyS3() {
        validatePolicy("FullAccessPolicyS3", "s3:*", "*", "Allow");
    }

    @Test
    public void testReadAccessPolicyS3() {
        validatePolicy("ReadAccessPolicyS3", "[s3:Describe*, s3:Get*, s3:List*]", "*", "Allow");
    }

    private void validatePolicy(String policyNameExpected, String expectedActions, String expectedResources, String expectedEffect) {
        Optional<Policy> actualPolicy = iamClient.listPolicies().policies()
                .stream()
                .filter(policy -> policyNameExpected.equals(policy.policyName())).findFirst();

        //Check policy exists
        Assert.assertTrue(policyNameExpected + "  was not found", actualPolicy.isPresent());
        String actualArn = actualPolicy.get().arn();

        GetPolicyRequest getPolicyRequest = GetPolicyRequest.builder().policyArn(actualArn).build();
        GetPolicyResponse getPolicyResponse = iamClient.getPolicy(getPolicyRequest);
        Policy policy = getPolicyResponse.policy();

        GetPolicyVersionRequest getPolicyVersionRequest = GetPolicyVersionRequest.builder()
                .policyArn(actualArn)
                .versionId(policy.defaultVersionId())
                .build();
        GetPolicyVersionResponse getPolicyVersionResponse = iamClient.getPolicyVersion(getPolicyVersionRequest);
        PolicyVersion policyVersion = getPolicyVersionResponse.policyVersion();

        String policyDocument = policyVersion.document();
        String decodedPolicyDocument = null;
        try {
            decodedPolicyDocument = URLDecoder.decode(policyDocument, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        // Parse policy document JSON using RestAssured's JsonPath
        JsonPath jsonPath = new JsonPath(decodedPolicyDocument);
        Assert.assertEquals("Should be one statement." , Optional.ofNullable(1), Optional.ofNullable(jsonPath.get("Statement.size()")));

        // Validate policy document
        String actualActions = jsonPath.getString("Statement[0].Action");
        String actualResources = jsonPath.getString("Statement[0].Resource");
        String actualEffect = jsonPath.getString("Statement[0].Effect");

        System.out.println("FullAccessPolicyEC2" + ": Action " + actualActions);
        System.out.println("FullAccessPolicyEC2" + ": Resource " + actualResources);
        System.out.println("FullAccessPolicyEC2" + ": Effect " + actualEffect);

        Assert.assertEquals(policyNameExpected + ": Effect ", expectedEffect, actualEffect);
        Assert.assertEquals(policyNameExpected + ": Resource ", expectedResources, actualResources);
        Assert.assertEquals(policyNameExpected + ": Action ", expectedActions, actualActions);
    }
}
