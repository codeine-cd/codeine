package codeine.servlets.template;

import java.util.List;

import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class ProjectNodesTemplateData extends TemplateData {

	private String projectName;
	private String versionName;
	private List<NameAndAlias> commands;
	private List<String> monitors;
	private boolean readonly;

	
	public ProjectNodesTemplateData(String projectName, String versionName, boolean readonly, List<NameAndAlias> commands, List<String> monitors) {
		super();
		this.versionName = versionName;
		this.projectName = projectName;
		this.readonly = readonly;
		this.commands = commands;
		this.monitors = monitors;
	}

}
