package codeine.configuration;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.utils.FilesUtils;
import codeine.utils.network.HttpUtils;

public class PathHelper {

	private static final Logger log = Logger.getLogger(PathHelper.class);

	public String getMonitorsDir(String projectName) {
		return getProjectsDir() + File.separator + projectName + Constants.MONITORS_DIR;
	}

	public String getCommandsDir(String projectName) {
		return getProjectsDir() + File.separator + projectName + Constants.COMMANDS_DIR;
	}
	
	public String getAllCommandsInProjectOutputDir(String projectName) {
		return getProjectsDir() + File.separator + projectName + Constants.COMMANDS_OUTPUT_DIR;
	}

	public String getCommandOutputDir(String projectName, String command) {
		return getAllCommandsInProjectOutputDir(projectName) + File.separator + command;
	}
	
	public String getMonitorOutputDir(String projectName) {
		return getMonitorOutputDirAllProjects() + File.separator + projectName + Constants.MONITOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
	}
	public String getCollectorOutputDir(String projectName) {
		return getMonitorOutputDirAllProjects() + File.separator + projectName + Constants.COLLECTOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
	}
	public String getMonitorOutputDirAllProjects() {
		return getProjectsDir();
	}

	public String getMonitorOutputDirWithNode(String projectName, String nodeName) {
		return getMonitorOutputDir(projectName) + File.separator + HttpUtils.specialEncode(nodeName);
	}
	public String getCollectorOutputDirWithNode(String projectName, String nodeName) {
		return getCollectorOutputDir(projectName) + File.separator + HttpUtils.specialEncode(nodeName);
	}

	public String getProjectsDir() {
		return Constants.getWorkarea() + Constants.PROJECT_PATH;
	}

	public static String getReadLogs() {
		return Constants.getHostWorkareaDir() + "/bin/readLogs";
	}

	public static String getTarFile() {
		return Constants.getHostWorkareaDir() + "/tars/codeine.tar.gz";
	}

	public String getPersistentDir() {
		return Constants.getPersistentDir();
	}

	public String getVersionMappingFile() {
		return getPersistentDir() + File.separator + "VersionsMapping.json";
	}
	public String getStatisticsFile() {
		return getPersistentDir() + File.separator + "statistics.data";
	}

	public String getPidFile() {
		return Constants.getHostWorkareaDir() + File.separator + "codeine.peer.pid";
	}

	public String getPortFile() {
		return Constants.getHostWorkareaDir() + File.separator + "codeine.peer.port";
	}

	public String getVersionLabelFile(String projectName) {
		return getConfDir(projectName) + File.separator + "VersionLabel.json";
	}

	public String getConfDir(String projectName) {
		return getProjectsDir() + File.separator + projectName;
	}

	public List<File> getMonitors(String project) {
		return getFilesForDir(getMonitorsDir(project));
	}

	private List<File> getFilesForDir(String dir) {
		log.debug("Collecting monitors from " + dir);
		List<File> files = FilesUtils.listFiles(dir);
		if (files.isEmpty()) {
			log.debug("No files found to execute under " + dir);
		} else {
			log.debug("Found files: " + files);
		}
		return files;
	}

	public List<File> getCommandsOutput(String project) {
		return getFilesForDir(getAllCommandsInProjectOutputDir(project));
	}

	public String getProjectDir(String projectName) {
		return getProjectsDir() + File.separator + projectName;
	}

	public String getCommandOutputInfoFile(String projectName, String command) {
		return getCommandOutputDir(projectName, command) + File.separator + Constants.JSON_COMMAND_FILE_NAME;
	}

	public String getCommandOutputFile(String projectName, String command) {
		return getCommandOutputDir(projectName, command) + File.separator + Constants.COMMAND_LOG_FILE;
	}

	

}
