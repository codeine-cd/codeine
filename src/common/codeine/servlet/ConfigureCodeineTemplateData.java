package codeine.servlet;

import codeine.utils.StringUtils;


@SuppressWarnings("unused")
public class ConfigureCodeineTemplateData extends TemplateData {

	private String configuration;
	private String view_config;

	public ConfigureCodeineTemplateData(String config, String view_config) {
		this.configuration = config;
		this.view_config = StringUtils.isEmpty(view_config) ? "[]" : view_config; 
	}
	
}
