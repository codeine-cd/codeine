package codeine.servlets.api_servlets.angular;

import java.util.List;

import codeine.api.NodeWithMonitorsInfo;
import codeine.jsons.collectors.CollectorExecutionInfo;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class NodeWithMonitorsInfoApi extends NodeWithMonitorsInfo {

	private List<String> failed_monitors;
	private List<String> failed_collectors;
	private List<CollectorInfo> collectors_info = Lists.newArrayList();
	private List<String> ok_monitors;
	private boolean user_can_command;
	
	public NodeWithMonitorsInfoApi(NodeWithMonitorsInfo info, boolean user_can_command) 
	{
		super(info);
		this.failed_monitors = info.failedMonitors();
		this.failed_collectors = info.failed_collectors();
		this.ok_monitors = info.ok_monitors();
		this.user_can_command = user_can_command;
		for (CollectorExecutionInfo collectorInfo : info.collectors().values()) {
			if (collectorInfo.isSuccess() && !StringUtils.isEmpty(collectorInfo.value())) {
				collectors_info.add(new CollectorInfo(collectorInfo.name(), collectorInfo.value()));
			}
		}
	}
	
	private static class CollectorInfo {
		private String name;
		private String value;
		public CollectorInfo(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}
}