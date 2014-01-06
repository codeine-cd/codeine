package codeine.servlets.template;

import java.util.List;

import codeine.servlet.TemplateData;
import codeine.utils.network.HttpUtils;
import codeine.version.VersionItemTemplate;

@SuppressWarnings("unused")
public class ProjectStatusTemplateData extends TemplateData {
	
	private List<VersionItemTemplate> version;
	private List<NameAndAlias> commands;
	private int total;
	private String project_name;
	private String project_name_encoded;
	private boolean readonly;
	private String project_nodes_context;
	
	
	public ProjectStatusTemplateData(String projectName, List<VersionItemTemplate> version, int total, List<NameAndAlias> commands, boolean readOnly, String project_nodes_context) {
		super();
		this.project_name = projectName;
		this.project_nodes_context = project_nodes_context;
		this.project_name_encoded = HttpUtils.encode(projectName);
		this.version = version;
		this.total = total;
		this.commands = commands;
		this.readonly = readOnly;
	}

}
