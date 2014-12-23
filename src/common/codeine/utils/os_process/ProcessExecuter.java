package codeine.utils.os_process;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import codeine.model.ExitStatus;
import codeine.model.Result;
import codeine.utils.MapUtils;
import codeine.utils.ThreadUtils;
import codeine.utils.os.OsUtils;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProcessExecuter {
	
	private static final Logger log = Logger.getLogger(ProcessExecuter.class);
	private List<String> cmd;
	private long timeoutInMinutes;
	private Function<String, Void> function;
	private String runFromDir;
	private Map<String, String> env;
	private String user;
	private boolean simpleCleanupOnly;
	

	private ProcessExecuter(List<String> cmd, List<String> cmdForOutput, long timeoutInMinutes, Function<String, Void> function, String runFromDir, Map<String, String> env, String user, boolean simpleCleanupOnly) {
		super();
		this.cmd = cmd;
		this.timeoutInMinutes = timeoutInMinutes;
		this.function = function;
		this.runFromDir = runFromDir;
		this.env = env;
		this.user = user;
		this.simpleCleanupOnly = simpleCleanupOnly;
	}

	public Result execute() {
		log.debug("executing " + cmd);
		Process process = null;
		ProcessExecuterWorker worker = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.environment().putAll(env);
			pb.directory(new File(runFromDir));
			pb.redirectErrorStream(true);
			process = pb.start();
			worker = new ProcessExecuterWorker(process, function, cmd);
			worker.start();
			long timeout = TimeUnit.MINUTES.toMillis(timeoutInMinutes);
			worker.join(timeout);
			if (worker.exitStatus() != null) {
				return new Result(worker.exitStatus(), worker.output());
			} else {
				ThreadUtils.sleep(100);
				return new Result(ExitStatus.TIMEOUT, worker.output() + "\n...Got timeout...\n");
			}
		} catch (IOException e) {
			log.warn("got IOException " + e.getMessage());
			String output = null == worker ? "" : worker.output();
			output +=  "\n...Got IOException...\n";
			return new Result(ExitStatus.IO_ERROR, output);
		} catch (InterruptedException ex) {
			log.warn("got InterruptedException " + ex.getMessage());
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw new RuntimeException(ex);
		} finally {
			cleanup(process);
		}
	}

	public void cleanup(Process process) {
		if (null == process) {
			return;
		}
		boolean cleaned = false;
		if (OsUtils.isLinux() && !simpleCleanupOnly) {
			try {
				cleaned = new LinuxProcessCleaner(process, user).cleanup();
			} catch (RuntimeException e) {
				log.warn("failed in cleanup " + cmd + " " + e.getMessage());
			}
		}
		if (!cleaned) {
			process.destroy();
		}
	}

	public static class ProcessExecuterBuilder{
		
		private String user;
		private boolean simpleCleanupOnly;
		private List<String> cmd;
		private List<String> cmdForOutput;
		private long timeoutInMinutes = 2;
		private String runFromDir;
		private Map<String, String> env = Maps.newHashMap();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Function<String, Void> function = (Function)Functions.constant(null);
		
		public ProcessExecuterBuilder(List<String> cmd, String runFromDir) {
			this.cmd = cmd;
			this.cmdForOutput = cmd;
			this.runFromDir = runFromDir;
		}

		public ProcessExecuterBuilder(List<String> cmd) {
			this(cmd, ".");
		}

		public ProcessExecuter build(){
			return new ProcessExecuter(cmd, cmdForOutput, timeoutInMinutes, function, runFromDir, MapUtils.noNullsMap(env), user, simpleCleanupOnly);
		}
		
		public ProcessExecuterBuilder cmd(List<String> cmd){
			this.cmd = cmd;
			return this;
		}
		public ProcessExecuterBuilder user(String user){
			this.user = user;
			return this;
		}
		public ProcessExecuterBuilder simpleCleanupOnly(boolean simpleCleanupOnly){
			this.simpleCleanupOnly = simpleCleanupOnly;
			return this;
		}
		public ProcessExecuterBuilder env(Map<String, String> env){
			this.env = env;
			return this;
		}
		public ProcessExecuterBuilder cmdForOutput(List<String> cmdForOutput){
			this.cmdForOutput = cmdForOutput;
			return this;
		}
		public ProcessExecuterBuilder timeoutInMinutes(long timeoutInMinutes){
			this.timeoutInMinutes = timeoutInMinutes;
			return this;
		}
		public ProcessExecuterBuilder function(Function<String, Void> function){
			this.function = function;
			return this;
		}
		
		
	}

	public static Result execute(String cmd) {
		List<String> cmdList = Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().split(cmd));
		return new ProcessExecuterBuilder(cmdList).build().execute();
	}

	public static String executeSuccess(String cmd) {
		Result r = execute(cmd);
		if (!r.success()) {
			throw new RuntimeException("fail with exit status " +  r.exit());
		}
		return r.output();
	}
	
//	public static void main(String[] args) {
//		ArrayList<String> cmd = Lists.newArrayList("/workarea/oshai/4_2_eclipse/workspace/codeintel/deployment/project/test_project/plugins/restart");
//		Function<String, Void> f = new Function<String, Void>(){
//			@Override
//			public Void apply(String input){
//				System.out.println(input);
//				return null;
//			}
//		};
//		new ProcessExecuter(cmd, cmd, 2, f).execute();
//	}
}
