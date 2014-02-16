package codeine.servlets.api_servlets.angular;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlets.front_end.ProjectsTab;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

@SuppressWarnings("serial")
public class ProjectsTabsApiServlet extends AbstractServlet {

	private static final Logger log = Logger.getLogger(ProjectsTabsApiServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String file = Constants.getViewConfPath();
		List<ProjectsTab> projects_tabs = Lists.newArrayList(new ProjectsTab("main", Lists.newArrayList(".*")));
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
		TextFileUtils.setContents(Constants.getViewConfPath(), gson().toJson(data));
		writeResponseJson(response, data);
	}


}
