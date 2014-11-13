package codeine.utils.os_process;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;
import codeine.utils.os.OperatingSystem;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;

public class ShellScript {

	private static final Logger log = Logger.getLogger(ShellScript.class);
	
	private String content;
	private String fileName;
	private String key;
	private String tmpDir;
	private OperatingSystem operatingSystem;
	private String runFromDir;
	private Map<String, String> env;

	public ShellScript(String key, String content, OperatingSystem operatingSystem, String tmp_dir, String runFromDir, Map<String, String> env) {
		this.key = key;
		this.content = content;
		this.operatingSystem = operatingSystem;
		this.runFromDir = runFromDir;
		this.env = env;
		this.tmpDir = StringUtils.isEmpty(tmp_dir) ? System.getProperty("java.io.tmpdir") : tmp_dir;
	}

	private boolean windows() {
		return operatingSystem == OperatingSystem.Windows;
	}
	
	public String create() {
		content = content.replace("\n", System.lineSeparator());
		String fileNameNoDir = "codeine_" + key.hashCode() + (windows() ? ".bat" : ".sh");
		this.fileName = tmpDir + File.separator + fileNameNoDir;
		try {
			TextFileUtils.setContents(fileName, content);
		}
		catch (RuntimeException ex){
			log.warn("failed to write to file " + fileName);
			if (ExceptionUtils.getRootCause(ex) instanceof IOException && !windows()) {
				String filename2 = Constants.getPersistentDir() + File.separator + fileNameNoDir;
				try {
					TextFileUtils.setContents(filename2, content);
					fileName = filename2;
				}
				catch (RuntimeException ex1){
					log.warn("fail to write to backup file " + filename2);
					throw ex;
				}
			}
			else {
				throw ex;
			}
		}
		if (!windows()) {
			FilesUtils.setPermissions(fileName, 
					PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
					PosixFilePermission.GROUP_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
					PosixFilePermission.OTHERS_EXECUTE, PosixFilePermission.OTHERS_READ
					);
		} 
		return fileName;
	}

	public void delete() {
		FilesUtils.delete(fileName);
		FilesUtils.delete(getOutputFile());
	}
	
	public Result execute() {
		try {
			create();
			Result result = executeInternal();
			String outputFromFile = "";
			if (FilesUtils.exists(getOutputFile())) {
				outputFromFile = TextFileUtils.getContents(getOutputFile()).trim();
			}
			result.outputFromFile(outputFromFile);
			return result;
		} finally {
			delete();
		}
	}

	private Result executeInternal() {
		env.put(Constants.EXECUTION_ENV_OUTPUT_FILE, getOutputFile());
		List<String> cmd = null;
		switch (operatingSystem) {
		case Linux:
			cmd = Lists.newArrayList("/bin/sh", "-xe", fileName);
			break;
		case Windows:
			cmd = Lists.newArrayList("cmd", "/c", "call", fileName);
			break;
		default:
			throw new RuntimeException("missing implementation for " + operatingSystem);
		}
		return new ProcessExecuterBuilder(cmd, runFromDir).env(env).build().execute();
	}

	private String getOutputFile() {
		return fileName + ".output";
	}

}
