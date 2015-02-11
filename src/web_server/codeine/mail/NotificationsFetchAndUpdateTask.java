package codeine.mail;

import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import codeine.configuration.IConfigurationManager;
import codeine.db.mysql.connectors.AlertsMysqlConnector;
import codeine.db.mysql.connectors.AlertsMysqlConnectorDatabaseConnectorListProvider;
import codeine.db.mysql.connectors.NotificationsMysqlConnector;
import codeine.db.mysql.connectors.NotificationsMysqlConnectorDatabaseConnectorListProvider;
import codeine.executer.Task;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NotificationsFetchAndUpdateTask implements Task {

	private static final Logger log = Logger.getLogger(NotificationsFetchAndUpdateTask.class);

	private IConfigurationManager configurationManager;
	private AggregateNotification mailCreator;
	private AggregateMailPrepare mailPrepare;
	private MailStrategy mailsStrategy;
	private CollectionTypeGetter collectionTypeGetter;
	private List<AlertsMysqlConnector> alertsConnectors;
	private List<NotificationsMysqlConnector> notificationsConnectors;
	private ExperimentalConfJsonStore webConfJsonStore;

	
	@Inject 
	public NotificationsFetchAndUpdateTask(IConfigurationManager configurationManager, AggregateNotification mailCreator,
			AggregateMailPrepare mailPrepare, MailStrategy mailsStrategy, CollectionTypeGetter collectionTypeGetter,
			AlertsMysqlConnectorDatabaseConnectorListProvider alertsMysqlConnectorDatabaseConnectorListProvider, ExperimentalConfJsonStore webConfJsonStore,
			NotificationsMysqlConnectorDatabaseConnectorListProvider notificationsMysqlConnectorDatabaseConnectorListProvider) {
		super();
		this.configurationManager = configurationManager;
		this.mailCreator = mailCreator;
		this.mailPrepare = mailPrepare;
		this.mailsStrategy = mailsStrategy;
		this.collectionTypeGetter = collectionTypeGetter;
		this.alertsConnectors = alertsMysqlConnectorDatabaseConnectorListProvider.get();
		this.notificationsConnectors = notificationsMysqlConnectorDatabaseConnectorListProvider.get();
		this.webConfJsonStore = webConfJsonStore;
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
			for (NotificationsMysqlConnector c : notificationsConnectors) {
				c.removeOldAlerts();
			}
		}
	}

	private void workOnCollectionType(AlertsCollectionType alertsCollectionType) {
		Multimap<String, CollectorNotificationJson> allItems = HashMultimap.create();
		for (AlertsMysqlConnector c : alertsConnectors) {
			try {
				log.info("fetching from " + c);
				allItems.putAll(c.getAlertsAndUpdate(alertsCollectionType));
			} catch (Exception e) {
				log.info("error fetching alerts from db " + c, e);
			}
		}
		for (NotificationsMysqlConnector c : notificationsConnectors) {
			try {
				log.info("fetching from " + c);
				allItems.putAll(c.getAlertsAndUpdate(alertsCollectionType));
			} catch (Exception e) {
				log.info("error fetching alerts from db " + c, e);
			}
		}
		List<NotificationContent> notificationContent = mailCreator.prepareMailsToUsers(alertsCollectionType, allItems, configurationManager.getConfiguredProjects());
		List<Mail> mails = mailPrepare.prepare(notificationContent, alertsCollectionType);
		for (Mail mail : mails) {
			log.info("sending mail to " + mail.recipients() + " with subject " + mail.subject());
			if (webConfJsonStore.get().readonly_web_server()) {
				log.info("read only mode");
				continue;
			}
			try {
				mailsStrategy.sendMail(mail);
			} catch (Exception e) {
				log.warn("error in mail send " + mail,e);
			}
		}
	}
}
