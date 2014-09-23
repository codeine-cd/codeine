package codeine.tests_framework;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import codeine.api.NodeInfo;
import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.MysqlConfigurationJson;
import codeine.jsons.project.ProjectJson;
import codeine.utils.JsonFileUtils;
import codeine.utils.network.InetUtils;
import codeine.utils.os_process.ProcessExecuter;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestsSuite {


	private final Logger log = Logger.getLogger(TestsSuite.class);
	private Injector injector;
	private TestSuiteConfiguration conf;
	private GlobalConfigurationJson codeineConf;

	
	static {
		BasicConfigurator.configure();
	}
	
	@Before
	public void init() {
		injector = Guice.createInjector(new CodeineIntegrationTestModule());
		String confFile = System.getenv("CODEINE_INTEGRATION_CONF_JSON");
		log.info("cheking work dir ");
		executeAndPrint("pwd");
		if (null == confFile) {
			confFile = "codeine.integration.conf.json";
		}
		log.info("confFile is " + confFile);
		conf = getJsonFileUtils().getConfFromFile(confFile, TestSuiteConfiguration.class);
//		openTar();
		configure();
		configureProjects();
		startProcessess();
	}
	
	private void startProcessess() {
//		kill mysql
//		change it to use the new server
		executeAndPrint(testsConf().scripts_dir() + "/start_processes.pl " + testsConf().dist_dir() + " " + testsConf().mysql_work_dir());
	}

	private void executeAndPrint(String cmd) {
		log.info(ProcessExecuter.executeSuccess(cmd));
	}

	private void configure() {
		codeineConf = new GlobalConfigurationJson(InetUtils.getLocalHost().getHostName());
		MysqlConfigurationJson mysql = new MysqlConfigurationJson(
				InetUtils.getLocalHost().getHostName(), 17171, conf.mysql_work_dir(), 
				conf.mysql_lib());
		codeineConf.mysql().add(mysql);
		codeineConf.web_server_port(19191);
		getJsonFileUtils().setContent(testsConf().conf_file(), codeineConf);
	}
	private void configureProjects() {
		ProjectJson project = new ProjectJson("integration_test_project");
		project.nodes_info().add(new NodeInfo(InetUtils.getLocalHost().getHostName()));
		getJsonFileUtils().setContent(testsConf().projects_dir() + "/integration_test_project/project.conf.json", project);
	}

	private JsonFileUtils getJsonFileUtils() {
		JsonFileUtils jsonFileUtils = injector().getInstance(JsonFileUtils.class);
		return jsonFileUtils;
	}

	public TestSuiteConfiguration testsConf() {
		return conf;
	}
	public GlobalConfigurationJson codeineConf() {
		return codeineConf;
	}

	private Injector injector() {
		return injector;
	}

//	private void openTar() {
////		String version = VERSION;
////		String dir = DIR;
////		String tar = dir + "/codeine_" + version + ".tar.gz";
////		String r = ProcessExecuter.executeSuccess("tar -xzf " + tar + " --directory=" + dir + "/integration_test/out");
//	}

	@After
	public void after() {
		//stop all processes?
	}
}
