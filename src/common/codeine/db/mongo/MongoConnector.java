package codeine.db.mongo;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.db.IAlertsDatabaseConnector;
import codeine.db.IStatusDatabaseConnector;
import codeine.jsons.labels.LabelJsonProvider;
import codeine.jsons.labels.ProjectLabelVersionJson;
import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.jsons.peer_status.PeerStatusJsonV2;
import codeine.model.Constants;
import codeine.utils.ExceptionUtils;
import codeine.utils.MongoUtils;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

public class MongoConnector implements IAlertsDatabaseConnector, LabelJsonProvider, IStatusDatabaseConnector {

	private static final Logger log = Logger.getLogger(MongoConnector.class);

	@Inject
	private Gson gson;
	@Inject
	private MongoClient mongoClient;

	private <T> void onItems(Class<T> type, DBObject query, Function<ObjectWithCollection, Void> function) {
		MongoClient mongoClient = getClient();
		try {
			DBCollection collection = getCollection(type, mongoClient);
			DBCursor cursor = collection.find(query);
			try {
				while (cursor.hasNext()) {
					final DBObject obj = cursor.next();
					log.debug("got " + obj);
					function.apply(new ObjectWithCollection(obj, collection));
				}
			} finally {
				cursor.close();
			}
		} finally {
//			mongoClient.close();
		}
	}

	public static class ObjectWithCollection{

		private DBObject object;
		private DBCollection collection;

		public ObjectWithCollection(DBObject object, DBCollection collection) {
			this.object = object;
			this.collection = collection;
		}

		public DBCollection collection() {
			return collection;
		}

		public DBObject object() {
			return object;
		}
		
	}
	private MongoClient getClient() {
		return mongoClient;
	}
	
	private <T> void putInternal(T t) {

		MongoClient mongoClient = getClient();
		try {
			DBCollection coll = getCollection(t.getClass(), mongoClient);
			String json = gson.toJson(t);
			log.debug("json is " + json);
			DBObject doc = (DBObject) JSON.parse(encode(json));
			log.debug("insert " + doc);
			coll.insert(doc);
		} catch (Exception e) {
			throw ExceptionUtils.asUnchecked(e);
		} finally {
//			mongoClient.close();
		}

	}
	
	@Override
	public void putReplaceStatus(PeerStatusJsonV2 p){
		delete(PeerStatusJsonV2.class, getStatusQuery(p));
		putInternal(p);
	}
	
	private DBObject getStatusQuery(PeerStatusJsonV2 p) {
		return new BasicDBObject("peer_key", encode(p.peer_key()));
	}

	private String encode(String key) {
		return MongoUtils.encode(key);
	}
	private String decode(String key) {
		return MongoUtils.decode(key);
	}

	private <T> DBCollection getCollection(Class<T> type, MongoClient mongoClient) {
		DB db = mongoClient.getDB(Constants.DB_NAME);
		DBCollection coll = db.getCollection(type.getCanonicalName());
		return coll;
	}

	@Override
	public Multimap<String, CollectorNotificationJson> getAlertsAndUpdate(final AlertsCollectionType collType) {
		final Multimap<String, CollectorNotificationJson> allItems = HashMultimap.create();
		DBObject alertsQuery = getAlertsQuery(collType);
		Function<ObjectWithCollection, Void> function = new Function<ObjectWithCollection, Void>() {
			@Override
			public Void apply(ObjectWithCollection input) {
				final CollectorNotificationJson collectorNotification = gson.fromJson(decode(input.object().toString()), CollectorNotificationJson.class);
				log.debug("Considering " + collectorNotification);
				input.object().put("collection_type", collType.toLong());
				input.object().put("collection_type_update_time", System.currentTimeMillis());
				input.collection().save(input.object());
				allItems.put(collectorNotification.project_name(),collectorNotification);
				return null;
			}
		};
		onItems(CollectorNotificationJson.class, alertsQuery, function);
		return allItems;
	}
	
	@Override
	public void removeOldAlerts() {
		delete(CollectorNotificationJson.class, getRemoveAlertsQuery());
	}

	private DBObject getRemoveAlertsQuery() {
		BasicDBList and = new BasicDBList();
		long timeToRemove = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(7);
		log.info("will remove older than " + timeToRemove);
		and.add(new BasicDBObject("collection_type_update_time", new BasicDBObject("$lt", timeToRemove)));
		and.add(new BasicDBObject("collection_type", AlertsCollectionType.Daily.toLong()));
		DBObject query = new BasicDBObject("$and", and);
		return query;
	}

	private DBObject getAlertsQuery(AlertsCollectionType collType) {
		BasicDBList or = new BasicDBList();
		or.add(new BasicDBObject("collection_type", new BasicDBObject("$lt", collType.toLong())));
		or.add(new BasicDBObject("collection_type", new BasicDBObject("$exists", false)));
		DBObject query = new BasicDBObject("$or", or);
		return query;
	}

	private DBObject getAllQuery() {
		return new BasicDBObject();
	}

	private DBObject getLabelsQuery(String project) {
		return new BasicDBObject("project", encode(project));
	}
	
	@Override
	public Set<ProjectLabelVersionJson> versions(String project) {
		final Set<ProjectLabelVersionJson> $ = Sets.newHashSet();
		onJsonItems(ProjectLabelVersionJson.class, getLabelsQuery(project), new Function<ProjectLabelVersionJson, Void>() {
			@Override
			public Void apply(ProjectLabelVersionJson input) {
				$.add(input);
				return null;
			}
		});
		return $;
	}

	private <T> void onJsonItems(final Class<T> type, DBObject query, final Function<T, Void> function) {
		Function<ObjectWithCollection, Void> function1 = new Function<MongoConnector.ObjectWithCollection, Void>() {
			@Override
			public Void apply(ObjectWithCollection input) {
				T item = gson.fromJson(decode(input.object().toString()), type);
				log.debug("parsed item " + item);
				function.apply(item);
				return null;
			}
		};
		onItems(type, query, function1);
	}

	@Override
	public void updateLabel(ProjectLabelVersionJson versionLabelJson) {
		deleteLabel(versionLabelJson.label(), versionLabelJson.project());
		putInternal(versionLabelJson);
	}

	@Override
	public void deleteLabel(String label, String project) {
		delete(ProjectLabelVersionJson.class, getLabelQuery(label, project));
	}

	private void delete(Class<?> type, DBObject alertsQuery) {
		Function<ObjectWithCollection, Void> function = new Function<ObjectWithCollection, Void>() {
			@Override
			public Void apply(ObjectWithCollection input) {
				log.debug("removing " + input.object());
				input.collection().remove(input.object());
				return null;
			}
		};
		onItems(type, alertsQuery, function);
	}

	private DBObject getLabelQuery(String label, String project) {
		return BasicDBObjectBuilder.start().add("label", label).add("project", project).get();
	}
	private DBObject getVersionQuery(String version, String project) {
		BasicDBList and = new BasicDBList();
		and.add(new BasicDBObject("version", encode(version)));
		and.add(new BasicDBObject("project", encode(project)));
		DBObject query = new BasicDBObject("$and", and);
		return query;
	}

	@Override
	public String labelForVersion(String version, String project) {
		final Set<String> $ = Sets.newHashSet();
		onJsonItems(ProjectLabelVersionJson.class, getVersionQuery(version, project), new Function<ProjectLabelVersionJson, Void>() {
			@Override
			public Void apply(ProjectLabelVersionJson input) {
				$.add(input.label());
				return null;
			}
		});
		if ($.isEmpty()){
			return version;
		}
		return $.iterator().next();
	}

	@Override
	public String versionForLabel(String label, String project) {
		final Set<String> $ = Sets.newHashSet();
		onJsonItems(ProjectLabelVersionJson.class, getLabelQuery(label, project), new Function<ProjectLabelVersionJson, Void>() {
			@Override
			public Void apply(ProjectLabelVersionJson input) {
				$.add(input.version());
				return null;
			}
		});
		if ($.isEmpty()){
			return label;
		}
		return $.iterator().next();
	}

	@Override
	public Map<String, PeerStatusJsonV2> getPeersStatus(){
		Map<String, PeerStatusJsonV2> $ = Maps.newHashMap();
		List<PeerStatusJsonV2> allItems = getAllItems(PeerStatusJsonV2.class);
		for (PeerStatusJsonV2 projectStatusList : allItems) {
			$.put(projectStatusList.peer_key(), projectStatusList);
		}
		return $;
	}

	private <T> List<T> getAllItems(Class<T> class1) {
		final List<T> $ = Lists.newArrayList();
		Function<T, Void> function = new Function<T, Void>() {
			@Override
			public Void apply(T t){
				$.add(t);
				return null;
			}
			
		};
		onJsonItems(class1, getAllQuery(), function);
		return $;
	}

	@Override
	public void put(CollectorNotificationJson collectorNotificationJson) {
		putInternal(collectorNotificationJson);
	}

	@Override
	public void updatePeersStatus(long timeToRemove, long timeToDisc) {
		throw new UnsupportedOperationException();
	}

}
