package codeine.manage;

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import codeine.servlet.AbstractFrontEndServlet;
import codeine.servlet.TemplateData;
import codeine.servlet.TemplateLink;
import codeine.servlet.TemplateLinkWithIcon;

import com.google.common.collect.Lists;

public class ManageServlet extends AbstractFrontEndServlet
{
	private static final long serialVersionUID = 1L;
	private static final String title = "Manage codeine";

	protected ManageServlet() {
		super(title, "config","command_executor", "projects");
	}

	@Override
	protected TemplateData doGet(HttpServletRequest request, PrintWriter writer) {
		return new TemplateData();
	}

	@Override
	protected List<TemplateLink> generateNavigation(HttpServletRequest request) {
		return Lists.<TemplateLink>newArrayList(new TemplateLink(title, "/manage"));
	}

	@Override
	protected List<TemplateLinkWithIcon> generateMenu(HttpServletRequest request) {
		return getMenuProvider().getMainMenu(request);
	}
}
