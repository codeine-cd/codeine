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

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class ConfigureServlet extends AbstractFrontEndServlet
{
	private static final Logger log = Logger.getLogger(ConfigureServlet.class);
	private static final long serialVersionUID = 1L;
	private @Inject GlobalConfigurationJson globalConfigurationJson;
	private @Inject PermissionsManager permissionsManager;

	protected ConfigureServlet() {
		super("Configure", "configure_codeine", "command_executor", "configure_codeine", "command_executor");
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		return new ConfigureCodeineTemplateData(gson().toJson(globalConfigurationJson));
	}
	
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
		log.info("Will update codeine configuration. New Config is: " + data);
		// TODO - Save new configuration and update all parts of the system
		writer.write("{}");
		return TemplateData.emptyTemplateData();
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return permissionsManager.isAdministrator(request);
	}
	
	
	@Override 
	protected List<String> getJsRenderTemplateFiles() {
		return Lists.newArrayList("configure_codeine");
	};
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("Manage Codeine", "/manage"), new TemplateLink("Configure", "#")); 
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
	}
}
