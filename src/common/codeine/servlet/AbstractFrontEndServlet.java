package codeine.servlet;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.security.authentication.FormAuthenticator;

import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.model.Constants;
import codeine.utils.StringUtils;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

public abstract class AbstractFrontEndServlet extends AbstractServlet {
	
	@Inject	private PermissionsManager permissionsManager;
	@Inject	private MenuProvider menuProvider;
	@Inject	private GlobalConfigurationJsonStore globalConfigurationJson;
	@Inject	private ExperimentalConfJsonStore webConfJsonStore;
	@Inject	private PrepareForShutdown prepareForShutdown;
	
	private static final Logger log = Logger.getLogger(AbstractFrontEndServlet.class);
	private static final long serialVersionUID = 1L;
	private String contentTemplateFile;
	
	protected AbstractFrontEndServlet(String contentTemplateFile) {
		this.contentTemplateFile = contentTemplateFile;
	}
	
	
	abstract protected List<String> getJSFiles();
	
	protected List<String> getSidebarTemplateFiles() {
		return Lists.newArrayList( "command_history");
	}
	
	@Override
	protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			if (request.getSession() != null) {
				request.getSession().setAttribute(FormAuthenticator.__J_URI, getUrl(request));
			}
		} catch(Exception e) {
			
		}
		super.doGet(request, response);
	}
	

	
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException{
		return null;
	}
	
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) throws FrontEndServletException {
		return null;
	}
	
	abstract protected List<TemplateLink> generateNavigation(HttpServletRequest request);
	abstract protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request);
	
	protected List<String> getJsRenderTemplateFiles() {
		return Lists.newArrayList();
	}
	
	@Override
	protected final void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("processing get request: " + request.getRequestURL());
		try {
			myGetFrontEnd(request, response);
			
		} catch (FrontEndServletException e) {
			response.setStatus(e.http_status());
			handleError(e.inner_exception(), response);
		}
	}


	protected void myGetFrontEnd(HttpServletRequest request, HttpServletResponse response)
			throws FrontEndServletException {
		PrintWriter writer = getWriter(response);
		TemplateData templateData = doGet(request, writer);
		if (templateData == null) {
			super.myGet(request, response);
		} else {
			response.setStatus(HttpStatus.OK_200);
			writeTemplateData(request, templateData, response);
		}
	}

	@Override
	protected final void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("processing post request: " + request.getRequestURL());
		PrintWriter writer = getWriter(response);
		TemplateData templateData;
		try {
			templateData = doPost(request, writer);
			if (templateData == null) {
				super.myPost(request, response);
			} else {
				response.setStatus(HttpStatus.OK_200);
				writeTemplateData(request, templateData, response);
			}
		} catch (FrontEndServletException e) {
			response.setStatus(e.http_status());
			handleError(e.inner_exception(), response);
		}
	}
	
	@Override
	protected void handleError(Exception e, HttpServletResponse response) {
		handleErrorRequestFromBrowser(e, response);
	}
	
	@Override
	protected void writeNotFound(HttpServletRequest request, HttpServletResponse response) {
		response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
		String contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/405.html");
		getWriter(response).write(contents);
	}
	
	protected MenuProvider getMenuProvider() {
		return menuProvider;
	}
	
	protected abstract String getTitle(HttpServletRequest request);
	
	private void prepareTemplateData(HttpServletRequest request, TemplateData templateData) {
		String user = permissionsManager.user(request);
		templateData.setLoggedUser(StringUtils.safeToString(user));
		templateData.authentication_method(globalConfigurationJson.get().authentication_method());
		if (!StringUtils.isEmpty(webConfJsonStore.get().new_issue_link())) {
			templateData.new_issue_link(webConfJsonStore.get().new_issue_link());
		}
		templateData.setNavBar(generateNavigation(request));
		templateData.setMenu(generateMenuWithActive(request));
		templateData.setJavascriptFiles(getJSFiles());
		templateData.setTitle(getFullTitle(request));
		templateData.prepare_for_shutdown(prepareForShutdown.isSequnceActivated());
		if (!StringUtils.isEmpty(globalConfigurationJson.get().server_name())) {
				templateData.setServerName(globalConfigurationJson.get().server_name());
		}
	}

	private String getFullTitle(HttpServletRequest request) {
		String server_name = globalConfigurationJson.get().server_name();
		return (StringUtils.isEmpty(server_name)) ? getTitle(request) : server_name + " [" + getTitle(request) + "]";
	}

	private List<TemplateLinkWithIcon> generateMenuWithActive(HttpServletRequest request) {
		List<TemplateLinkWithIcon> $ = generateMenu(request);
		for (TemplateLinkWithIcon templateLinkWithIcon : $) {
			if ((templateLinkWithIcon.link().contains(request.getRequestURI())) || ((templateLinkWithIcon.link().equals("/")) && (request.getRequestURI().equals(Constants.PROJECTS_LIST_CONTEXT)))) {
				templateLinkWithIcon.setActive();
				break;
			}
		}
		return $;
	}
	
	private String getMainTemplate(TemplateData data, HttpServletRequest request, final String contentTemplateFile, final List<String> sidebarTemplateFiles) {
		String contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/main.html");
		prepareTemplateData(request, data);
		TemplateLoader loader = new TemplateLoader() {
			@Override
			public Reader getTemplate(String name) throws Exception {
				switch (name) {
				case "maincontent":
					return new FileReader(new File(Constants.getResourcesDir() + "/html/" + contentTemplateFile + ".html"));
				case "sidebar":
					StringBuilder content = new StringBuilder();
					for (String sidebarFile : sidebarTemplateFiles) {
						content.append(TextFileUtils.getContents(Constants.getResourcesDir() + "/html/" + sidebarFile + ".html"));
					}
					return new StringReader(content.toString());
				default: 
					return new FileReader(new File(Constants.getResourcesDir() + "/html/" + name + ".html")); 
				}
			}
		};
		Template template = Mustache.compiler().escapeHTML(false).withLoader(loader).compile(contents);
		StringBuilder $ = new StringBuilder(template.execute(data));
		
		for (String tmpl : getJsRenderTemplateFiles()) {
			$.append("<script id='" + tmpl + "' type='text/x-jsrender'>");
			$.append(TextFileUtils.getContents(Constants.getResourcesDir() + "/html/jsrendertemplates/" +  tmpl + ".tmpl.html"));
			$.append("</script>");
		}
		
		 $.append("</body></html>");
		return $.toString();
	}
	
	private String getContentTemplateFile() {
		return contentTemplateFile;
	}

	private void writeTemplateData(HttpServletRequest request, TemplateData templateData, HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		if (!templateData.is_empty()) {
			String mainTemplate = getMainTemplate(templateData, request, getContentTemplateFile(), getSidebarTemplateFiles());
			writer.write(mainTemplate);
		}
	}
	
	private String getUrl(HttpServletRequest request) {
		String url = ((HttpServletRequest)request).getRequestURL().toString();
		String queryString = ((HttpServletRequest)request).getQueryString();
		return StringUtils.isEmpty(queryString) ? url : url + "?" + queryString;
	}
	
}
