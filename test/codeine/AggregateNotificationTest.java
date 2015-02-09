package codeine;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import codeine.jsons.mails.AlertsCollectionType;
import codeine.jsons.mails.CollectorNotificationJson;
import codeine.jsons.project.MailPolicyJson;
import codeine.jsons.project.ProjectJson;
import codeine.mail.AggregateNotification;
import codeine.mail.NotificationContent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class AggregateNotificationTest {

	private AggregateNotification tested = new AggregateNotification();
	private List<ProjectJson> projects = Lists.newArrayList();
	private Multimap<String, CollectorNotificationJson> projectNameToItems = HashMultimap.create();;
	
	@Test
	public void testNoItems() {
		assertTrue(tested.prepareMailsToUsers(AlertsCollectionType.Immediately, HashMultimap.<String, CollectorNotificationJson>create(), Lists.<ProjectJson>newArrayList()).isEmpty());
	}
	@Test
	public void testNoItemsWithConfiguredProject_shouldNotSend() {
		ProjectJson projectJson = createProject("myproject");
		createPolicy(projectJson, "oshai");
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Immediately, projectNameToItems, projects);
		
		assertTrue(result.isEmpty());
	}
	@Test
	public void testOneItems() {
		ProjectJson projectJson = createProject("myproject");
		createNotification(projectJson);
		MailPolicyJson mailPolicy = createPolicy(projectJson, "oshai");
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Immediately, projectNameToItems, projects);
		
		assertEquals(1, result.size());
		assertEquals(mailPolicy.user(), result.get(0).user());
		assertEquals(Lists.newArrayList(projectNameToItems.values()), result.get(0).notifications());
	}
	@Test
	public void testOneItemsButWillNotSend_ScopeIsHourly() {
		ProjectJson projectJson = createProject("myproject");
		createNotification(projectJson);
		createPolicy(projectJson, "oshai", AlertsCollectionType.Daily);
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Hourly, projectNameToItems, projects);
		
		assertEquals(0, result.size());
	}
	@Test
	public void testOneItemsWillSend_ScopeIsDaily() {
		ProjectJson projectJson = createProject("myproject");
		createNotification(projectJson);
		createPolicy(projectJson, "oshai", AlertsCollectionType.Daily);
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Daily, projectNameToItems, projects);
		
		assertEquals(1, result.size());
	}
	
	@Test
	public void testWillNotSendImmediatlyWhenDailyCalled() {
		ProjectJson projectJson = createProject("myproject");
		createNotification(projectJson);
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Daily, projectNameToItems, projects);
		
		assertEquals(0, result.size());
	}
	
	@Test
	public void testTwoItemsForTwoProjects() {
		ProjectJson projectJson = createProject("myproject");
		ProjectJson projectJson1 = createProject("myproject1");
		createNotification(projectJson);
		createNotification(projectJson1);
		String user = "oshai";
		MailPolicyJson mailPolicy = createPolicy(projectJson, user);
		createPolicy(projectJson1, user);
		
		List<NotificationContent> result = tested.prepareMailsToUsers(AlertsCollectionType.Immediately, projectNameToItems, projects);
		
		assertEquals(1, result.size());
		assertEquals(mailPolicy.user(), result.get(0).user());
		ArrayList<CollectorNotificationJson> newArrayList = Lists.newArrayList(projectNameToItems.values());
		assertEquals(newArrayList.size(), result.get(0).notifications().size());
		assertEquals(newArrayList, result.get(0).notifications());
	}
	
	
	
	private MailPolicyJson createPolicy(ProjectJson projectJson, String user) {
		AlertsCollectionType intensity = AlertsCollectionType.Immediately;
		return createPolicy(projectJson, user, intensity);
	}
	private MailPolicyJson createPolicy(ProjectJson projectJson, String user, AlertsCollectionType intensity) {
		MailPolicyJson mailPolicy = new MailPolicyJson(user, intensity);
		projectJson.mail().add(mailPolicy);
		return mailPolicy;
	}
	private CollectorNotificationJson createNotification(ProjectJson projectJson) {
		CollectorNotificationJson value = new CollectorNotificationJson(null, projectJson.name(), "node", "node", null, null, null, 0, null, false, 1);
		projectNameToItems.put(projectJson.name(), value);
		return value;
	}
	private ProjectJson createProject(String projectName) {
		ProjectJson projectJson = new ProjectJson(projectName);
		projects.add(projectJson);
		return projectJson;
	}

}
