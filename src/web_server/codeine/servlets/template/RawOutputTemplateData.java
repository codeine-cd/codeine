package codeine.servlets.template;

import codeine.servlet.TemplateData;

public class RawOutputTemplateData extends TemplateData {

	@SuppressWarnings("unused")
	private String content;

	public RawOutputTemplateData(String content) {
		super();
		this.content = content;
	}

}
