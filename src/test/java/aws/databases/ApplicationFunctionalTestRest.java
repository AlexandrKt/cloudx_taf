package aws.databases;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;

import static io.restassured.RestAssured.*;

public class ApplicationFunctionalTestRest extends AbstractTest{
    @Test
    @Description("Verify image is saved into bucket by POST request")
    public void testPostImage() throws URISyntaxException {
        RestAssured.baseURI = "http://" + PUBLIC_IP;
        Response uploadResponse = RestAssured.given()
                .log().all()
                .header("Content-Type", "multipart/form-data")
                .header("Accept", "application/json")
                .multiPart("upfile", new File("src/test/resources/ES_v1_configuration.jpg"))
                .when()
                .post("/api/image")
                .then()
                .statusCode(200)
                .extract().response();

        String imageId = uploadResponse.jsonPath().getString("id");
        logger.info("Response body after POST request: " + uploadResponse.getBody().asPrettyString());
        testGetImageMetadata(imageId);
    }

    @Description("Verify image metadata is returned by GET request")
    public void testGetImageMetadata(String id) {
        String imageId = "ES_v1_configuration.jpg";

        Response response = given()
                .baseUri("http://" + PUBLIC_IP)
                .basePath("/api/image/" + id)
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        logger.info("GET request" + response.getBody().asPrettyString());
        ImageMetadata metadata = response.as(ImageMetadata.class);

        Assert.assertEquals("Image key mismatch", imageId, metadata.getImageKey().substring(44));
        Assert.assertTrue("Image size should be greater than 0", metadata.getObjectSize() > 0);
        Assert.assertNotNull("Image type should not be null", metadata.getObjectType().toString() );
        Assert.assertNotNull("Last modification date should not be null", metadata.getLastModified());
    }
    @Test
    @Description("Verify All images metadata is returned by GET request")
    public void testGetAllImagesMetadata() {
        Response response = given()
                .baseUri("http://" + PUBLIC_IP)
                .basePath("api/image")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        logger.info("GET request" + response.getBody().asPrettyString());
    }
    @Test
    @Description("Verify image file is returned by GET request")
    public void testGetImageFileById() {
        Response response = given()
                .baseUri("http://" + PUBLIC_IP)
                .basePath("api/image/file/10")
                .when()
                .get()
                .then()
                .statusCode(200)
                .extract()
                .response();
        logger.info("GET request for file retrieve: " + response.getBody().asPrettyString());
    }
    @Test
    @Description("Verify image is deleted by DELETE request")
    public void testDeleteImage() {

        Response response = given()
                .baseUri("http://" + PUBLIC_IP)
                .basePath("/api/image/14")
                .when()
                .delete()
                .then()
                .statusCode(200)
                .extract()
                .response();
        logger.info("Response body after DELETE request: " + response.getBody().asPrettyString());
        Assert.assertEquals("Response body mismatch",   "\"" + "Image is deleted"+ "\"", response.getBody().asPrettyString());
    }
}
