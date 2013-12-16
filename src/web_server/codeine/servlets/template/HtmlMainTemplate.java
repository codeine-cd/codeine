package codeine.servlets.template;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import codeine.model.Constants;
import codeine.servlet.TemplateData;
import codeine.utils.TextFileUtils;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

public class HtmlMainTemplate {
	
	public String getMainTemplate(TemplateData data, HttpServletRequest request, final String contentTemplateFile, final String sidebarTemplateFile) {
		String contents = TextFileUtils.getContents(Constants.getResourcesDir() + "/html/main.html");
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
	
}
