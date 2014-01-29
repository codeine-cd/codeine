package codeine.utils.os_process;

import java.nio.file.attribute.PosixFilePermission;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;

public class ShellScriptWithOutput {

	private static final Logger log = Logger.getLogger(ShellScriptWithOutput.class);
	private String content;
	private String fileName;
	private String key;
	private String runFromDir;
	private Map<String, String> env;

	public ShellScriptWithOutput(String key, String content, String runFromDir, Map<String, String> env) {
		this.key = key;
		this.content = content;
		this.runFromDir = runFromDir;
		this.env = env;
		this.fileName = "/tmp/codeine" + key.hashCode();
	}

	public String create() {
		TextFileUtils.setContents(fileName, content);
		FilesUtils.setPermissions(fileName, 
				PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
				PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
				PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ
				);
		return fileName;
	}

	public void delete() {
		FilesUtils.delete(fileName);
		FilesUtils.delete(getOutputFile());
	}

	public String execute() {
		try {
			create();
			Result result = executeInternal();
			if (!result.success()) {
				log.warn("failed to run script " + key +  " output: " + result.output());
				return "";
			}
			if (!FilesUtils.exists(getOutputFile())) {
				return "";
			}
			return TextFileUtils.getContents(getOutputFile()).trim();
		} finally {
			delete();
		}
	}

	private Result executeInternal() {
		env.put(Constants.EXECUTION_ENV_OUTPUT_FILE, getOutputFile());
		return new ProcessExecuterBuilder(Lists.newArrayList("/bin/sh", "-xe", fileName), runFromDir).env(env).build().execute();
	}

	private String getOutputFile() {
		return fileName + ".output";
	}

}
