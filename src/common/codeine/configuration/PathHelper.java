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
		return getProjectsDir() + "/" + projectName + Constants.MONITORS_DIR;
	}

	public String getPluginsDir(String projectName) {
		return getProjectsDir() + "/" + projectName + Constants.PLUGINS_DIR;
	}
	
	public String getPluginsOutputDir(String projectName) {
		return getProjectsDir() + "/" + projectName + Constants.PLUGINS_OUTPUT_DIR;
	}

	public String getMonitorOutputDir(String projectName) {
		return getMonitorOutputDirAllProjects() + "/" + projectName + Constants.MONITOR_OUTPUT_CONTEXT + Constants.NODE_PATH;
	}
	public String getMonitorOutputDirAllProjects() {
		return getProjectsDir();
	}

	public String getMonitorOutputDirWithNode(String projectName, String nodeName) {
		return getMonitorOutputDir(projectName) + "/" + HttpUtils.specialEncode(nodeName);
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
		return getPersistentDir() + "/VersionsMapping.json";
	}
	public String getStatisticsFile() {
		return getPersistentDir() + "/statistics.data";
	}

	public String getPidFile() {
		return Constants.getHostWorkareaDir() + "/codeine.peer.pid";
	}

	public String getPortFile() {
		return Constants.getHostWorkareaDir() + "/codeine.peer.port";
	}

	public String getVersionLabelFile(String projectName) {
		return getConfDir(projectName) + "/VersionLabel.json";
	}

	public String getConfDir(String projectName) {
		return getProjectsDir() + "/" + projectName;
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
			log.debug("Found monitors: " + files);
		}
		return files;
	}

	public List<File> getPlugins(String project) {
		return getFilesForDir(getPluginsDir(project));
	}

	public String getProjectDir(String projectName) {
		return getProjectsDir() + "/" + projectName;
	}

}
