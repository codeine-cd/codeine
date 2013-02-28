package yami;

import java.util.List;

import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.mail.SendMailTLS;
import yami.model.Constants;
import yami.model.Result;

public class SendMailStrategy
{
	
	public void mailCollectorResult(List<String> mailingList, HttpCollector c, Node n, Result results)
	{
		String successString = results.success() ? "OK" : "FAIL";
		String subject = "yami monitor '" + c.name + "' on " + n.nick() + " is now " + successString;
		String content = "Collector current status: " + Constants.CLIENT_LINK.replace(Constants.CLIENT_PORT, ConfigurationManager.getInstance().getCurrentGlobalConfiguration().getClientPort() + "").replace(Constants.APP_NAME, n.name).replace(Constants.NODE_NAME, n.peer.name).replace(Constants.COLLECTOR_NAME, c.name) + "\n\n";
		content += "Dashboard: " + Constants.getServerDashboard() + "\n\n";
		content += "Collector Output:\n";
		content += results.output + "\n";
		ConfigurationManager cm = ConfigurationManager.getInstance();
		if (null == cm.getCurrentGlobalConfiguration() || null == cm.getCurrentGlobalConfiguration().email_configuration)
		{
		    Send.mail(subject, content, mailingList);
		}
		else
		{
		    SendMailTLS.mail(subject, content, mailingList, cm.getCurrentGlobalConfiguration().email_configuration);
		}
	}
}
