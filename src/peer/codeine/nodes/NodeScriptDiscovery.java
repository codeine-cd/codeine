package codeine.nodes;

import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.jsons.nodes.NodeListJson;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.os_process.ShellScriptWithOutput;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class NodeScriptDiscovery {

	private static final Logger log = Logger.getLogger(NodeScriptDiscovery.class);
	@Inject private Gson gson;
	@Inject private PathHelper pathHelper;
	
	public NodeListJson get(ProjectJson projectJson) {
		String nodes_discovery_script = projectJson.nodes_discovery_script();
		String dir = pathHelper.getProjectDir(projectJson.name());
		Map<String, String> env = Maps.newHashMap();
		env.put(Constants.EXECUTION_ENV_PROJECT_NAME, projectJson.name());
		ShellScriptWithOutput shellScript = 
				new ShellScriptWithOutput("discovery_" + projectJson.name(), nodes_discovery_script, dir, env);
		String result = shellScript.execute();
		if (result.trim().isEmpty()){
			return new NodeListJson();
		}
		NodeListJson nodeListJson = null;
		try {
			nodeListJson = gson.fromJson(result, NodeListJson.class);
		} catch (JsonSyntaxException e) {
			log.warn("json is " + result);
			log.warn("failed to parse nodes from discovery in project " + projectJson.name(), e);
		}
		if (null == nodeListJson){
			return new NodeListJson();
		}
		return nodeListJson;
	}

}
