package yami;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
		YamiMailSenderTest.class, YamiMailSenderTest2.class, ShouldSendMailValidatorTest.class
})
public class YamiMailSenderTestSuite
{
	
}
