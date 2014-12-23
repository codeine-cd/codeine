package codeine.utils.os_process;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;


public class LinuxProcessCleanerTest {

	@Test
	public void test_getPidsFromOutput() {
		String output = "{java}(39165)";
		List<String> pidsFromOutput = LinuxProcessCleaner.getPidsFromOutput(output);
		assertEquals(Lists.newArrayList("39165"), pidsFromOutput);
	}
	@Test
	public void test_getPidsFromOutput2() {
		String output = "{java}(39165)\n{java}(10560)";
		List<String> pidsFromOutput = LinuxProcessCleaner.getPidsFromOutput(output);
		assertEquals(Lists.newArrayList("39165","10560"), pidsFromOutput);
	}
	@Test
	public void test_getPidsFromOutput3() {
		String output = "111(39165)";
		List<String> pidsFromOutput = LinuxProcessCleaner.getPidsFromOutput(output);
		assertEquals(Lists.newArrayList("39165"), pidsFromOutput);
	}

}
