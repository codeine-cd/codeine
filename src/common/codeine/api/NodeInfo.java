package codeine.api;

import java.util.List;

import com.google.common.collect.Lists;


public class NodeInfo extends NodeInfoNameAndAlias{

	private List<String> tags = Lists.newArrayList();

	public NodeInfo(){
		
	}
	public NodeInfo(String name) {
		super(name);
	}

	public NodeInfo(String name, String alias) {
		super(name, alias);
	}

	public NodeInfo(String name, String alias, List<String> tags) {
		super(name, alias);
		this.tags = tags;
	}
	
	public List<String> tags() {
		if (null == tags) {
			return Lists.newArrayList();
		}
		return tags;
	}
	
	public List<String> tags(List<String> tags) {
		List<String> $ = this.tags;
		this.tags = tags;
		return $;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}
		
	
}
