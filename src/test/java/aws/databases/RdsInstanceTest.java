package aws.databases;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.junit.Test;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

import software.amazon.awssdk.services.rds.model.DBInstance;

import software.amazon.awssdk.services.rds.model.DescribeDbInstancesResponse;
import software.amazon.awssdk.services.rds.model.Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RdsInstanceTest extends AbstractTest{
    public void pemFileCreation() throws IOException {
        SsmClient ssmClient = SsmClient.create();

        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(KEY_FOR_PEM_FILE)
                .withDecryption(true)
                .build();
        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        String keyValue = parameterResponse.parameter().value();

        //To Save the PEM content to a file locally
        Path targetPath = Paths.get(PATH_TO_PEM_FILE);
        byte[] bytes = keyValue.getBytes();
        Files.write(targetPath, bytes);

        System.out.println("Successfully saved key to: " + targetPath);
    }

    @Test
    @Description("Verify the RDS instance configuration and access control")
    public void testRdsInstanceConfigurationAndAccess() throws IOException {
        pemFileCreation();
        validateRdsInstanceConfiguration();
        validateRdsInstanceAccess();
    }

    @Step("Validate RDS Instance Configuration")
    private void validateRdsInstanceConfiguration() {

        DescribeDbInstancesResponse response = rdsClient.describeDBInstances();
        DBInstance dbInstance = response.dbInstances().get(0);

        logger.info("Instance type: " + dbInstance.dbInstanceClass() + "\n");
        assertEquals("Instance type mismatch", INSTANCE_TYPE, dbInstance.dbInstanceClass());
        logger.info("Storage size: " + dbInstance.allocatedStorage() + "\n");
        assertEquals("Storage size mismatch", Optional.ofNullable(STORAGE_SIZE), Optional.ofNullable(dbInstance.allocatedStorage()));
        logger.info("Storage type: " + dbInstance.storageType() + "\n");
        assertEquals("Storage type mismatch", STORAGE_TYPE, dbInstance.storageType());
        logger.info("Database version: " + dbInstance.engine() + "\n");
        assertEquals("Database type mismatch", DATABASE_TYPE, dbInstance.engine());
        logger.info("Database version: " + dbInstance.engineVersion() + "\n");
        assertEquals("Database version mismatch", DATABASE_VERSION, dbInstance.engineVersion());
        logger.info("Multi-AZ: " + dbInstance.multiAZ() + "\n");
        assertTrue("Multi-AZ should be disabled", !dbInstance.multiAZ());
        logger.info("Encryption enabled: " + dbInstance.storageEncrypted() + "\n");
        assertTrue("Encryption should not be enabled", !dbInstance.storageEncrypted());

        List<Tag> tags = dbInstance.tagList();
        logger.info("Tags: " + tags.stream().filter(tag -> TAG_KEY.equals(tag.key()) && TAG_VALUE.equals(tag.value())).toList());
        assertTrue("Tag cloudx: qa not found", tags.stream().anyMatch(tag -> TAG_KEY.equals(tag.key()) && TAG_VALUE.equals(tag.value())));
    }

    @Step("Validate RDS Instance Access Control")
    private void validateRdsInstanceAccess() {
        // Check SSH access to the application instance in the public subnet
        boolean canConnectToPublicInstance = canConnectToInstance(PUBLIC_IP, PATH_TO_PEM_FILE);
        assertTrue("Public instance should be accessible via SSH", canConnectToPublicInstance);
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
}
