package aws.vpc;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        VpcConfigurationTest.class,
        VpcRoutingConfigurationTest.class
})
public class RegressionSuite {
}
