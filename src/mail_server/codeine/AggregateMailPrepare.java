package codeine;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import codeine.configuration.Links;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.mail.Mail;
import codeine.model.Constants;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class AggregateMailPrepare {

	@Inject private Links links;
	@Inject	private LabelJsonProvider labelJsonProvider;
	
	public List<Mail> prepare(List<NotificationContent> notificationContent, AlertsCollectionType alertsCollectionType) {
		List<Mail> $ = Lists.newArrayList();
		for (NotificationContent item : notificationContent) {
			StringBuilder content = new StringBuilder();
			content.append("Hi,\nBelow are alerts from monitors in codeine for policy " + alertsCollectionType + ".\n");
			content.append("For more info: " + links.getWebServerLandingPage() + "\n");
			content.append("Enjoy!\n\n");
			content.append("========================================================================\n");
			
			for (CollectorNotificationJson notification : item.notifications()) {
				String nodeName = notification.node() == null ? "unknown" : notification.node().alias();
				String version = notification.version() == null ? Constants.NO_VERSION : labelJsonProvider.labelForVersion(notification.version(), notification.project_name());
				content.append("Project       : " + notification.project_name() + "\n");
				content.append("Node          : " + nodeName + "\n");
				content.append("Server        : " + notification.peer() + "\n");
				long time = notification.time();
				content.append("Time on node  : " + formatTime(time) + "\n");
				content.append("Version       : " + version + "\n");
				content.append("Monitor       : " + notification.collector_name() + "\n");
				content.append("Output\n" + notification.output() + "\n");
				content.append("========================================================================\n");
			}
			$.add(new Mail(Lists.newArrayList(item.user()), "Aggregated alerts from codeine for policy " + alertsCollectionType, content.toString()));
		}
		return $;
	}
	private String formatTime(long time) {
		return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date(time));
	}
	
//	public static void main(String[] args) {
//		System.out.println(formatTime(System.currentTimeMillis()));
//	}
	
//	public static void main(String[] args) {
//		AggregateMailPrepare aggregateMailSender = new AggregateMailPrepare();
//		NotificationContent notificationContent2 = new NotificationContent("oshai");
//		NodeJson node = new NodeJson("itstl1043:12345");
//		notificationContent2.add(Lists.newArrayList(new CollectorNotificationJson("collector_name", "project_name", "outputdfgdfg\n\n\nsadkljfhsdfaklh", false, node)));
//		List<NotificationContent> notificationContent = Lists.newArrayList(notificationContent2);
//		List<Mail> mails = aggregateMailSender.prepare(notificationContent );
//		Send.mail(mails.get(0));
//	}
}
