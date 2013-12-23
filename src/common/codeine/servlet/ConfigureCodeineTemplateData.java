package codeine.servlet;


@SuppressWarnings("unused")
public class ConfigureCodeineTemplateData extends TemplateData {

	private String configuration;
	private String view_config;

	public ConfigureCodeineTemplateData(String config, String view_config) {
		configuration = config;
		this.view_config = view_config; 
	}
	
}
