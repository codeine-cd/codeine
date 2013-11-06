package codeine.servlet;

@SuppressWarnings("unused")
public class TemplateLinkWithIcon extends TemplateLink {

	private String icon, active = "";

	public TemplateLinkWithIcon(String label, String link, String icon) {
		super(label, link);
		this.icon = icon;
	}
	
	public void setActive() {
		active ="class='active'";
	}

}
