package codeine.mail;

import java.util.List;

import codeine.jsons.mails.CollectorNotificationJson;

import com.google.common.collect.Lists;

public class NotificationContent {

	private String user;
	private List<CollectorNotificationJson> notifications = Lists.newArrayList();

	public NotificationContent(String user) {
		this.user = user;
	}

	public String user() {
		return user;
	}

	public List<CollectorNotificationJson> notifications() {
		return notifications;
	}

	public void addAll(List<CollectorNotificationJson> collection) {
		notifications.addAll(0, collection); //location 0 is for test AggregateNotificationTest.testTwoItemsForTwoProjects
	}

}
