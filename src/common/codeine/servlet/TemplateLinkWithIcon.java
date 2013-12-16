package codeine.servlet;

@SuppressWarnings("unused")
public class TemplateLinkWithIcon extends TemplateLink {

	private String icon, active = "", id;
	private String perrmission_class;

	public TemplateLinkWithIcon(String label, String link, String id, String icon) {
		this(label, link, icon, id, "");
	}
	
	public TemplateLinkWithIcon(String label, String link, String icon, String id, String perrmission_class) {
		super(label, link);
		this.icon = icon;
		this.id = id;
		this.perrmission_class = perrmission_class;
	}
	
	public void setActive() {
		active ="active";
	}

}
