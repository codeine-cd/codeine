package codeine.servlet;

import java.util.List;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class NodeTemplate {
	
	private String node_alias;
	private String node_name;
	private List<TemplateLink> failed_monitors;
	private List<String> success_monitors;
	private String peer_address;

	public NodeTemplate(String node_alias,String node_name, String peer_address, List<TemplateLink> failed_monitors) {
		this.node_alias = node_alias;
		this.node_name = node_name;
		this.peer_address = peer_address;
		this.failed_monitors = failed_monitors;
		this.success_monitors= Lists.newArrayList();  
		if (failed_monitors.size() == 0)
			success_monitors.add("");
	}
	
}
