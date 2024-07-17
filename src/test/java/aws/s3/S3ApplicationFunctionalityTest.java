package aws.s3;
import io.qameta.allure.Description;
import org.junit.Test;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertTrue;
public class S3ApplicationFunctionalityTest extends AbstractTest {

    private static final String BUCKET_NAME = "cloudximage-imagestorebucket123"; // Replace with your bucket name
    private static final String IMAGE_PATH = "resources\\ES_v1_configuration.jpg"; // Replace with your image path
    private static final String IMAGE_KEY = "ES_v1_configuration.jpg";

    @Test
    @Description("Upload image to S3 bucket")
    public void test1() {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(IMAGE_KEY)
                .build();

        s3Client.putObject(putObjectRequest, new File(IMAGE_PATH).toPath());
        logger.info("File uploaded to S3 bucket!");
    }

    @Test
    @Description("Download image from S3 bucket")
    public void test2() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(IMAGE_KEY)
                .build();

        Instant Date;
        s3Client.getObject(getObjectRequest, new File("downloaded_" + IMAGE_KEY + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))).toPath());
        logger.info("File downloaded from S3 bucket to root folder.");
        assertTrue(new File("downloaded_" + IMAGE_KEY + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))).exists());
    }

    @Test
    @Description("View list of uploaded images")
    public void test3() {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .build();

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        logger.info("List of images in S3 bucket: \n" + listObjectsResponse.contents().stream().filter(Key -> Key != null).toList());
        assertTrue(listObjectsResponse.contents().stream().anyMatch(s3Object -> s3Object.key().equals(IMAGE_KEY)));
    }

    @Test
    @Description("Delete image from S3 bucket")
    public void test4() {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(IMAGE_KEY)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME)
                .build();

        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        logger.info("List of images in S3 bucket after file deletion: " + listObjectsResponse.contents());
        assertTrue(listObjectsResponse.contents().stream().noneMatch(s3Object -> s3Object.key().equals(IMAGE_KEY)));
    }
}
