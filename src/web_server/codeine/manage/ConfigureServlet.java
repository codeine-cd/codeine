package codeine.manage;


import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import codeine.jsons.global.GlobalConfigurationJson;
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
	private @Inject GlobalConfigurationJson globalConfigurationJson;
	private @Inject PermissionsManager permissionsManager;

	protected ConfigureServlet() {
		super("Configure Codiene", "configure_codeine", "command_executor", "configure_codeine", "command_executor");
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String viewConf = "";
		if (FilesUtils.exists(Constants.getViewConfPath())) {
			viewConf = TextFileUtils.getContents(Constants.getViewConfPath());
		}
		return new ConfigureCodeineTemplateData(gson().toJson(globalConfigurationJson),viewConf);
	}
	
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		String section = request.getParameter(Constants.UrlParameters.SECTION);
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		switch (section)
		{
			case "view_configuration":
				log.info("Will update codeine view configuration. New Config is: " + data);
				TextFileUtils.setContents(Constants.getViewConfPath(), data);
				break;
			case "configuration":
				log.info("Will update codeine configuration. New Config is: " + data);
				// TODO - Save new configuration and update all parts of the system
				break;
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
		return Lists.newArrayList("configure_codeine", "projects_tab");
	};
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Management", "/manage"), new TemplateLink("Configure Codeine", "#")); 
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getManageMenu(request);
	}
}
