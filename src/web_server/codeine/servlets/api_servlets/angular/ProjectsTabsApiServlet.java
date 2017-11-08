package codeine.servlets.api_servlets.angular;

import codeine.model.Constants;
import codeine.plugins.CodeineConfModifyPlugin;
import codeine.plugins.CodeineConfModifyPlugin.Step;
import codeine.servlet.AbstractApiServlet;
import codeine.servlets.api_servlets.ProjectsTab;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class ProjectsTabsApiServlet extends AbstractApiServlet {

	private static final Logger log = Logger.getLogger(ProjectsTabsApiServlet.class);
	private static final long serialVersionUID = 1L;
	@Inject	private CodeineConfModifyPlugin codeineConfModifyPlugin;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		if (request.getMethod().equals("POST")) {
			if (!isAdministrator(request)) {
				log.info("User can not define new project");
				return false;
			}
			return true;
		}
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String file = Constants.getViewConfPath();
		List<ProjectsTab> projects_tabs = Lists.newArrayList();
		if (FilesUtils.exists(file)) {
			Type listType = new TypeToken<ArrayList<ProjectsTab>>() { }.getType();
			projects_tabs.addAll((Collection<? extends ProjectsTab>) gson().fromJson(TextFileUtils.getContents(file), listType));
		}
		writeResponseJson(response, projects_tabs);
	}
	
	@Override
	protected void myPut(HttpServletRequest request, HttpServletResponse response) {
		Type listType = new TypeToken<ArrayList<ProjectsTab>>() { }.getType();
		ArrayList<ProjectsTab> data = readBodyJson(request, listType);
		
		log.info("Will update codeine view configuration. New Config is: " + data);
		codeineConfModifyPlugin.call(Step.pre, getUser(request).user().username());
		TextFileUtils.setContents(Constants.getViewConfPath(), gson().toJson(data));
		codeineConfModifyPlugin.call(Step.post, getUser(request).user().username());
		writeResponseJson(response, data);
	}


}
