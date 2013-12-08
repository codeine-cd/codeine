package codeine.jsons.project;

import codeine.jsons.mails.AlertsCollectionType;

public class MailPolicyJson {
	
	private String user;
	private AlertsCollectionType intensity;
	
	
	public MailPolicyJson(String user, AlertsCollectionType intensity) {
		super();
		this.user = user;
		this.intensity = intensity;
	}
	
	public AlertsCollectionType intensity() {
		if (null == intensity){
			return AlertsCollectionType.Immediately;
		}
		return intensity;
	}
	public String user() {
		return user;
	}
}
