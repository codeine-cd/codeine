package codeine.servlet;

@SuppressWarnings("unused")
public class UserInfoTemplateData extends TemplateData {
	
	private String username;
	private String api_token;
	
	
	public UserInfoTemplateData(String username, String api_token) {
		super();
		this.username = username;
		this.api_token = api_token;
	}
	
	

}
