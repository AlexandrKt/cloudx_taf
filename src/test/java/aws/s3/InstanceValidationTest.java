package aws.s3;

import com.jcraft.jsch.JSchException;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Test;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;

import java.io.*;
import java.util.Properties;
import static org.junit.Assert.assertTrue;

public class InstanceValidationTest extends AbstractTest {
    private static final String PUBLIC_IP = "1.1.1.1"; // Replace with your public IP
    private static final String FQDN = "ec2.eu-central-1.compute.amazonaws.com"; // Replace with your FQDN
    private static final String SSH_USER = "ec2-user";
    private static final String BUCKET_NAME = "cloudximage-imagestorebucket123"; // Replace with your bucket name

    private static final String keyName = "/ec2/keypair/key-123";
    //private String keyValue = "";

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://" + PUBLIC_IP + "/api/ui";
        //RestAssured.baseURI = "http://" + FQDN + "/api/ui";
    }

    @Test
    @Description("Verify the application is accessible via HTTP")
    public void testApplicationHttpAccess() {

        RequestSpecification httpRequest = RestAssured
                .given()
                .header("Content Type","application/json");
        Response response = httpRequest.get("/");

        ResponseBody body = response.getBody();

        // By using the ResponseBody.asString() method, we can convert the  body
        // into the string representation.
        System.out.println("Response Body is: " + body.asString());
    }

    @Test
    @Description("Verify the application is accessible via SSH")
    public void testApplicationSshAccess() throws IOException, JSchException {
        SsmClient ssmClient = SsmClient.builder().region(REGION).build();
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(keyName)
                .withDecryption(true)
                .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        String keyValue = parameterResponse.parameter().value();

        // Here you have your PEM file content in keyValue variable
        System.out.println(keyValue);
        File tempKeyFile = File.createTempFile("key", "");
        FileWriter fileWriter = new FileWriter(tempKeyFile);
        fileWriter.write(keyValue);
        fileWriter.close();

            JSch jsch = new JSch();
            jsch.addIdentity(tempKeyFile.getAbsolutePath(), null,null);


            Session session = jsch.getSession(SSH_USER, PUBLIC_IP, 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();

            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            channel.setOutputStream(outputStream);

            // Execute a simple command to check SSH access
            channel.setCommand("echo 'SSH Access Verified'");
            channel.connect();

        InputStream inputStream = null;
        try {
            inputStream = channel.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] tmp = new byte[1024];
            while (true) {
                while (inputStream.available() > 0) {
                    int i = inputStream.read(tmp, 0, 1024);
                    if (i < 0) break;
                    outputStream.write(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    if (inputStream.available() > 0) continue;
                    break;
                }
                try { Thread.sleep(1000); } catch (Exception ee) {}
            }

            String output = new String(outputStream.toByteArray());
            channel.disconnect();
            session.disconnect();

            assertTrue(output.contains("SSH Access Verified"));
    }

}
