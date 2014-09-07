package codeine.tests_framework;


public class TestSuiteConfiguration {
	private String work_dir;
	private String sources_dir;
	
	public String sources_dir(){
		return sources_dir;
	}
	public String work_dir(){
		return work_dir;
	}
	public String scripts_dir() {
		return sources_dir() + "/integration_tests/scripts";
	}
	public String dist_dir() {
		return work_dir() + "/deployment";
	}
	public String mysql_work_dir() {
		return work_dir() + "/mysql_work_dir";
	}
	public String mysql_lib() {
		return sources_dir() + "libs/mysql";
	}
	public String conf_file() {
		return dist_dir() + "/conf/codeine.conf.json";
	}
	public String projects_dir() {
		return dist_dir() + "/project";
	}
}
