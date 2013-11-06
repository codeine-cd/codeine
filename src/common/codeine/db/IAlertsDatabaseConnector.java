package codeine.db;

import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;

import com.google.common.collect.Multimap;

public interface IAlertsDatabaseConnector {

	Multimap<String, CollectorNotificationJson> getAlertsAndUpdate(AlertsCollectionType collType);
	void removeOldAlerts();
	void put(CollectorNotificationJson collectorNotificationJson);

}