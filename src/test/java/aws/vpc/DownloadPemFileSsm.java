package aws.vpc;
import org.junit.Test;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;
import software.amazon.awssdk.services.ssm.model.GetParameterResponse;
import software.amazon.awssdk.services.ssm.model.SsmException;
public class DownloadPemFileSsm {
    Region region = Region.EU_CENTRAL_1;
    SsmClient ssmClient = SsmClient.builder().region(Region.of("eu-central-1")).build();

    String keyName = "/ec2/keypair/key-123";
    @Test
    public void downloadPemFileFromAWS(){
        GetParameterRequest parameterRequest = GetParameterRequest.builder()
                .name(keyName)
                .withDecryption(true)
                .build();

        GetParameterResponse parameterResponse = ssmClient.getParameter(parameterRequest);
        String keyValue = parameterResponse.parameter().value();

        // Here you have your PEM file content in keyValue variable
        System.out.println(keyValue);

    }
}
