package codeine.jsons.info;

public class CodeineRuntimeInfo {

	private long startTime = System.currentTimeMillis();
	private String version;
	private String component;
	private int port;
	private int pid;
	
	
	public CodeineRuntimeInfo(String version, String component, int pid) {
		super();
		this.version = version;
		this.component = component;
		this.pid = pid;
	}


	public long startTime() {
		return startTime;
	}


	public String component() {
		return component;
	}
	public String version() {
		return version;
	}

	public int port() {
		return port;
	}
	public int pid() {
		return pid;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
