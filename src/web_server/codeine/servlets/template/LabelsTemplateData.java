package codeine.servlets.template;

import java.util.List;

import codeine.jsons.labels.ProjectLabelVersionJson;
import codeine.servlet.TemplateData;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class LabelsTemplateData extends TemplateData {

	private String labelsJson;
	private String projectName;
	private boolean readOnly;

	public LabelsTemplateData(String projectName, List<ProjectLabelVersionJson> label, boolean readOnly) {
		super();
		this.readOnly = readOnly;
		this.labelsJson = new Gson().toJson(label);
		this.projectName = projectName;
	}

}
