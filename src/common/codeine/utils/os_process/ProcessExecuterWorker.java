package codeine.utils.os_process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.model.ExitStatus;
import codeine.utils.ExceptionUtils;
import codeine.utils.StringUtils;

import com.google.common.base.Function;

public class ProcessExecuterWorker extends Thread {
	
	private static final Logger log = Logger.getLogger(ProcessExecuterWorker.class);
	
	private final Process process;
	private volatile Integer exit;
	private String output = "";
	private String error = "";
	private Function<String, Void> function;
//	private List<String> cmd;

	ProcessExecuterWorker(Process process, Function<String, Void> function, List<String> cmd) {
		this.process = process;
		this.function = function;
//		this.cmd = cmd;
	}

	@Override
	public void run() {
		try {
			try {
				process.getOutputStream().close();
				InputStream is1 = process.getInputStream();
				InputStreamReader isr1 = new InputStreamReader(is1);
				BufferedReader br1 = new BufferedReader(isr1);
				String line;
				while ((line = br1.readLine()) != null) {
					output += line + "\n";
					function.apply(line);
				}
				InputStream is = process.getErrorStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				while ((line = br.readLine()) != null) {
					error += line + "\n";
					function.apply(line);
				}
				exit = process.waitFor();
			} catch (IOException ex) {
				log.warn("got IO-Exception " + ex.getMessage());
				error += ExceptionUtils.getStackTrace(ex);
				function.apply(ExceptionUtils.getStackTrace(ex));
				exit = ExitStatus.IO_ERROR;
			}
		} catch (InterruptedException e) {
			log.warn("got Interrupted-Exception " + e.getMessage());
			error += "\nprocess was interrupted\n";
			exit = ExitStatus.INTERRUPTED;
			log.info("thread was interuppted");
			return;
		}
	}

	public Integer exitStatus() {
		return exit;
	}

	public String output() {
		return StringUtils.isEmpty(error) ? output : "\noutput:\n=======\n" + output + "\nerror:\n======\n" + error;
	}
}