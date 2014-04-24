package codeine.utils.os;

import org.junit.Test;

public class OsUtilsTest {

	@Test
	public void test() {
		@SuppressWarnings("unused")
		OperatingSystem hostOs = OsUtils.getHostOs();//just checking no exception is thrown
//		System.out.println(hostOs);
	}

}
