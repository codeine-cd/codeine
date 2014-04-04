package codeine.servlet;

public class TemplateLink {

	private String label;
	private String link;
	
	public TemplateLink(String label, String link) {
		super();
		this.label = label;
		this.link = link;
	}

	public String label() {
		return label;
	}
	
	public String link() {
		return link;
	}
	
	
}
