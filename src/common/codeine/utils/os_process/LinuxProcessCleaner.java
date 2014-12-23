package codeine.utils.os_process;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.credentials.CredHelper;
import codeine.model.Result;
import codeine.utils.ReflectionUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class LinuxProcessCleaner {

	private static final Logger log = Logger.getLogger(LinuxProcessCleaner.class);
	
	private static final int SLEEP_BETWEEN_KILL = 3000;
	private String user;

	public LinuxProcessCleaner(Process process, String user) {
		this.user = user;
		pid = ReflectionUtils.getFieldValue(process, "pid");
	}

	public void cleanup(){
		log.info("cleanup linux pid " + pid + " user " + user);
		kill(false);
		ThreadUtils.sleep(SLEEP_BETWEEN_KILL);
		kill(true);
	}

	private void kill(boolean force) {
		List<String> pids1 = getPids();
		if (pids1.isEmpty()) {
			log.info("pids is empty " + force);
			return;
		}
		ArrayList<String> cmd1 = Lists.newArrayList("kill");
		if (force) {
			cmd1.add("-9");
		}
		cmd1.addAll(pids1);
		executeAsUserIfNeeded(cmd1);
	}

	private List<String> getPids() {
		List<String> cmd = Lists.newArrayList("/usr/bin/pstree", "-pA", pid.toString());
		Result result = execute(cmd);
		return getPidsFromOutput(result.output());
	}
	private String encode(final String value) {
		return CredHelper.encode(value);
	}
	
	private Result execute(List<String> cmd) {
		return new ProcessExecuterBuilder(cmd).simpleCleanupOnly(true).build().execute();
	}
	private Result executeAsUserIfNeeded(List<String> cmd) {
		List<String> cmd1;
		if (user == null) {
			cmd1 = cmd;
		} else {
			cmd1 = Lists.newArrayList(PathHelper.getReadLogs(), encode(user));
			for (String string : cmd) {
				cmd1.add(encode(string));
			}
		}
		return new ProcessExecuterBuilder(cmd1).simpleCleanupOnly(true).build().execute();
	}

	private static Pattern PATTERN = Pattern.compile("\\d+");

	private Integer pid;
	static List<String> getPidsFromOutput(String output) {
		List<String> $ = Lists.newArrayList();
		List<String> list = Splitter.on(CharMatcher.anyOf("(\n")).splitToList(output);
		for (String string : list) {
			if (!string.contains(")")) {
				continue;
			}
			List<String> list2 = Splitter.on(CharMatcher.anyOf(")")).splitToList(string);
			for (String string2 : list2) {
				Matcher matcher = PATTERN.matcher(string2);
				if (matcher.matches()) {
					$.add(string2);
				}
			}
		}
		return $ ;
	}

}
