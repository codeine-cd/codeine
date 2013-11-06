package codeine.mail;

import javax.inject.Inject;

import codeine.jsons.global.GlobalConfigurationJson;

public class MailStrategy {

	@Inject
	private GlobalConfigurationJson globalConfiguration;
	@Inject
	private JavaMailSendWrapper javaMailSendWrapper;

	public void sendMail(Mail mail) {
		if (null == globalConfiguration.email_configuration())
		{
			javaMailSendWrapper.sendMail(mail);
		}
		else
		{
		    SendMailTLS.mail(mail , globalConfiguration.email_configuration());
		}
	}
}
