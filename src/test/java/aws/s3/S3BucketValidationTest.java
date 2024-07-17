package aws.s3;

import io.qameta.allure.Description;
import org.junit.Test;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class S3BucketValidationTest extends AbstractTest {

    private static final String BUCKET_NAME = "cloudximage-imagestorebucket123"; // Replace with your bucket name

        @Test
        @Description("Verify S3 bucket tagging configuration")
        public void testBucketTagging() {
            GetBucketTaggingRequest taggingRequest = GetBucketTaggingRequest.builder()
                    .bucket(BUCKET_NAME)
                    .build();
            GetBucketTaggingResponse taggingResponse = s3Client.getBucketTagging(taggingRequest);

            List<Tag> tags = taggingResponse.tagSet();
            logger.info("Tags: " + tags.stream().filter(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")).collect(Collectors.toList()) + "\n");
            assertTrue(tags.stream().anyMatch(tag -> tag.key().equals("cloudx") && tag.value().equals("qa")));
        }
         @Test
         @Description("Verify S3 bucket encryption configuration")
         public void testBucketEncryption() {
             GetBucketEncryptionRequest encryptionRequest = GetBucketEncryptionRequest.builder()
                     .bucket(BUCKET_NAME)
                     .build();

             GetBucketEncryptionResponse encryptionResponse = s3Client.getBucketEncryption(encryptionRequest);
             ServerSideEncryptionConfiguration configuration = encryptionResponse.serverSideEncryptionConfiguration();
             logger.info("S3 bucket encryption: " + configuration.rules().get(0).applyServerSideEncryptionByDefault().sseAlgorithm().toString() + "\n");
             assertEquals("AES256", configuration.rules().get(0).applyServerSideEncryptionByDefault().sseAlgorithm().toString());
         }

         @Test
         @Description("Verify S3 bucket policy status")
         public void testBucketPolicyStatus() {
              GetBucketPolicyStatusRequest policyStatusRequest = GetBucketPolicyStatusRequest.builder()
                .bucket(BUCKET_NAME)
                .build();

        GetBucketPolicyStatusResponse policyStatusResponse = s3Client.getBucketPolicyStatus(policyStatusRequest);
        logger.info("S3 bucket policy: " + policyStatusResponse.policyStatus() + "\n");
        assertFalse(policyStatusResponse.policyStatus().isPublic());
    }

    @Test
    @Description("Verify S3 bucket versioning configuration")
    public void testS3Versioning() {
        GetBucketVersioningRequest versioningRequest = GetBucketVersioningRequest.builder()
                .bucket(BUCKET_NAME)
                .build();

        GetBucketVersioningResponse versioningResponse = s3Client.getBucketVersioning(versioningRequest);
        logger.info("S3 bucket Version: " + versioningResponse.status());
        assertNotEquals(versioningResponse.status(), BucketVersioningStatus.ENABLED);
    }
}
