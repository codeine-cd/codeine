package codeine.utils.os_process;

import java.util.List;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.credentials.CredHelper;
import codeine.model.Result;
import codeine.utils.ReflectionUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;

public class LinuxProcessCleaner {

	private static final Logger log = Logger.getLogger(LinuxProcessCleaner.class);
	
	private static final int SLEEP_BETWEEN_KILL = 3000;
	private Process process;
	private String user;

	public LinuxProcessCleaner(Process process, String user) {
		this.process = process;
		this.user = user;
	}

	public boolean cleanup() {
		Integer pid = ReflectionUtils.getFieldValue(process, "pid");
		log.info("cleanup linux pid " + pid + " user " + user);
//		List<String> cmd = Lists.newArrayList("/usr/bin/pstree", "-p", pid.toString());
//		List<String> pids = getPidsFromOutput(result.output());
		if (null == user) {
			return false;
//			e(Lists.newArrayList("kill", pid.toString()));
//			ThreadUtils.sleep(SLEEP_BETWEEN_KILL);
//			e(Lists.newArrayList("kill", "-9", pid.toString()));
		} else {
			e(Lists.newArrayList(PathHelper.getReadLogs(), encode(user), encode("kill"), encode(pid.toString())));
			ThreadUtils.sleep(SLEEP_BETWEEN_KILL);
			e(Lists.newArrayList(PathHelper.getReadLogs(), encode(user), encode("kill"), encode("-9"), encode(pid.toString())));
		}
		return true;
	}
	private String encode(final String value) {
		return CredHelper.encode(value);
	}
	
	private Result e(List<String> cmd) {
		Result result = new ProcessExecuterBuilder(cmd).simpleCleanupOnly(true).build().execute();
		return result;
	}

//	static List<String> getPidsFromOutput(String output) {
////		String oneLiner = output.replace("\n", "");
//		Matcher matcher = PATTERN.matcher(output);
//		List<String> $ = Lists.newArrayList();
//		if (matcher.matches()) {
//			for (int i = 0; i < matcher.groupCount(); i++) {
//				$.add(matcher.group(i));
//			}
//		}
//		return $ ;
//	}

}
