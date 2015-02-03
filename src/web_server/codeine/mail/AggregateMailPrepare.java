package codeine.mail;

import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import codeine.configuration.Links;
import codeine.jsons.global.GlobalConfigurationJsonStore;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.model.Constants;
import codeine.model.ExitStatus;
import codeine.utils.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimaps;
import com.google.inject.Inject;

public class AggregateMailPrepare {

	private static final Logger log = Logger.getLogger(AggregateMailPrepare.class);
	private static final int MAX_MAIL_SIZE = 100000;
	@Inject
	private Links links;
	@Inject
	private LabelJsonProvider labelJsonProvider;
	@Inject
	private GlobalConfigurationJsonStore globalConfigurationJsonStore;

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
				content.append("Monitor       : " + notification.collector_name() + "\n");
				if (null != notification.exit_status()) {
				String exitString = "" + notification.exit_status();
				if (notification.exit_status() <= 0) {
					exitString += " (" + ExitStatus.fromInt(notification.exit_status()) + ")";
				}
				content.append("Exit Status   : " + exitString + "\n");
				}
				if (!StringUtils.isEmpty(notification.duration())) {
				content.append("Duration      : " + notification.duration() + "\n");
				}
				content.append("Time on node  : " + getTimeOnNode(notification) + "\n");
				content.append("Server        : " + notification.peer() + "\n");
				content.append("Version       : " + version + "\n");
				content.append("Link to monitor page : " + getLink(notification) + "\n");
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
				title = StringUtils.trimStringToMaxLength(title, 150);
			}
			$.add(new Mail(Lists.newArrayList(item.user()), title, stringContent, globalConfigurationJsonStore.get().admin_mail()));
		}
		return $;
	}

	private String getLink(CollectorNotificationJson notification) {
		if (notification.is_for_collector()) {
			return links.getWebServerCollectorStatus(notification.project_name(), notification.node().name(), notification.collector_name());
		} else {
			return links.getWebServerMonitorStatus(notification.project_name(), notification.node().name(), notification.collector_name());
		}
	}

	private String getTimeOnNode(CollectorNotificationJson notification) {
		if (StringUtils.isEmpty(notification.time_formatted())) {
			return StringUtils.formatDate(notification.time());
		}
		else {
			return notification.time_formatted();
		}
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
