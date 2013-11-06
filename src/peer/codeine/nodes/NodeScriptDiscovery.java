package codeine.nodes;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.configuration.PathHelper;
import codeine.jsons.nodes.NodeListJson;
import codeine.model.Result;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class NodeScriptDiscovery {

	private static final Logger log = Logger.getLogger(NodeScriptDiscovery.class);
	@Inject private Gson gson;
	@Inject private PathHelper pathHelper;
	
	public NodeListJson get(String projectName) {
		String dir = pathHelper.getPluginsDir(projectName);
		String p = dir + "/discovery";
		List<String> cmd = Lists.newArrayList(p, projectName);
		Result result = new ProcessExecuterBuilder(cmd, dir).build().execute();
		if (!result.success()){
			throw new RuntimeException("error discovering nodes" + result.output);
		}
		if (result.output.trim().isEmpty()){
			return new NodeListJson();
		}
		String json = result.output;
		NodeListJson nodeListJson = null;
		try {
			nodeListJson = gson.fromJson(json, NodeListJson.class);
		} catch (JsonSyntaxException e) {
			log.warn("json is " + json);
			log.warn("failed to parsed nodes from discovery ", e);
		}
		if (null == nodeListJson){
			return new NodeListJson();
		}
		return nodeListJson;
	}

}
