package codeine.servlet;

import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
// TODO - Remove success class from this as this is an API class
public class NodeTemplate {
	
	private String node_alias;
	private String node_name;
	private List<MonitorTemplateLink> failed_monitors;
	private List<String> success_monitors;
	private List<String> tags;
	private String peer_address;
	private String version;

	public NodeTemplate(String node_alias,String node_name, String peer_address, List<MonitorTemplateLink> failed_monitors, String success_class, String version, List<String> tags) {
		this.node_alias = node_alias;
		this.node_name = node_name;
		this.peer_address = peer_address;
		this.failed_monitors = failed_monitors;
		this.version = version;
		this.tags = tags;
		this.success_monitors= Lists.newArrayList();  
		if (failed_monitors.size() == 0)
			success_monitors.add(success_class);
	}
	
}
