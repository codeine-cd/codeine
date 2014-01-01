package codeine.mail;

import javax.inject.Inject;

import codeine.jsons.global.GlobalConfigurationJsonStore;

public class MailStrategy {

	@Inject
	private GlobalConfigurationJsonStore globalConfiguration;
	@Inject
	private JavaMailSendWrapper javaMailSendWrapper;

	public void sendMail(Mail mail) {
		if (null == globalConfiguration.get().email_configuration())
		{
			javaMailSendWrapper.sendMail(mail);
		}
		else
		{
		    SendMailTLS.mail(mail , globalConfiguration.get().email_configuration());
		}
	}
}
