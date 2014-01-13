package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ConfigureProjectTemplateData;
import codeine.utils.JsonUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;

public class ConfigureProjectServlet extends AbstractFrontEndServlet {

	@Inject private ConfigurationManagerServer configurationManager;
	@Inject private  PermissionsManager permissionsManager;
	
	private static final Logger log = Logger.getLogger(ConfigureProjectServlet.class);
	private static final long serialVersionUID = 1L;
	
	protected ConfigureProjectServlet() {
		super("configure_project");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("configure_project", "command_history");
	}
	
	@Override 
	protected List<String> getJsRenderTemplateFiles() {
		return Lists.newArrayList("configure_command_parameter", "configure_project_command", "configure_project_monitor","nodes_table_row", "mail_policy_table_row", "command_startegy");
	};
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		String user = permissionsManager.user(request);
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		if (!permissionsManager.canConfigure(projectName, request)) {
			log.info("User " + user + " is not allowed to configure project " + projectName);
			return false;
		}
		return true;
	};
	
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) {
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		log.info("Updating configuration of " + projectName + ", new configuration is " + data);
		ProjectJson projectJson = gson().fromJson(data, ProjectJson.class);
		configurationManager.updateProject(projectJson);
		writer.write("{}");
		return TemplateData.emptyTemplateData();
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		ProjectJson projectJson = JsonUtils.cloneJson(configurationManager.getProjectForName(projectName), ProjectJson.class);
		return new ConfigureProjectTemplateData(projectJson);
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return "Configure " + projectName;
	}
		
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?"+Constants.UrlParameters.PROJECT_NAME+"=" + HttpUtils.encode(projectName)),new TemplateLink("Configure", "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}

}
