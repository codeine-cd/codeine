package codeine.servlet;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.security.authentication.FormAuthenticator;

import codeine.jsons.global.GlobalConfigurationJson;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
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
	@Inject	private GlobalConfigurationJson globalConfigurationJson;
	
	private static final Logger log = Logger.getLogger(AbstractFrontEndServlet.class);
	private static final long serialVersionUID = 1L;
	private List<String> jsFiles;
	private String contentTemplateFile;
	private String sidebarTemplateFile;
	private PrintWriter writer;
	private String title;
	
	protected AbstractFrontEndServlet(String title, String contentTemplateFile, String sidebarTemplateFile, String... jsFiles) {
		this.title = title;
		this.contentTemplateFile = contentTemplateFile;
		this.sidebarTemplateFile = sidebarTemplateFile;
		this.jsFiles = Lists.newArrayList(jsFiles);
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
	

	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
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
		writer = getWriter(response);
		if (!checkPermissions(request)) {
			response.setStatus(HttpStatus.FORBIDDEN_403);
			return;
		}
		try {
			TemplateData templateData = doGet(request, writer);
			if (templateData == null) {
				super.myGet(request, response);
			} else {
				response.setStatus(HttpStatus.OK_200);
				writeTemplateData(request, templateData);
			}
		} catch (FrontEndServletException e) {
			response.setStatus(e.http_status());
			getWriter(response).write(ExceptionUtils.getRootCause(e.inner_exception()).getMessage());
			// TODO - Generate / redirect to error page / bug reporting
		}
	}

	@Override
	protected final void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("processing post request: " + request.getRequestURL());
		writer = getWriter(response);
		if (!checkPermissions(request)) {
			response.setStatus(HttpStatus.FORBIDDEN_403);
			return;
		}
		TemplateData templateData;
		try {
			templateData = doPost(request, writer);
			if (templateData == null) {
				super.myPost(request, response);
			} else {
				response.setStatus(HttpStatus.OK_200);
				writeTemplateData(request, templateData);
			}
		} catch (FrontEndServletException e) {
			response.setStatus(e.http_status());
			getWriter(response).write(ExceptionUtils.getRootCause(e.inner_exception()).getMessage());
		}
	}
	
	protected MenuProvider getMenuProvider() {
		return menuProvider;
	}
	
	protected void setTitle(String title) {
		this.title = title;
	}
	
	private void prepareTemplateData(HttpServletRequest request, TemplateData templateData) {
		String user = permissionsManager.user(request);
		templateData.setLoggedUser(StringUtils.safeToString(user));
		templateData.authentication_method(globalConfigurationJson.authentication_method());
		templateData.setNavBar(generateNavigation(request));
		templateData.setMenu(generateMenuWithActive(request));
		templateData.setJavascriptFiles(jsFiles);
		templateData.setTitle(title);
		if (!StringUtils.isEmpty(globalConfigurationJson.server_name())) {
				templateData.setServerName(globalConfigurationJson.server_name());
		}
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
	
	private String getMainTemplate(TemplateData data, HttpServletRequest request, final String contentTemplateFile, final String sidebarTemplateFile) {
		String contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/main.html");
		prepareTemplateData(request, data);
		TemplateLoader loader = new TemplateLoader() {
			@Override
			public Reader getTemplate(String name) throws Exception {
				switch (name) {
				case "maincontent":
					return new FileReader(new File(Constants.getResourcesDir() + "/html/" + contentTemplateFile + ".html"));
				case "sidebar":
					return new FileReader(new File(Constants.getResourcesDir() + "/html/" + sidebarTemplateFile + ".html"));
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
	
	private String getSidebarTemplateFile() {
		return sidebarTemplateFile;
	}

	private void writeTemplateData(HttpServletRequest request, TemplateData templateData) {
		if (!templateData.is_empty()) {
			String mainTemplate = getMainTemplate(templateData, request, getContentTemplateFile(), getSidebarTemplateFile());
			writer.write(mainTemplate);
		}
	}
	
	private String getUrl(HttpServletRequest request) {
		String url = ((HttpServletRequest)request).getRequestURL().toString();
		String queryString = ((HttpServletRequest)request).getQueryString();
		return StringUtils.isEmpty(queryString) ? url : url + "?" + queryString;
	}
	
}
