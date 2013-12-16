package codeine.mail;

import java.util.List;

public class Mail {
	
	private List<String> recepients;
	private String subject;
	private String content;
	
	public Mail(List<String> recepients, String subject, String content) {
		super();
		this.recepients = recepients;
		this.subject = subject;
		this.content = content;
	}

	public String subject() {
		return subject;
	}

	public String content() {
		return content;
	}

	public List<String> recipients() {
		return recepients;
	}
	
	
}
