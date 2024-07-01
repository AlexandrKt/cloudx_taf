package aws.aim;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        IamGroupTest.class,
        IamPolicyTest.class,
        IamRoleTest.class,
        IamUserTest.class
})
public class RegressionSuite {
}
