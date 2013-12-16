package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;

import codeine.ConfigurationManagerServer;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlets.template.NewProjetTemplateData;
import codeine.utils.ExceptionUtils;
import codeine.utils.JsonUtils;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class NewProjectServlet extends AbstractFrontEndServlet {

	private static final Logger log = Logger.getLogger(NewProjectServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject private ConfigurationManagerServer configurationManager;
	
	protected NewProjectServlet() {
		super("New Project", "new_project", "command_executor", "new_project", "command_executor");
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
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink("New Project", "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
	}

	@Override
	protected void myPost(HttpServletRequest request, HttpServletResponse response) {
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
			response.setStatus(HttpStatus.OK_200);
			getWriter(response).write("{}");
		} catch (Exception e) {
			getWriter(response).write(ExceptionUtils.getRootCause(e).getMessage());
			response.setStatus(HttpStatus.BAD_REQUEST_400);
		}
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
