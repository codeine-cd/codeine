package codeine.servlets.front_end;

import java.io.PrintWriter;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import codeine.jsons.auth.CodeineUser;
import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.PermissionsManager;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;
import codeine.servlet.UserInfoTemplateData;
import codeine.servlet.UsersManager;

import com.google.common.collect.Lists;

public class UserInfoServlet extends AbstractFrontEndServlet {

	private static final long serialVersionUID = 1L;
	private @Inject PermissionsManager permissionsManager;
	private @Inject UsersManager usersManager;
	
	protected UserInfoServlet() {
		super("user_info");
	}

	@Override
	protected List<String> getJSFiles() {
		return Lists.newArrayList("user_info","command_executor");
	}
	
	@Override
	protected List<String> getSidebarTemplateFiles() {
		return Lists.newArrayList("command_executor");
	}
	
	@Override
	protected String getTitle(HttpServletRequest request) {
		return "User Info";
	}
	
	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		String user = permissionsManager.user(request);
		CodeineUser codeineUser = usersManager.user(user);
		return new UserInfoTemplateData(codeineUser.username(), codeineUser.api_token());
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
