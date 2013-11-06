package codeine.servlets.template;

import java.util.List;

import codeine.servlet.TemplateData;
import codeine.version.VersionItem;

@SuppressWarnings("unused")
public class ProjectStatusTemplateData extends TemplateData {
	
	private List<VersionItem> version;
	private List<NameAndAlias> commands;
	private int total;
	private String projectName;
	private boolean readonly;
	
	
	public ProjectStatusTemplateData(String projectName, List<VersionItem> version, int total, List<NameAndAlias> commands, boolean readOnly) {
		super();
		this.projectName = projectName;
		this.version = version;
		this.total = total;
		this.commands = commands;
		this.readonly = readOnly;
	}

}
