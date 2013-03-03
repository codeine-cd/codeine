package yami.mail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import yami.configuration.EmailConfiguration;

public class SendMailTLS {

    private static final Logger log = Logger.getLogger(SendMailTLS.class);

    public static void mail(String subject, String content, List<String> recipients, final EmailConfiguration conf)
    {
	Properties props = new Properties();
	props.put("mail.smtp.auth", conf.mail_smtp_auth);
	props.put("mail.smtp.starttls.enable", conf.mail_smtp_starttls_enable);
	props.put("mail.smtp.host", conf.mail_smtp_host);
	props.put("mail.smtp.port", conf.mail_smtp_port);

	Session session = Session.getInstance(props,
		new javax.mail.Authenticator() {
	    @Override
		protected PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(conf.username, conf.password);
	    }
	});

	try {

	    Message message = new MimeMessage(session);
	    message.setFrom(new InternetAddress(conf.username));
	    InternetAddress[] address = buildAdresses(recipients);
	    message.setRecipients(Message.RecipientType.TO, address);
	    message.setSubject(subject);
	    message.setSentDate(new Date());
	    message.setText(content);
	    log.info("Mail subject '" + subject + "' is sent to recipients: " + recipients);
	    if (System.getProperty("nomail") == null)
	    {
		Transport.send(message);
	    }
	} catch (MessagingException e) {
	    throw new RuntimeException(e);
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
}