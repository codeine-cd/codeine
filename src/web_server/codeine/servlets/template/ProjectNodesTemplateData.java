package codeine.servlets.template;

import java.util.List;

import codeine.servlet.NodeTemplate;
import codeine.servlet.TemplateData;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class ProjectNodesTemplateData extends TemplateData {

	private String projectName;
	private String versionName;
	private String nodesJson;
	private List<NameAndAlias> commands;
	private List<String> monitors;
	private List<NodeTemplate> nodes;
	private boolean readonly;

	
	public ProjectNodesTemplateData(String projectName, String versionName, boolean readonly, List<NodeTemplate> nodes, List<NameAndAlias> commands, List<String> monitors) {
		super();
		this.versionName = versionName;
		this.projectName = projectName;
		this.readonly = readonly;
		this.nodes = nodes;
		this.commands = commands;
		this.monitors = monitors;
		this.nodesJson = new Gson().toJson(nodes);
	}

}
