package codeine.utils.os_process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import codeine.utils.ExceptionUtils;

import com.google.common.base.Function;

@SuppressWarnings("unused")
class ProcessExecuterWorker extends Thread {
	private final Process process;
	Integer exit;
	String output = "";
	private Function<String, Void> function;
	private List<String> cmd;

	ProcessExecuterWorker(Process process, Function<String, Void> function, List<String> cmd) {
		this.process = process;
		this.function = function;
		this.cmd = cmd;
	}

	@Override
	public void run() {
		try {
			//TODO read error
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			try {
				process.getOutputStream().close();
				while ((line = br.readLine()) != null) {
					output += line + "\n";
					function.apply(line);
				}
			} catch (IOException ex) {
				output += ExceptionUtils.getStackTrace(ex);
				function.apply(ExceptionUtils.getStackTrace(ex));
			}
			exit = process.waitFor();
		} catch (InterruptedException ignore) {
			ProcessExecuter.log.info("thread was interuppted");
			return;
		}
	}
}