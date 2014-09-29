package integration_tests.tests;

import static org.junit.Assert.assertEquals;
import integration_tests.tests_framework.TestsSuite;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import codeine.CodeineApiClient;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.VersionItemInfo;
import codeine.jsons.project.ProjectJson;
import codeine.utils.ThreadUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class BasicTests extends TestsSuite{

	@Test
	public void sanityTest() {
		final CodeineApiClient client = new CodeineApiClient(codeineConf().web_server_host(), codeineConf().web_server_port());
		List<ProjectJson> projects = client.projects();
		System.out.println(projects);
		final ProjectJson projectJson = client.project("integration_test_project");
		final Map<String, VersionItemInfo> projectStatus = client.projectStatus("integration_test_project");
		NodesCountPredicate predicate = new NodesCountPredicate(projectStatus, client, projectJson);
		assertBusyWait(predicate, 60);
		assertEquals(1, predicate.size);
//		CommandInfo commandJson = projectJson.commandForName("short-command");
//		ScehudleCommandExecutionInfo data = ScehudleCommandExecutionInfo.createImmediate(commandJson, nodes.subList(0, 10));
		
		
//		System.out.println(data);
//		String runCommand = client.runCommand(data);
//		System.out.println(runCommand);
//		ThreadUtils.sleep(3000);
//		List<CommandStatusJson> commandHistory = client.commandHistory("test_project");
//		System.out.println(commandHistory);
		
	}

	public static boolean assertBusyWait(Predicate<Void> predicate, int timesToCheck)
	{
		if (timesToCheck <= 0) {
			throw new IllegalArgumentException("timesToCheck less than 1 " + timesToCheck);
		}
		for (int counter = 0; counter < timesToCheck; ++counter)
		{
			if (predicate.apply(null))
			{
				return true;
			}
			ThreadUtils.sleep(1000);
		}
		return false;
	}
	
	private static final class NodesCountPredicate implements Predicate<Void> {
		private final Map<String, VersionItemInfo> projectStatus;
		private final CodeineApiClient client;
		private final ProjectJson projectJson;
		private int size;

		private NodesCountPredicate(Map<String, VersionItemInfo> projectStatus, CodeineApiClient client,
				ProjectJson projectJson) {
			this.projectStatus = projectStatus;
			this.client = client;
			this.projectJson = projectJson;
		}

		@Override
		public boolean apply(Void arg0) {
			List<NodeWithMonitorsInfo> nodes = Lists.newArrayList(client.projectNodes(projectJson.name(), projectStatus.entrySet().iterator().next().getKey()));
			size = nodes.size();
			return size == 1;
		}
	}
}
