package codeine.servlet;

public class MonitorTemplateLink extends TemplateLink {

	private String class_name;

	public MonitorTemplateLink(String label, String link, String class_name) {
		super(label, link);
		this.class_name = class_name;
	
	}

}
