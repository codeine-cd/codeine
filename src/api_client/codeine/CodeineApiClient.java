package codeine;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import codeine.api.CommandStatusJson;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.api.VersionItemInfo;
import codeine.jsons.project.CodeineProject;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.Constants.UrlParameters;
import codeine.permissions.UserProjectPermissions;
import codeine.servlets.api_servlets.projects.CreateNewProjectJson;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class CodeineApiClient {

	private Logger log = Logger.getLogger(CodeineApiClient.class);
	
	private String host;
	private int port;
	private Gson gson = new Gson();
	private Map<String,String> headers = Maps.newHashMap();

	public CodeineApiClient(String host, int port) {
		this(host,port,null);
	}
	
	public CodeineApiClient(String host, int port, String api_token) {
		this.host = host;
		this.port = port;
		if (!StringUtils.isEmpty(api_token)) {
			headers.put(Constants.API_TOKEN, api_token);
		}
		headers.put(Constants.RequestHeaders.NO_ZIP, Constants.RequestHeaders.NO_ZIP);
	}

	public List<CodeineProject> projects() {
		return apiCall(Constants.PROJECTS_LIST_CONTEXT,"", new TypeToken<List<CodeineProject>>(){}.getType());
	}
	
	public void report(NodeWithMonitorsInfo nodeWithMonitorsInfo) {
		apiPostCall(Constants.REPORTER_CONTEXT, "", nodeWithMonitorsInfo);
	}
	public void createProject(CreateNewProjectJson newProjectProperties) {
		apiPostCall(Constants.PROJECTS_LIST_CONTEXT, "", newProjectProperties);
	}


	private <T> T apiCall(String path, String params, Type type) {
		String json = HttpUtils.doGET(getServerPath(path) + params,headers, HttpUtils.SHORT_READ_TIMEOUT_MILLI);
		return gson.fromJson(json, type);
	}
	
	private String apiPostCall(String path, String params, Object bodyData) {
		return HttpUtils.doPOST(getServerPath(path) + params, gson.toJson(bodyData),headers);
	}


	private String getServerPath(String contextPath) {
		return "http://"+host+":"+port + Constants.apiTokenContext(contextPath);
	}

	public Map<String, VersionItemInfo> projectStatus(String projectName) {
		return apiCall(
				Constants.PROJECT_STATUS_CONTEXT,"?" + projectNameParam(projectName) ,
				new TypeToken<Map<String, VersionItemInfo>>(){}.getType());
	}

	private String projectNameParam(String projectName) {
		return Constants.UrlParameters.PROJECT_NAME + "=" + HttpUtils.encodeURL(projectName);
	}

	public List<NodeWithMonitorsInfo> projectNodes(String projectName, String version) {
		return apiCall(
				Constants.PROJECT_NODES_CONTEXT,"?" + projectNameParam(projectName)  + "&" + Constants.UrlParameters.VERSION_NAME + "=" + HttpUtils.encodeURL(version),
				new TypeToken<List<NodeWithMonitorsInfo>>(){}.getType());
	}



	public ProjectJson project(String projectName) {
		return apiCall(Constants.PROJECT_CONFIGURATION_CONTEXT, "?" + projectNameParam(projectName), ProjectJson.class);
	}



	public String runCommand(ScehudleCommandExecutionInfo data) {
		String url = getServerPath(Constants.COMMAND_NODES_CONTEXT);
		String postData = UrlParameters.DATA_NAME + "=" + HttpUtils.encodeURL(gson.toJson(data));
		return HttpUtils.doPOST(url, postData,headers);
	}



	public List<CommandStatusJson> commandHistory(String projectName) {
		return apiCall(Constants.COMMANDS_LOG_CONTEXT, "?" + projectNameParam(projectName) , new TypeToken<List<CommandStatusJson>>(){}.getType()); 
	}
	
	public String saveProject(ProjectJson project) {
		String url = getServerPath(Constants.PROJECT_CONFIGURATION_CONTEXT);
		return HttpUtils.doPUT(url, gson.toJson(project),headers);
	}

	public void addCommandPermissionsToProject(String projectName, String user, String nodeAlias) {
		log.info("addCommandPermissionsToProject " + projectName + " " + user + " " + nodeAlias);
		ProjectJson project = project(projectName);
		boolean found = false;
		for (UserProjectPermissions p : project.permissions()) {
			if (p.username().equals(user)) {
				p.can_command().add(nodeAlias);
				found = true;
				break;
			}
		}
		if (!found) {
			Set<String> can_command = Sets.newHashSet(nodeAlias);
			project.permissions().add(new UserProjectPermissions(user, false, can_command, true));
		}
		saveProject(project);
	}
	
	public void addNodeToProject(String projectName, NodeInfo nodeInfo) {
		log.info("addNodeToProject " + projectName + " " + nodeInfo);
		ProjectJson project = project(projectName);
		for (NodeInfo n : project.nodes_info()) {
			if (n.name().equals(nodeInfo.name())) {
				throw new IllegalArgumentException("node name already defined " + nodeInfo.name());
			}
			if (n.alias().equals(nodeInfo.alias())) {
				throw new IllegalArgumentException("node alias already defined " + nodeInfo.alias());
			}
		}
		project.nodes_info().add(nodeInfo);
		saveProject(project);
	}

	public NodeInfo removeNodeFromProjectByAlias(String projectName, String alias) {
		ProjectJson project = project(projectName);
		NodeInfo nodeFound = null;
		for (Iterator<NodeInfo> iterator = project.nodes_info().iterator(); iterator.hasNext();) {
			NodeInfo node = iterator.next();
			if (node.alias().equals(alias)) {
				iterator.remove();
				nodeFound = node;
				break;
			}
		}
		if (null == nodeFound) {
			throw new IllegalArgumentException("node alias not found " + alias);
		}
		for (UserProjectPermissions p : project.permissions()) {
			p.can_command().remove(alias);
		}
		saveProject(project);
		return nodeFound;
	}
}
