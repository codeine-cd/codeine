package codeine;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import codeine.api.CommandStatusJson;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.api.VersionItemInfo;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.Constants.UrlParameters;
import codeine.utils.StringUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class CodeineApiClient {

	private String host;
	private int port;
	private Gson gson = new Gson();
	private Map<String,String> headers;

	public CodeineApiClient(String host, int port) {
		this(host,port,null);
	}
	
	public CodeineApiClient(String host, int port, String api_token) {
		this.host = host;
		this.port = port;
		if (!StringUtils.isEmpty(api_token)) {
			headers = Maps.newHashMap();
			headers.put(Constants.API_TOKEN, api_token);
		}
	}

	public List<ProjectJson> projects() {
		return apiCall(Constants.PROJECTS_LIST_CONTEXT,"", new TypeToken<List<ProjectJson>>(){}.getType());
	}
	
	public void report(NodeWithMonitorsInfo nodeWithMonitorsInfo) {
		apiPostCall(Constants.REPORTER_CONTEXT, "", nodeWithMonitorsInfo);
	}


	private <T> T apiCall(String path, String params, Type type) {
		String json = HttpUtils.doGET(getServerPath(path) + params,headers, HttpUtils.SHORT_READ_TIMEOUT_MILLI);
		return gson.fromJson(json, type);
	}
	
	private String apiPostCall(String path, String params, Object bodyData) {
		return HttpUtils.doPOST(getServerPath(path) + params, gson.toJson(bodyData),headers);
	}


	private String getServerPath(String contextPath) {
		return "http://"+host+":"+port + Constants.apiContext(contextPath);
	}

	public Map<String, VersionItemInfo> projectStatus(String projectName) {
		return apiCall(
				Constants.PROJECT_STATUS_CONTEXT,"?" + projectNameParam(projectName) ,
				new TypeToken<Map<String, VersionItemInfo>>(){}.getType());
	}



	private String projectNameParam(String projectName) {
		return Constants.UrlParameters.PROJECT_NAME + "=" + HttpUtils.encodeURL(projectName);
	}


//TODO
//	public List<NodeWithMonitorsInfo> projectNodes(String projectName, String version) {
//		return apiCall(
//				Constants.PROJECT_NODES_CONTEXT,"?" + projectNameParam(projectName)  + "&" + Constants.UrlParameters.VERSION_NAME + "=" + HttpUtils.encodeURL(version),
//				new TypeToken<List<NodeWithMonitorsInfo>>(){}.getType());
//	}



	public ProjectJson project(String name) {
		for (ProjectJson p : projects()) {
			if (p.name().equals(name)){
				return p;
			}
		}
		throw new IllegalArgumentException("no project " + name);
	}



	public String runCommand(ScehudleCommandExecutionInfo data) {
		String url = getServerPath(Constants.COMMAND_NODES_CONTEXT);
		String postData = UrlParameters.DATA_NAME + "=" + HttpUtils.encodeURL(gson.toJson(data));
		return HttpUtils.doPOST(url, postData,headers);
	}



	public List<CommandStatusJson> commandHistory(String projectName) {
		return apiCall(Constants.COMMANDS_LOG_CONTEXT, "?" + projectNameParam(projectName) , new TypeToken<List<CommandStatusJson>>(){}.getType()); 
	}
	
	public static void main(String[] args) {
		CodeineApiClient api = new CodeineApiClient("localhost", 12347, "36b887b6-fe4e-46c9-838f-abc3feee180f");
		
		System.out.println(api.projects());
		//api.report(new NodeWithMonitorsInfo("localhost", "roi", "test_project",  Maps.<String,MonitorStatusInfo>newHashMap(),"1"));
	}
}
