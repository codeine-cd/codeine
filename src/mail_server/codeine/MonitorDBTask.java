package codeine;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import codeine.configuration.ConfigurationManager;
import codeine.db.IAlertsDatabaseConnector;
import codeine.executer.Task;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.mail.Mail;
import codeine.mail.MailStrategy;

import com.google.common.collect.Multimap;

public class MonitorDBTask implements Task {

	private static final Logger log = Logger.getLogger(MonitorDBTask.class);
	@Inject
	private ConfigurationManager configurationManager;
	@Inject 
	private AggregateNotification mailCreator;
	@Inject 
	private AggregateMailPrepare mailPrepare;
	@Inject 
	private MailStrategy mailsStrategy;
	@Inject 
	private CollectionTypeGetter collectionTypeGetter;
	@Inject 
	private IAlertsDatabaseConnector mongoConnector;

	@Override
	public void run() {
		List<AlertsCollectionType> collType = collectionTypeGetter.getCollectionType(new DateTime());
		log.debug("starting collection " + collType);
		for (AlertsCollectionType alertsCollectionType : collType) {
			workOnCollectionType(alertsCollectionType);
		}
		if (collType.contains(AlertsCollectionType.Daily)){
			mongoConnector.removeOldAlerts();
		}
	}

	private void workOnCollectionType(AlertsCollectionType alertsCollectionType) {
		Multimap<String, CollectorNotificationJson> allItems = mongoConnector.getAlertsAndUpdate(alertsCollectionType);
		List<NotificationContent> notificationContent = mailCreator.prepareMailsToUsers(alertsCollectionType, allItems, configurationManager.getConfiguredProjects());
		List<Mail> mails = mailPrepare.prepare(notificationContent, alertsCollectionType);
		for (Mail mail : mails) {
			mailsStrategy.sendMail(mail);
		}
	}
}
