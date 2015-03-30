package integration_tests.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import integration_tests.tests_framework.TestsSuite;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import codeine.CodeineApiClient;
import codeine.api.CommandExcutionType;
import codeine.api.CommandStatusJson;
import codeine.api.DurationUnits;
import codeine.api.NodeInfo;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.NodeWithPeerInfo;
import codeine.api.RatioType;
import codeine.api.ScehudleCommandExecutionInfo;
import codeine.api.VersionItemInfo;
import codeine.jsons.collectors.CollectorInfo;
import codeine.jsons.collectors.CollectorInfo.CollectorType;
import codeine.jsons.command.CommandInfo;
import codeine.jsons.command.CommandParameterInfo;
import codeine.jsons.project.CodeineProject;
import codeine.jsons.project.ProjectJson;
import codeine.utils.ThreadUtils;
import codeine.utils.network.InetUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

public class BasicTest extends TestsSuite {

	
	private static final String TEST_PROJECT_NAME = "integration_test_project";
	private static final String TEST_COMMAND_NAME = "test_command";
	private static final String TEST_COLLECTOR_NAME = "test_collector";
	private ProjectJson project;
	private String file = "/tmp/codeine_integration_test_" + System.currentTimeMillis();
	
	
	
	
	public BasicTest() {
		super();
		initProject();
	}

	private void initProject() {
		project = new ProjectJson(TEST_PROJECT_NAME);
		project.nodes_info().add(new NodeInfo(InetUtils.getLocalHost().getHostName()));
		CollectorInfo collector = new CollectorInfo(TEST_COLLECTOR_NAME, "echo 'hi'", null, null, CollectorType.Monitor, false);
		project.collectors().add(collector);
		CommandExcutionType command_strategy = CommandExcutionType.Single;
		Integer duration = null;
		Integer concurrency = 1;
		Integer error_percent_val = null;
		DurationUnits duration_units = null;
		String script_content = "touch " + file;
		List<CommandParameterInfo> parameters = Lists.newArrayList();
		Boolean prevent_override = false;
		RatioType ratio = null;
		CommandInfo command = new CommandInfo(TEST_COMMAND_NAME, "description", project.name(), null, 1, null, duration, concurrency, error_percent_val, command_strategy, false, duration_units, ratio , false, prevent_override, parameters, script_content);
		project.commands().add(command);
	}

	@Test
	public void sanityTest() {
		final CodeineApiClient client = new CodeineApiClient(codeineConf().web_server_host(), codeineConf().web_server_port());
		List<CodeineProject> projects = client.projects();
		assertEquals(1, projects.size());
		final ProjectJson projectJson = client.project(TEST_PROJECT_NAME);
		final Map<String, VersionItemInfo> projectStatus = client.projectStatus(TEST_PROJECT_NAME);
		NodesCountPredicate predicate = new NodesCountPredicate(projectStatus, client, projectJson);
		assertBusyWait(predicate, 60);
		assertEquals(1, predicate.size);
		
	}
	
	@Test
	@Ignore
	public void testCollector() {
		final CodeineApiClient client = new CodeineApiClient(codeineConf().web_server_host(), codeineConf().web_server_port());
		List<CodeineProject> projects = client.projects();
		assertEquals(1, projects.size());
		final ProjectJson projectJson = client.project(TEST_PROJECT_NAME);
		final Map<String, VersionItemInfo> projectStatus = client.projectStatus(TEST_PROJECT_NAME);
		NodesCountPredicate predicate = new NodesCountPredicate(projectStatus, client, projectJson);
		assertBusyWait(predicate, 60);
		assertTrue(predicate.nodes.get(0).collectors().get(TEST_COLLECTOR_NAME).isSuccess());
	}
	@Test
	@Ignore
	public void testCommand() {
		final CodeineApiClient client = new CodeineApiClient(codeineConf().web_server_host(), codeineConf().web_server_port());
		List<CodeineProject> projects = client.projects();
		assertEquals(1, projects.size());
		CommandInfo command_info = new CommandInfo(TEST_COMMAND_NAME, TEST_PROJECT_NAME);
		ScehudleCommandExecutionInfo data = new ScehudleCommandExecutionInfo(command_info, Lists.<NodeWithPeerInfo>newArrayList(), true);
		client.runCommand(data);
		ThreadUtils.sleep(5000);
		List<CommandStatusJson> commandHistory = client.commandHistory(TEST_PROJECT_NAME);
		assertEquals(1, commandHistory.size());
		assertTrue(commandHistory.get(0).finished());
		assertEquals(100, commandHistory.get(0).successPercent);
	}
	
	@Override
	protected ProjectJson getProjectConfiguration() {
		return project;
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
		private List<NodeWithMonitorsInfo> nodes;

		private NodesCountPredicate(Map<String, VersionItemInfo> projectStatus, CodeineApiClient client,
				ProjectJson projectJson) {
			this.projectStatus = projectStatus;
			this.client = client;
			this.projectJson = projectJson;
		}

		@Override
		public boolean apply(Void arg0) {
			nodes = Lists.newArrayList(client.projectNodes(projectJson.name(), projectStatus.entrySet().iterator().next().getKey()));
			size = nodes.size();
			return size == 1;
		}
	}
}
