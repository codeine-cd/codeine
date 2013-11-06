package codeine.servlet;


import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

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
	
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		return null;
	}
	
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) {
		return null;
	}
	
	abstract protected List<TemplateLink> generateNavigation(HttpServletRequest request);
	abstract protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request);
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("processing get request: " + request.getRequestURL());
		writer = getWriter(response);
		TemplateData templateData = doGet(request, writer);
		if (templateData == null) {
			super.myGet(request, response);
		} else {
			returnResponse(request, templateData);
		}
	}

	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
		log.debug("processing post request: " + request.getRequestURL());
		writer = getWriter(response);
		TemplateData templateData = doPost(request, writer);
		if (templateData == null) {
			super.myPost(request, response);
		} else {
			returnResponse(request, templateData);
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
		templateData.setNavBar(generateNavigation(request));
		templateData.setMenu(generateMenuWithActive(request));
		templateData.setJavascriptFiles(jsFiles);
		templateData.setTitle(title);
	}

	private List<TemplateLinkWithIcon> generateMenuWithActive(HttpServletRequest request) {
		List<TemplateLinkWithIcon> $ = generateMenu(request);
		for (TemplateLinkWithIcon templateLinkWithIcon : $) {
			if (templateLinkWithIcon.link().contains(request.getRequestURI())) {
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
		return template.execute(data);
	}
	
	private String getContentTemplateFile() {
		return contentTemplateFile;
	}
	
	private String getSidebarTemplateFile() {
		return sidebarTemplateFile;
	}

	private void returnResponse(HttpServletRequest request, TemplateData templateData) {
		String mainTemplate = getMainTemplate(templateData, request, getContentTemplateFile(), getSidebarTemplateFile());
		writer.write(mainTemplate);
	}
	
}
