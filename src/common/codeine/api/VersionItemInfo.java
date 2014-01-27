package codeine.api;


public class VersionItemInfo {

	private final String version_label;
	private final String version_name;
	private int fail_percent, success_percent, count, total_fail;
	private int max;

	public VersionItemInfo(String version_label, String version_name, int total_fail, int count, int max) {
		super();
		this.version_label = version_label;
		this.version_name = version_name;
		this.total_fail = total_fail;
		this.count = count;
		this.max = max;
		if (max == 0) {
			this.fail_percent = 0;
			this.success_percent = 0;
		} else {
			this.fail_percent = (int) Math.ceil(total_fail * 100 / (double)max);
			this.success_percent = (int) Math.ceil((count - total_fail) * 100 / (double)max);
			if (fail_percent + success_percent > 100){
				success_percent = 100 - fail_percent;
			}
		}
	}

	public int count() {
		return count;
	}
	public int max() {
		return max;
	}

	public String version_label() {
		return version_label;
	}

	public String version_name() {
		return version_name;
	}

	public int total_fail() {
		return total_fail;
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
