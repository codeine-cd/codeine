package codeine.servlet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.model.Constants;
import codeine.utils.UrlUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class MenuProvider {

	private @Inject PermissionsManager permissionsManager;
	
	public List<TemplateLinkWithIcon> getMainMenu(HttpServletRequest request) {
		ArrayList<TemplateLinkWithIcon> $ =Lists.newArrayList(new TemplateLinkWithIcon("Projects", "/","projectsMenuItem","icon-th-list"));
		if (permissionsManager.isAdministrator(request)) {
			$.add(new TemplateLinkWithIcon("New Project", UrlUtils.buildUrl(Constants.NEW_PROJECT_CONTEXT,null) , "icon-file", "newProjectMenuItem" , "admin"));
			$.add(new TemplateLinkWithIcon("Manage Codeine", UrlUtils.buildUrl(Constants.MANAGEMENT_CONTEXT,null) ,"icon-wrench", "manageMenuItem", "admin"));
		}
		return $;
	}
	
	public List<TemplateLinkWithIcon> getProjectMenu(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ArrayList<TemplateLinkWithIcon> $ = Lists.newArrayList(new TemplateLinkWithIcon("Status", UrlUtils.buildUrl(Constants.PROJECT_STATUS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)) ,"statusMenuItem","icon-signal"),
								  new TemplateLinkWithIcon("Nodes", UrlUtils.buildUrl(Constants.PROJECT_NODES_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName,Constants.UrlParameters.VERSION_NAME, "All versions")), "nodesMenuItem", "icon-list-alt"),
								  new TemplateLinkWithIcon("Labels", UrlUtils.buildUrl(Constants.LABELS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)), "lablesMenuItem","icon-tags")
								  
				);
		if (permissionsManager.isAdministrator(request)) {
			$.add(new TemplateLinkWithIcon("Delete", "javascript: void(0);",  "icon-remove-sign", "deleteProjectMenuItem", "admin"));
		}
		if (permissionsManager.canConfigure(projectName, request)) {
			$.add(new TemplateLinkWithIcon("Configure", UrlUtils.buildUrl(Constants.CONFIGURE_PROJECT_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)) ,"configureMenuItem" ,"icon-cog"));
		}
		return $;
	}
	
}
