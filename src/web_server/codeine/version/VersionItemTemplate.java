package codeine.version;

import codeine.api.VersionItemInfo;
import codeine.utils.network.HttpUtils;

@SuppressWarnings("unused")
public class VersionItemTemplate extends VersionItemInfo{
	private String version_name_encoded;
	private String hide_no_errors;
	
	public VersionItemTemplate(VersionItemInfo input) {
		super(input.version_label(), input.version_name(), input.total_fail(), input.count(), input.max());
		this.version_name_encoded = HttpUtils.encodeURL(input.version_name());
		this.hide_no_errors = input.fail_percent() == 0 ? "display: none;" : "";
	}
}