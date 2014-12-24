package codeine.utils.os_process;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Sets;


public class LinuxProcessCleanerTest {

	private void assertGetPids(String output, String... expected) {
		Set<String> pidsFromOutput = LinuxProcessCleaner.getPidsFromOutput(output);
		assertEquals(Sets.newHashSet(expected), pidsFromOutput);
	}
	
	@Test
	public void test_getPidsFromOutput_Simple() {
		String output = "java(39165)";
		assertGetPids(output, "39165");
	}
	
	@Test
	public void test_getPidsFromOutput_Thread() {
		String output = "{java}(39165)";
		assertGetPids(output);
	}
	@Test
	public void test_getPidsFromOutput_2Lines() {
		String output = "java(39165)\njava(10560)";
		assertGetPids(output, "39165","10560");
	}
	@Test
	public void test_getPidsFromOutputInOneLine() {
		String output = "eclipse(17221)---java(17222)";
		assertGetPids(output, "17221","17222");
	}
	@Test
	public void test_getPidsFromOutput3() {
		String output = "111(39165)";
		assertGetPids(output, "39165");
	}
	

}
