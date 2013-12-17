package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;

import com.google.common.collect.Lists;

public class UserInfoServlet extends AbstractFrontEndServlet {

	private static final long serialVersionUID = 1L;
	private @Inject PermissionsManager permissionsManager;
	
	protected UserInfoServlet() {
		super("", "", "", "");
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String user = permissionsManager.user(request);
		
		
		return null;
	}
	
	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		String user = permissionsManager.user(request);
		return Lists.<TemplateLink>newArrayList(new TemplateLink(user, "#"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
	}

}
