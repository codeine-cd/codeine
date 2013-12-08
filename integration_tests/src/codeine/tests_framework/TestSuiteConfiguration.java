package codeine.tests_framework;


public class TestSuiteConfiguration {
	private String work_dir;
	private String scripts_dir;
	private String mysql_lib;
	
	public String scripts_dir(){
		return scripts_dir;
	}
	public String work_dir(){
		return work_dir;
	}
	public String dist_dir(){
		return work_dir + "/dist";
	}
	public String mysql_dir() {
		return work_dir + "/mysql";
	}
	public String conf_file() {
		return dist_dir() + "/conf/codeine.conf.json";
	}
	public String mysql_lib() {
		return mysql_lib;
	}
	
}
