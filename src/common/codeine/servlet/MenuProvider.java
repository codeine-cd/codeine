package codeine.servlet;

import java.util.List;

import codeine.model.Constants;
import codeine.utils.UrlUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class MenuProvider {

	public List<TemplateLinkWithIcon> getMainMenu() {
		return Lists.newArrayList(
				new TemplateLinkWithIcon("Projects", "/","icon-th-list"),
				new TemplateLinkWithIcon("New project", "#","icon-file"),
				new TemplateLinkWithIcon("Manage codeine", UrlUtils.buildUrl(Constants.MANAGEMENT_CONTEXT,null) ,"icon-wrench")
		);
	}
	
	public List<TemplateLinkWithIcon> getProjectMenu(String projectName) {
		
		
		return Lists.newArrayList(new TemplateLinkWithIcon("Status", UrlUtils.buildUrl(Constants.AGGREGATE_NODE_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)),"icon-signal"),
								  new TemplateLinkWithIcon("Nodes", UrlUtils.buildUrl(Constants.DASHBOARD_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName,Constants.UrlParameters.VERSION_NAME, "All versions")),"icon-list-alt"),
								  new TemplateLinkWithIcon("Labels", UrlUtils.buildUrl(Constants.LABELS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)),"icon-tags"),
								  new TemplateLinkWithIcon("Configure", "#","icon-cog")
								  
				);
	}
	
}
