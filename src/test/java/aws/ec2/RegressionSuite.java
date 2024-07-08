package aws.ec2;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        Ec2InstanceTest.class,
        SecurityGroupTest.class
})
public class RegressionSuite {

}
