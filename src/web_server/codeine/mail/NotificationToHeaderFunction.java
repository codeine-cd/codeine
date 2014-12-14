package codeine.mail;

import codeine.jsons.mails.CollectorNotificationJson;

import com.google.common.base.Function;

public class NotificationToHeaderFunction implements Function<CollectorNotificationJson, String> {
	@Override
	public String apply(CollectorNotificationJson input) {
		return input.project_name() + "/" + input.collector_name();
	}
}