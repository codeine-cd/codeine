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
	private @Inject PrepareForShutdown prepareForShutdown;
	
	public List<TemplateLinkWithIcon> getMainMenu(HttpServletRequest request) {
		ArrayList<TemplateLinkWithIcon> $ =Lists.newArrayList(new TemplateLinkWithIcon("Projects", "/","projectsMenuItem","fa fa-th-list fa-fw"));
		if (permissionsManager.isAdministrator(request)) {
			$.add(new TemplateLinkWithIcon("New Project", UrlUtils.buildUrl(Constants.NEW_PROJECT_CONTEXT,null) , "fa fa-file fa-fw", "newProjectMenuItem" , "admin"));
			$.add(new TemplateLinkWithIcon("Manage Codeine", UrlUtils.buildUrl(Constants.CONFIGURE_CONTEXT,null) ,"fa fa-wrench fa-fw", "manageMenuItem", "admin"));
		}
		return $;
	}
	
	
	public List<TemplateLinkWithIcon> getManageMenu(HttpServletRequest request) {
		ArrayList<TemplateLinkWithIcon> $ =Lists.newArrayList();
		$.add(new TemplateLinkWithIcon("Configure Codeine", UrlUtils.buildUrl(Constants.CONFIGURE_CONTEXT,null) ,"fa fa-gears fa-fw", "configureMenuItem", "admin"));
		$.add(new TemplateLinkWithIcon("Codeine Nodes Status", UrlUtils.buildUrl(Constants.CODEINE_STATUS_CONTEXT,ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, Constants.CODEINE_NODES_PROJECT_NAME)) ,"fa fa-signal fa-fw", "codeineStatusMenuItem", "admin"));
		$.add(new TemplateLinkWithIcon("Codeine Nodes Info", UrlUtils.buildUrl(Constants.CODEINE_NODES_CONTEXT,ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, Constants.CODEINE_NODES_PROJECT_NAME,Constants.UrlParameters.VERSION_NAME, Constants.ALL_VERSION)) ,"fa fa-list-alt fa-fw", "codeineNodesMenuItem", "admin"));
		if (prepareForShutdown.isSequnceActivated()) {
			$.add(new TemplateLinkWithIcon("Cancel shutdown", UrlUtils.buildUrl(Constants.CANCEL_SHUTDOWN_CONTEXT ,ImmutableMap.of(Constants.UrlParameters.REDIRECT, "true")) ,"fa fa-eraser fa-fw", "prepareForShutdownMenuItem", "admin"));
		}
		else {
			$.add(new TemplateLinkWithIcon("Prepare for shutdown", UrlUtils.buildUrl(Constants.PREPARE_FOR_SHUTDOWN_CONTEXT ,ImmutableMap.of(Constants.UrlParameters.REDIRECT, "true")) ,"fa fa-bullhorn fa-fw", "prepareForShutdownMenuItem", "admin"));
		}
		$.add(new TemplateLinkWithIcon("Codeine Server Logs", UrlUtils.buildUrl(Constants.RESOURCESS_CONTEXT,null) ,"fa fa-suitcase fa-fw", "logsMenuItem", "admin"));
		return $;
	}
	
	public List<TemplateLinkWithIcon> getProjectMenu(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ArrayList<TemplateLinkWithIcon> $ = Lists.newArrayList(new TemplateLinkWithIcon("Status", UrlUtils.buildUrl(Constants.PROJECT_STATUS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)) ,"statusMenuItem","fa fa-signal fa-fw"),
								  new TemplateLinkWithIcon("Nodes", UrlUtils.buildUrl(Constants.PROJECT_NODES_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName,Constants.UrlParameters.VERSION_NAME, Constants.ALL_VERSION)), "nodesMenuItem", "fa fa-list-alt fa-fw")
//								  new TemplateLinkWithIcon("Labels", UrlUtils.buildUrl(Constants.LABELS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)), "lablesMenuItem","fa fa-tags")
								  
				);
		if (permissionsManager.isAdministrator(request)) {
			$.add(new TemplateLinkWithIcon("Delete", "javascript: void(0);",  "fa fa-times-circle fa-fw", "deleteProjectMenuItem", "admin"));
		}
		if (permissionsManager.canConfigure(projectName, request)) {
			$.add(new TemplateLinkWithIcon("Configure", UrlUtils.buildUrl(Constants.CONFIGURE_PROJECT_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName)) ,"configureMenuItem" ,"fa fa-cog fa-fw"));
		}
		return $;
	}
	
}
