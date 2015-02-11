package codeine.mail;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.jsons.project.MailPolicyJson;
import codeine.jsons.project.ProjectJson;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class AggregateNotification {


	public List<NotificationContent> prepareMailsToUsers(AlertsCollectionType collType, Multimap<String, CollectorNotificationJson> projectNameToItems, List<ProjectJson> projects) {
		Map<String,NotificationContent> notifications = Maps.newHashMap();
		for (ProjectJson p : projects) {
			for (MailPolicyJson policy : p.mail()) {
				if(policy.intensity() == collType) {
					updateNotification(collType, projectNameToItems.get(p.name()), policy.user(),notifications);
				}
			}
		}
		return Lists.newArrayList(notifications.values());

	}

	private void updateNotification(AlertsCollectionType collType, Collection<CollectorNotificationJson> collection, String user, Map<String, NotificationContent> notifications) {
		
		NotificationContent n = notifications.get(user) == null ? new NotificationContent(user) : notifications.get(user);
		List<CollectorNotificationJson> list = Lists.newArrayList();
		for (CollectorNotificationJson collectorNotificationJson : collection) {
			list.add(collectorNotificationJson);
		}
		if (!list.isEmpty()){
			n.addAll(list);
			notifications.put(user,n);
		}
	}

}
