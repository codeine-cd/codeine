package codeine.api;


public class VersionItemInfo {

	private final String version_label;
	private final String version_name;
	private int fail_percent, success_percent, count;

	public VersionItemInfo(String version_label, String version_name, int failPercent, int successPercent, int count) {
		super();
		this.version_label = version_label;
		this.version_name = version_name;
		this.fail_percent = failPercent;
		this.success_percent = successPercent;
		this.count = count;
	}

	public int count() {
		return count;
	}

	public String version_label() {
		return version_label;
	}

	public String version_name() {
		return version_name;
	}

	public int fail_percent() {
		return fail_percent;
	}

	public int success_percent() {
		return success_percent;
	}

	@Override
	public String toString() {
		return "VersionItemInfo [version_label=" + version_label + ", version_name=" + version_name + ", fail_percent="
				+ fail_percent + ", success_percent=" + success_percent + ", count=" + count + "]";
	}

	
}
