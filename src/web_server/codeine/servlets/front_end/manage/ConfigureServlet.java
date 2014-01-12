package codeine.servlets.front_end.manage;


import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.configuration.IConfigurationManager;
import codeine.jsons.auth.PermissionsConfJson;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.jsons.global.UserPermissionsJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.ConfigureCodeineTemplateData;
import codeine.servlet.FrontEndServletException;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.utils.ExceptionUtils;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ConfigureServlet extends AbstractFrontEndServlet
{
	private static final Logger log = Logger.getLogger(ConfigureServlet.class);
	private static final long serialVersionUID = 1L;
	private @Inject GlobalConfigurationJsonStore globalConfigurationJsonStore;
	private @Inject PermissionsManager permissionsManager;
	private @Inject UserPermissionsJsonStore permissionsJsonStore;
	private @Inject IConfigurationManager configurationManager;

	protected ConfigureServlet() {
		super("configure_codeine", "command_executor", "configure_codeine", "command_executor");
	}

	@Override
	protected String getTitle(HttpServletRequest request) {
		return "Configure Codeine";
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String viewConf = "";
		if (FilesUtils.exists(Constants.getViewConfPath())) {
			viewConf = TextFileUtils.getContents(Constants.getViewConfPath());
		}
		List<ProjectJson> configuredProjects = configurationManager.getConfiguredProjects();
		List<String> projectsNames = Lists.newArrayList();
		for (ProjectJson projectJson : configuredProjects) {
			projectsNames.add(projectJson.name());
		}
		GlobalConfigurationJson globalConfigurationJson = globalConfigurationJsonStore.getNew();
		if (globalConfigurationJson.mysql().isEmpty()) {
			globalConfigurationJson.mysql().add(new MysqlConfigurationJson());
		}
		PermissionsConfJson permissionsConfJson = permissionsJsonStore.get();
		return new ConfigureCodeineTemplateData(gson().toJson(globalConfigurationJson),viewConf,gson().toJson(permissionsConfJson), gson().toJson(projectsNames));
	}
	
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		String section = request.getParameter(Constants.UrlParameters.SECTION);
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		switch (section)
		{
			case "view_configuration": {
				log.info("Will update codeine view configuration. New Config is: " + data);
				TextFileUtils.setContents(Constants.getViewConfPath(), data);
				break;
			}
			case "configuration": {
				log.info("Will update codeine configuration. New Config is: " + data);
				GlobalConfigurationJson json = gson().fromJson(data, GlobalConfigurationJson.class);
				globalConfigurationJsonStore.store(json);
				break;
			}
			case "permissions": {
				log.info("Will update codeine configuration. New Config is: " + data);
				PermissionsConfJson json = gson().fromJson(data, PermissionsConfJson.class);
				permissionsJsonStore.store(json);
				break;
			}
			default:
				log.error("Unknown section parameter: " + section);
				throw ExceptionUtils.asUnchecked(new IllegalArgumentException("Unknown section parameter: " + section));
		}
		writer.write("{}");
		return TemplateData.emptyTemplateData();
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
	
	
	@Override 
	protected List<String> getJsRenderTemplateFiles() {
		return Lists.newArrayList("configure_codeine", "projects_tab", "configure_permissions");
	};
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Management", Constants.CONFIGURE_CONTEXT), new TemplateLink("Configure Codeine", "#")); 
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getManageMenu(request);
	}
}
