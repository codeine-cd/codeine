package codeine;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import codeine.configuration.IConfigurationManager;
import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.AlertsMysqlConnectorDatabaseConnectorListProvider;
import codeine.executer.Task;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.mail.Mail;
import codeine.mail.MailStrategy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class MonitorDBTask implements Task {

	private static final Logger log = Logger.getLogger(MonitorDBTask.class);

	private IConfigurationManager configurationManager;
	private AggregateNotification mailCreator;
	private AggregateMailPrepare mailPrepare;
	private MailStrategy mailsStrategy;
	private CollectionTypeGetter collectionTypeGetter;
	private List<AlertsMysqlConnector> alertsConnectors;

	
	@Inject 
	public MonitorDBTask(IConfigurationManager configurationManager, AggregateNotification mailCreator,
			AggregateMailPrepare mailPrepare, MailStrategy mailsStrategy, CollectionTypeGetter collectionTypeGetter,
			AlertsMysqlConnectorDatabaseConnectorListProvider alertsMysqlConnectorDatabaseConnectorListProvider) {
		super();
		this.configurationManager = configurationManager;
		this.mailCreator = mailCreator;
		this.mailPrepare = mailPrepare;
		this.mailsStrategy = mailsStrategy;
		this.collectionTypeGetter = collectionTypeGetter;
		this.alertsConnectors = alertsMysqlConnectorDatabaseConnectorListProvider.get();
	}

	@Override
	public void run() {
		List<AlertsCollectionType> collType = collectionTypeGetter.getCollectionType(new DateTime());
		log.debug("starting collection " + collType);
		for (AlertsCollectionType alertsCollectionType : collType) {
			workOnCollectionType(alertsCollectionType);
		}
		if (collType.contains(AlertsCollectionType.Daily)){
			for (AlertsMysqlConnector c : alertsConnectors) {
				c.removeOldAlerts();
			}
		}
	}

	private void workOnCollectionType(AlertsCollectionType alertsCollectionType) {
		Multimap<String, CollectorNotificationJson> allItems = HashMultimap.create();
		for (AlertsMysqlConnector c : alertsConnectors) {
			try {
				allItems.putAll(c.getAlertsAndUpdate(alertsCollectionType));
			} catch (Exception e) {
				log.info("error fetching alerts from db " + c);
			}
		}
		List<NotificationContent> notificationContent = mailCreator.prepareMailsToUsers(alertsCollectionType, allItems, configurationManager.getConfiguredProjects());
		List<Mail> mails = mailPrepare.prepare(notificationContent, alertsCollectionType);
		for (Mail mail : mails) {
			log.info("sending mail to " + mail.recipients() + " with subject " + mail.subject());
			mailsStrategy.sendMail(mail);
		}
	}
}
