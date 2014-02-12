package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.RawOutputTemplateData;
import codeine.utils.UrlUtils;
import codeine.utils.network.HttpUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class RawOutputServlet extends AbstractFrontEndServlet {

	private static final long serialVersionUID = 1L;
	
	protected RawOutputServlet() {
		super("raw_output");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("command_history");
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		return projectName + " Monitor Output";
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String link = request.getParameter(Constants.UrlParameters.LINK_NAME);
		boolean isWebServerLink = link.startsWith("/");
		if (isWebServerLink) {
			String address = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf(request.getRequestURI()));
			link = address + link;
		}
		return new RawOutputTemplateData(HttpUtils.encode(HttpUtils.doGET(link,null)));
	}

	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String projectName = request.getParameter(Constants.UrlParameters.PROJECT_NAME);
		String resourece = request.getParameter(Constants.UrlParameters.RESOURCE_NAME);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(projectName, UrlUtils.buildUrl(Constants.PROJECT_STATUS_CONTEXT, ImmutableMap.of(Constants.UrlParameters.PROJECT_NAME, projectName))),new TemplateLink(resourece, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getProjectMenu(request);
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return canReadProject(request);
	}

}
