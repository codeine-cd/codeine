package yami;

import java.util.List;

import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.model.Constants;
import yami.model.Result;

public class SendMailStrategy
{
	
	public void sendMail(List<String> mailingList, HttpCollector c, Node n, Result results)
	{
		String successString = results.success() ? "OK" : "FAIL";
		String subject = "yami monitor '" + c.name + "' on " + n.nick() + " is now " + successString;
		String content = "Collector current status: "
				+ Constants.CLIENT_LINK.replace(Constants.APP_NAME, n.name).replace(Constants.NODE_NAME, n.node.name).replace(Constants.COLLECTOR_NAME, c.name)
				+ "\n\n";
		content += "Dashboard: " + Constants.getServerDashboard() + "\n\n";
		content += "Collector Output:\n";
		content += results.output + "\n";
		Send.mail(subject, content, mailingList);
	}
}
