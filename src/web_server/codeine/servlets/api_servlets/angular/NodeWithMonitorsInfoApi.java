package codeine.servlets.api_servlets.angular;

import java.util.List;

import codeine.api.NodeWithMonitorsInfo;

@SuppressWarnings("unused")
public class NodeWithMonitorsInfoApi extends NodeWithMonitorsInfo {

	private List<String> failed_monitors;
	private List<String> ok_monitors;
	private boolean user_can_command;
	
	public NodeWithMonitorsInfoApi(NodeWithMonitorsInfo info, boolean user_can_command) 
	{
		super(info);
		this.failed_monitors = info.failedMonitors();
		this.ok_monitors = info.ok_monitors();
		this.user_can_command = user_can_command;
	}
}