package codeine.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.model.Constants;
import codeine.utils.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;

public class AggregateMailPrepare {

	public static class NotificationToHeaderFunction implements Function<CollectorNotificationJson, String> {
		@Override
		public String apply(CollectorNotificationJson input) {
			return input.project_name() + "/" + input.collector_name();
		}
	}

	private static final Logger log = Logger.getLogger(AggregateMailPrepare.class);
	private static final int MAX_MAIL_SIZE = 100000;
	@Inject
	private Links links;
	@Inject
	private LabelJsonProvider labelJsonProvider;

	public List<Mail> prepare(List<NotificationContent> notificationContent, AlertsCollectionType alertsCollectionType) {
		List<Mail> $ = Lists.newArrayList();
		for (NotificationContent item : notificationContent) {

			StringBuilder content = new StringBuilder();
			content.append("Hi,\nBelow are alerts from monitors in codeine for policy " + alertsCollectionType + ".\n");
			content.append("For more info: " + links.getWebServerLandingPage() + "\n");
			content.append("Enjoy!\n\n");
			content.append("========================================================================\n");

			ImmutableListMultimap<String, CollectorNotificationJson> byNode = createSummary(item, content);

			for (CollectorNotificationJson notification : item.notifications()) {
				String nodeName = notification.node() == null ? "unknown" : notification.node().alias();
				String version = notification.version() == null ? Constants.NO_VERSION : labelJsonProvider
						.labelForVersion(notification.version(), notification.project_name());
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
			if (content.length() > MAX_MAIL_SIZE) {
				log.warn("mail was too big to user " + item.user());
				stringContent = content.substring(0, MAX_MAIL_SIZE) + "\n...Mail was too long...";
			} else {
				stringContent = content.toString();
			}
			String title = "Aggregated alerts from codeine for policy " + alertsCollectionType;
			if (byNode.values().size() == 1) {
				Entry<String, CollectorNotificationJson> e = byNode.entries().iterator().next();
				title += " on [" + e.getValue().project_name() + "/" + e.getKey() + "/" + e.getValue().collector_name() + "]";
			}
			else {
				title += " on nodes: " + byNode.keySet();
			}
			$.add(new Mail(Lists.newArrayList(item.user()), StringUtils.trimStringToMaxLength(
					title, 150), stringContent));
		}
		return $;
	}

	private ImmutableListMultimap<String, CollectorNotificationJson> createSummary(NotificationContent item,
			StringBuilder content) {
		Function<CollectorNotificationJson, String> f = new Function<CollectorNotificationJson, String>() {

			@Override
			public String apply(CollectorNotificationJson notification) {

				return notification.node() == null ? "unknown" : notification.node().alias();
			}
		};
		ImmutableListMultimap<String, CollectorNotificationJson> byNode = Multimaps.index(item.notifications(), f);
		if (byNode.values().size() < 2) {
			log.debug("will not append summary");
			return byNode;
		}
		content.append("Summary:\n");
		for (String n : byNode.keySet()) {
			content.append(n + " -> "
					+ StringUtils.trimStringToMaxLength(Collections2.transform(byNode.get(n), new NotificationToHeaderFunction()).toString(), 250) + " \n");
		}
		content.append("========================================================================\n");

		return byNode;
	}

	private String formatTime(long time) {
		return new SimpleDateFormat("HH:mm:ss dd/MM/yyyy").format(new Date(time));
	}

	// public static void main(String[] args) {
	// System.out.println(formatTime(System.currentTimeMillis()));
	// }

	// public static void main(String[] args) {
	// AggregateMailPrepare aggregateMailSender = new AggregateMailPrepare();
	// NotificationContent notificationContent2 = new
	// NotificationContent("oshai");
	// NodeJson node = new NodeJson("itstl1043:12345");
	// notificationContent2.add(Lists.newArrayList(new
	// CollectorNotificationJson("collector_name", "project_name",
	// "outputdfgdfg\n\n\nsadkljfhsdfaklh", false, node)));
	// List<NotificationContent> notificationContent =
	// Lists.newArrayList(notificationContent2);
	// List<Mail> mails = aggregateMailSender.prepare(notificationContent );
	// Send.mail(mails.get(0));
	// }
}
