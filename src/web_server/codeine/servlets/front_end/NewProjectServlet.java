package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.FrontEndServletException;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.NewProjetTemplateData;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class NewProjectServlet extends AbstractFrontEndServlet {

	private static final Logger log = Logger.getLogger(NewProjectServlet.class);
	private static final long serialVersionUID = 1L;
	private @Inject ConfigurationManagerServer configurationManager;
	private @Inject PermissionsManager permissionsManager;
	
	protected NewProjectServlet() {
		super("new_project");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList( "new_project", "command_executor");
	}
	
	@Override
	protected List<String> getSidebarTemplateFiles() {
		return Lists.newArrayList( "command_executor");
	}
	
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		return "New Project";
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		List<ProjectJson> projects = configurationManager.getConfiguredProjects();
		List<String> names = Lists.newArrayList();
		for (ProjectJson p : projects) {
			names.add(p.name());
		}
		Collections.sort(names);
		return new NewProjetTemplateData(names);
	}
	
	@Override
	protected TemplateData doPost(HttpServletRequest request, PrintWriter writer) throws  FrontEndServletException{
		try {
			String data = request.getParameter(Constants.UrlParameters.DATA_NAME);
			log.info("creating project " + data);
			CreateNewProjectJson newProjectParamsJson = gson().fromJson(data, CreateNewProjectJson.class);
			ProjectJson newProject = new ProjectJson();
			if (newProjectParamsJson.type == NewProjectType.Copy) {
				ProjectJson projectForCopy = configurationManager.getProjectForName(newProjectParamsJson.selected_project);
				newProject = JsonUtils.cloneJson(projectForCopy, ProjectJson.class);
			}
			newProject.name(newProjectParamsJson.project_name);
			configurationManager.createNewProject(newProject);
			writer.write("{}");
			return TemplateData.emptyTemplateData();
		} catch (Exception e) {
			throw new FrontEndServletException(e,HttpStatus.BAD_REQUEST_400);
		}	
	}
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (!permissionsManager.isAdministrator(request)) {
			log.info("User can not define new project");
			return false;
		}
		return true;
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("New Project", "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
	}
	
	public static class CreateNewProjectJson {
		private String project_name;
		private String selected_project;
		private NewProjectType type;
	}
	
	public enum NewProjectType {
		Copy,New;
	}
}
