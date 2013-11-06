package codeine.servlets;



import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.ProgressiveOutputTemplateData;
import codeine.utils.UrlUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ProgressiveRawOutputServlet extends AbstractFrontEndServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected ProgressiveRawOutputServlet() {
		super("", "progress_output", "command_history", "progress_output", "command_history");
	
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String command = request.getParameter(Constants.UrlParameters.COMMAND_NAME);
		String path = request.getParameter(Constants.UrlParameters.PATH_NAME);
		setTitle(projectName + "-" + command);
		return new ProgressiveOutputTemplateData(projectName, command, path);
	}


	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String command = request.getParameter(Constants.UrlParameters.COMMAND_NAME);
		return Lists.newArrayList(new TemplateLink(projectName, UrlUtils.buildUrl(Constants.AGGREGATE_NODE_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName))), new TemplateLink(command, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request.getParameter(Constants.UrlParameters.PROJECT_NAME));
	}

}
