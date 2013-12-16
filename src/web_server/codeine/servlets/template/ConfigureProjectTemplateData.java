package codeine.servlets.template;

import java.util.List;

import codeine.jsons.nodes.NodeDiscoveryStrategy;
import codeine.jsons.project.ProjectJson;
import codeine.servlet.TemplateData;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class ConfigureProjectTemplateData extends TemplateData {

	private String project_name;
	private List<String> node_discovery_strategy_options;
	private String project_json;
	private String nodes_discovery_script;
	private String version_detection_script;
	
	public ConfigureProjectTemplateData(ProjectJson project) {
		this.project_json = new Gson().toJson(project);
		this.project_name = project.name();
		this.node_discovery_strategy_options = Lists.newArrayList(StringUtils.getEnumNames(NodeDiscoveryStrategy.class));
		this.nodes_discovery_script = StringUtils.safeToString(project.nodes_discovery_script());
		this.version_detection_script = StringUtils.safeToString(project.version_detection_script());
	}
}
