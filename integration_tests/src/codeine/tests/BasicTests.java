package codeine.tests;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import codeine.CodeineApiClient;
import codeine.api.NodeWithMonitorsInfo;
import codeine.api.VersionItemInfo;
import codeine.jsons.project.ProjectJson;
import codeine.tests_framework.TestsSuite;

import com.google.common.collect.Lists;

public class BasicTests extends TestsSuite{

	@Test
	public void sanityTest() {
		CodeineApiClient client = new CodeineApiClient(codeineConf().web_server_host(), codeineConf().web_server_port());
		List<ProjectJson> projects = client.projects();
		System.out.println(projects);
		ProjectJson projectJson = client.project("integration_test_project");
		Map<String, VersionItemInfo> projectStatus = client.projectStatus("integration_test_project");
		List<NodeWithMonitorsInfo> nodes = Lists.newArrayList(client.projectNodes(projectJson.name(), projectStatus.entrySet().iterator().next().getKey()));
		assertEquals(1, nodes.size());
//		CommandInfo commandJson = projectJson.commandForName("short-command");
//		ScehudleCommandExecutionInfo data = ScehudleCommandExecutionInfo.createImmediate(commandJson, nodes.subList(0, 10));
		
		
//		System.out.println(data);
//		String runCommand = client.runCommand(data);
//		System.out.println(runCommand);
//		ThreadUtils.sleep(3000);
//		List<CommandStatusJson> commandHistory = client.commandHistory("test_project");
//		System.out.println(commandHistory);
		
	}

}
