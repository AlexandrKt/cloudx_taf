package aws.databases;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.junit.Assert;
import org.junit.Test;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class ApplicationFunctionalTestWithRdsConnection extends AbstractTest{
    @Test
    @Description("Verify uploaded image metadata is stored in MySQL RDS database")
    public void testImageMetadataStorage() throws IOException {
        String bucketName = "cloudximage-databasemysqlinstanced64026b2-rra2qolcioxe.crgam0u8sl3j";
        String imageName = "ES_v1_configuration.jpg";
        //uploadImageToS3(bucketName, imageName);

        ImageMetadata metadata = getImageMetadataFromRds(imageName);

        Assert.assertNotNull("Image metadata should not be null", metadata);
        Assert.assertEquals("Image key mismatch", metadata.getImageKey(), imageName);
        Assert.assertTrue("Image size should be greater than 0", metadata.getObjectSize() > 0);
        Assert.assertNotNull("Image type should not be null", metadata.getObjectType());
        Assert.assertNotNull("Last modification date should not be null", metadata.getLastModified());
    }

    @Step("Upload image to S3")
    private void uploadImageToS3(String bucketName, String imageName) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build();
        PutObjectResponse response = s3Client.putObject(request, RequestBody.fromString("Image content"));
        Assert.assertNotNull("Image upload response should not be null", response);
    }
    @Step("Check SSH access to instance")
    private boolean canConnectToInstance(String instanceIp, String keyFile) {
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(instanceIp);
            KeyProvider keys = ssh.loadKeys(PATH_TO_PEM_FILE);
            ssh.authPublickey("ec2-user", keys);

            Session session = ssh.startSession();

            session.close();
            ssh.disconnect();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Step("Get image metadata from RDS")
    private ImageMetadata getImageMetadataFromRds(String imageName) throws IOException {
        SSHClient ssh = new SSHClient();
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(PUBLIC_IP);
            KeyProvider keys = ssh.loadKeys(PATH_TO_PEM_FILE);
            ssh.authPublickey("ec2-user", keys);

            Session session = ssh.startSession();

        try (Connection conn = getConnection()) {
            String query = "SELECT image_key, image_size, image_type, last_modified FROM image_metadata WHERE image_key = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, imageName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new ImageMetadata(
                                rs.getString("id"),
                                rs.getLong("image_size"),
                                rs.getString("image_type"),
                                rs.getTimestamp("last_modified")
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        session.close();
        ssh.disconnect();
        return null;
    }

    private Connection getConnection() throws Exception {
        String url = "jdbc:mysql://cloudximage-databasemysqlinstanced6.eu-central-1.rds.amazonaws.com";
        String user = "mysql_admin";
        String password = "123";
        Properties connectionProps = new Properties();
        connectionProps.put("user", user);
        connectionProps.put("password", password);
        return DriverManager.getConnection(url, connectionProps);
    }
}
