package codeine.servlet;

import java.util.List;

import codeine.CodeineVersion;
import codeine.jsons.auth.AuthenticationMethod;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class TemplateData {
	private List<TemplateLinkWithIcon> menu;
	private List<TemplateLink> navigationbar;
	private TemplateLink navigationbarlast;
	private String loggeduser;
	private AuthenticationMethod authentication_method;
	private String title;
	private String server_name = "Codeine";
	private String codeineversion;
	private String codeineversionfull;
	private String javascriptfiles;
	
	public TemplateData() {
		super();
		this.codeineversion = CodeineVersion.getNoDate();
		this.codeineversionfull = CodeineVersion.get();
	}

	private String addJsTags(List<String> jsFiles) {
		String $ = "";
		for (String file : jsFiles) {
			$ += "<script src='resources/js/"+file+".js'></script>";
		}
		return $;
	}
	
	public void setJavascriptFiles(List<String> jsFiles) {
		this.javascriptfiles = addJsTags(jsFiles);
	}
	
	public void setLoggedUser(String user) {
		loggeduser = user;
	}
	public void authentication_method(AuthenticationMethod authentication_method) {
		this.authentication_method = authentication_method;
	}
	
	public void setNavBar(List<TemplateLink> navigation) {
		this.navigationbar =  navigation;
		if (!navigation.isEmpty()){
			this.navigationbar = Lists.newArrayList(navigation.subList(0, navigation.size()-1));
			this.navigationbarlast = navigation.get(navigation.size()-1);
		}
	}
	
	public void setMenu(List<TemplateLinkWithIcon> menu) {
		this.menu = menu;
	}

	public void setServerName(String serverName) {
		server_name = server_name + " - " + serverName;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
}
