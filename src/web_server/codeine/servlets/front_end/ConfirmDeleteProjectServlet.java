package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ConfirmDeleteProjectTemplateData;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.Lists;

public class ConfirmDeleteProjectServlet extends AbstractFrontEndServlet  {

	public ConfirmDeleteProjectServlet() {
		super("confirm_delete", "project_confirm_delete", "command_history", "project_confirm_delete", "command_history");
	}

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(ConfirmDeleteProjectServlet.class);
	private @Inject PermissionsManager permissionsManager;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String user = permissionsManager.user(request);
		if (!permissionsManager.isAdministrator(request)) {
			log.info("Non admin user (" + user + ") cannot delete project");
			response.setStatus(HttpStatus.FORBIDDEN_403);
			return;
		}
		super.myGet(request, response);
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		return new ConfirmDeleteProjectTemplateData(request.getParameter(Constants.UrlParameters.PROJECT_NAME));
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, Constants.PROJECT_STATUS_CONTEXT + "?"+Constants.UrlParameters.PROJECT_NAME+"=" + HttpUtils.encode(projectName)),new TemplateLink("Delete", "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
}
