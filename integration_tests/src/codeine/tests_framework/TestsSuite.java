package codeine.tests_framework;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import codeine.jsons.global.GlobalConfigurationJson;
import codeine.jsons.global.MysqlConfigurationJson;
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
		injector = Guice.createInjector();
		String confFile = System.getenv("CODEINE_INTEGRATION_CONF_JSON");
		log.info("cheking work dir ");
		executeAndPrint("pwd");
		log.info("confFile is " + confFile);
		conf = getJsonFileUtils().getConfFromFile(confFile, TestSuiteConfiguration.class);
//		openTar();
		configure();
		startProcessess();
	}
	
	private void startProcessess() {
//		kill mysql
//		change it to use the new server
		executeAndPrint(testsConf().scripts_dir() + "/start_processes.pl " + testsConf().dist_dir() + " " + testsConf().mysql_dir());
	}

	private void executeAndPrint(String cmd) {
		log.info(ProcessExecuter.executeSuccess(cmd));
	}

	private void configure() {
		codeineConf = new GlobalConfigurationJson(InetUtils.getLocalHost().getHostName());
		MysqlConfigurationJson mysql = new MysqlConfigurationJson(
				InetUtils.getLocalHost().getHostName(), 17171, conf.mysql_dir(), 
				conf.mysql_lib());
		codeineConf.mysql().add(mysql);
		JsonFileUtils jsonFileUtils = getJsonFileUtils();
		jsonFileUtils.setContent(testsConf().conf_file(), codeineConf);
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

	private void openTar() {
//		String version = VERSION;
//		String dir = DIR;
//		String tar = dir + "/codeine_" + version + ".tar.gz";
//		String r = ProcessExecuter.executeSuccess("tar -xzf " + tar + " --directory=" + dir + "/integration_test/out");
	}

	@After
	public void after() {
		//stop all processes?
	}
}
