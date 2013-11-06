package codeine.utils.os_process;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import codeine.model.Result;

import com.google.common.base.Function;
import com.google.common.base.Functions;

public class ProcessExecuter {
	
	static final Logger log = Logger.getLogger(ProcessExecuter.class);
	private List<String> cmd;
	private long timeoutInMinutes;
	private List<String> cmdForOutput;
	private Function<String, Void> function;
	private String runFromDir;
	

	private ProcessExecuter(List<String> cmd, List<String> cmdForOutput, long timeoutInMinutes, Function<String, Void> function, String runFromDir) {
		super();
		this.cmd = cmd;
		this.cmdForOutput = cmdForOutput;
		this.timeoutInMinutes = timeoutInMinutes;
		this.function = function;
		this.runFromDir = runFromDir;
	}

	public Result execute() {
		log.debug("executing " + cmd);
		Process process = null;
		ProcessExecuterWorker worker = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.directory(new File(runFromDir));
			pb.redirectErrorStream(true);
			process = pb.start();
			worker = new ProcessExecuterWorker(process, function, cmd);
			worker.start();
			long timeout = TimeUnit.MINUTES.toMillis(timeoutInMinutes);
			worker.join(timeout);
			if (worker.exit != null) {
				return new Result(worker.exit, worker.output);
			} else {
				throw new RuntimeException("command exited with timeout " + cmdForOutput);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
			throw new RuntimeException(ex);
		} finally {
			if (null != process) {
				process.destroy();
			}
		}
	}

	public static class ProcessExecuterBuilder{
		
		private List<String> cmd;
		private List<String> cmdForOutput;
		private long timeoutInMinutes = 2;
		private String runFromDir;
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Function<String, Void> function = (Function)Functions.constant(null);
		
		public ProcessExecuterBuilder(List<String> cmd, String runFromDir) {
			this.cmd = cmd;
			this.cmdForOutput = cmd;
			this.runFromDir = runFromDir;
		}

		public ProcessExecuter build(){
			return new ProcessExecuter(cmd, cmdForOutput, timeoutInMinutes, function, runFromDir);
		}
		
		public ProcessExecuterBuilder cmd(List<String> cmd){
			this.cmd = cmd;
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
