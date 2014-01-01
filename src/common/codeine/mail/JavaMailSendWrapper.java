package codeine.mail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import codeine.jsons.global.GlobalConfigurationJsonStore;


public class JavaMailSendWrapper
{
	private static final Logger log = Logger.getLogger(JavaMailSendWrapper.class);
	
	@Inject
	private GlobalConfigurationJsonStore globalConfiguration;
	
	public void sendMail(String subject, String content, List<String> recipients)
	{
		String from = globalConfiguration.get().admin_mail();
		String host = "localhost";
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		if (log.isDebugEnabled())
		{
			props.put("mail.debug", "true");
		}
		
		Session session = Session.getInstance(props);
		try
		{
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] address = buildAdresses(recipients);
			msg.setRecipients(Message.RecipientType.TO, address);
			msg.setSubject(subject);
			msg.setSentDate(new Date());
			msg.setText(content);
			log.info("Mail subject '" + subject + "' is sent to recipients: " + recipients);
			if (System.getProperty("nomail") == null)
			{
				Transport.send(msg);
			}
		}
		catch (MessagingException e)
		{
			log.warn("got excpetion in Send. to: " + recipients, e);
		}
	}
	
	private static InternetAddress[] buildAdresses(List<String> recipients) throws AddressException
	{
		InternetAddress[] address = new InternetAddress[recipients.size()];
		for (int i = 0; i < address.length; i++)
		{
			address[i] = InternetAddress.parse(recipients.get(i))[0];
		}
		return address;
	}

	public void sendMail(Mail mail) {
		sendMail(mail.subject(), mail.content(), mail.recipients());
	}
	
}
