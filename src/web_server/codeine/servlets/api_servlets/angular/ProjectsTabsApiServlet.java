package codeine.servlets.api_servlets.angular;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.servlets.front_end.ProjectsTab;
import codeine.utils.TextFileUtils;

import com.google.common.reflect.TypeToken;

public class ProjectsTabsApiServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected boolean checkPermissions(HttpServletRequest request) {
		return true;
	}
	
	
	@Override
	@SuppressWarnings("serial")
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		String file = Constants.getViewConfPath();
		Type listType = new TypeToken<ArrayList<ProjectsTab>>() { }.getType();
		List<ProjectsTab> projects_tabs = gson().fromJson(TextFileUtils.getContents(file), listType);
		writeResponseJson(response, projects_tabs);
	}

}
