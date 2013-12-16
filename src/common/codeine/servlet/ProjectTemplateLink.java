package codeine.servlet;

public class ProjectTemplateLink extends TemplateLink {

	@SuppressWarnings("unused")
	private int nodes;

	public ProjectTemplateLink(String label, String link, int nodes) {
		super(label, link);
		this.nodes = nodes;
	}

}
