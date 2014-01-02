package codeine.servlet;

import codeine.utils.StringUtils;


@SuppressWarnings("unused")
public class ConfigureCodeineTemplateData extends TemplateData {

	private String configuration;
	private String view_config;
	private String permissions_config;
	private String projects;

	public ConfigureCodeineTemplateData(String config, String view_config, String permissions_config, String projects) {
		this.configuration = config;
		this.permissions_config = permissions_config;
		this.projects = projects;
		this.view_config = StringUtils.isEmpty(view_config) ? "[]" : view_config; 
	}
	
}
