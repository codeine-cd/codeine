package codeine.version;

@SuppressWarnings("unused")
public class VersionItem {

	private final String version_label;
	private final String version_name;
	private int failPercent, successPercent, count;
	private String hideNoErrors;

	public VersionItem(String version_label, String version_name, int failPercent, int successPercent, int count) {
		super();
		this.version_label = version_label;
		this.version_name = version_name;
		this.failPercent = failPercent;
		this.successPercent = successPercent;
		this.count = count;
		this.hideNoErrors = failPercent == 0 ? "display: none;" : "";
	}

	public int count() {
		return count;
	}

}
