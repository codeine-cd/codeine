package codeine.servlet;

@SuppressWarnings("unused")
public class TemplateLinkWithProgress extends TemplateLink {

	private String success;
	private String warning;
	private String error;
	
	public TemplateLinkWithProgress(String label, String link, String success, String warning, String error) {
		super(label, link);
		this.success = success;
		this.warning = warning;
		this.error = error;
	}

	

}
