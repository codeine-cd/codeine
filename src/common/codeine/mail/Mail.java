package codeine.mail;

import java.util.List;

public class Mail {
	
	private List<String> recepients;
	private String subject;
	private String content;
	private String sender;
	
	public Mail(List<String> recepients, String subject, String content, String sender) {
		super();
		this.recepients = recepients;
		this.subject = subject;
		this.content = content;
		this.sender = sender;
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

	public String sender() {
		return sender;
	}

	@Override
	public String toString() {
		return "Mail [recepients=" + recepients + ", subject=" + subject + ", sender=" + sender + "]";
	}
	
	
}
