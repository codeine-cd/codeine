package codeine.servlets.template;

import java.util.List;

import codeine.api.NodeWithPeerInfo;
import codeine.utils.HtmlUtils;

import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class VersionNodesJson {
	
	private String version;
	private String id;
	private List<NodeWithPeerInfo> node = Lists.newArrayList();
	private Integer count;
	
	public VersionNodesJson(String version) {
		this.version = version;
		setId();
	}

	public void setId() {
		id = HtmlUtils.encodeHtmlElementId(version);
	}
	
	public void updateCount(){
		count = node.size();
	}
	public List<NodeWithPeerInfo> node() {
		return node;
	}
}
