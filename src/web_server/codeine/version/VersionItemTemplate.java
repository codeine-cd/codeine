package codeine.version;

import codeine.api.VersionItemInfo;
import codeine.utils.network.HttpUtils;

@SuppressWarnings("unused")
public class VersionItemTemplate extends VersionItemInfo{
	private String version_name_encoded;
	private String hide_no_errors;
	private int total_fail;
	
	public VersionItemTemplate(VersionItemInfo input) {
		super(input.version_label(), input.version_name(), input.fail_percent(), input.success_percent(), input.count());
		this.version_name_encoded = HttpUtils.encode(input.version_name());
		this.hide_no_errors = input.fail_percent() == 0 ? "display: none;" : "";
		this.total_fail = (input.count() * 100) /  input.fail_percent();
	}
}