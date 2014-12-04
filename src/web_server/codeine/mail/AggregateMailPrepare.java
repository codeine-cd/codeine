package codeine.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.model.Constants;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;

public class AggregateMailPrepare {

	private static final Logger log = Logger
			.getLogger(AggregateMailPrepare.class);
	private static final int MAX_MAIL_SIZE = 100000;
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
			content.append("Summary:\n");
			ImmutableListMultimap<String, CollectorNotificationJson> byNode = createSummary(item, content);
			
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
			String stringContent = "";
			if (content.length() > MAX_MAIL_SIZE){
				log.warn("mail was too big to user " + item.user());
				stringContent = content.substring(0, MAX_MAIL_SIZE) + "\n...Mail was too long...";
			}
			else {
				stringContent = content.toString();
			}
			$.add(new Mail(Lists.newArrayList(item.user()), trimStringToMaxLength("Aggregated alerts from codeine for policy " + alertsCollectionType + " on nodes: " + byNode.keySet(),100), stringContent));
		}
		return $;
	}
	private String trimStringToMaxLength(String s, int size)
	{
		if(s.length() > size)
		{
			String substring = s.substring(0,size);
			int lastIndexOf = substring.lastIndexOf(" ");
			return lastIndexOf == -1 ? substring + "... and some more." : substring.substring(0,lastIndexOf) + "... and some more.";
		}
		return s;
		
	}
	private ImmutableListMultimap<String, CollectorNotificationJson> createSummary(NotificationContent item,
			StringBuilder content) {
		Function<CollectorNotificationJson, String> f = new Function<CollectorNotificationJson, String>() {
			
			@Override
			public String apply(CollectorNotificationJson notification) {
				
				return notification.node() == null ? "unknown" : notification.node().alias();
			}
		};
		ImmutableListMultimap<String, CollectorNotificationJson> byNode = Multimaps.index(item.notifications(), f );
		Function<CollectorNotificationJson, String> function = new Function<CollectorNotificationJson, String>() {
			
			@Override
			public String apply(CollectorNotificationJson input) {
				return input.project_name() + "=>"+input.collector_name();
			}
			
		};
		for (String n : byNode.keySet()) {
				content.append("Node          : " + n + "failing monitors:" + trimStringToMaxLength(Collections2.transform(byNode.get(n), function ).toString(),250) + " \n" );
			}
			
		return byNode;
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
