package codeine.configuration;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class HttpCollector
{
	private String name;
	private List<String> includedNode = Lists.newArrayList();
	private List<String> excludedNode = Lists.newArrayList();
	private List<String> dependsOn = Lists.newArrayList();
	private List<CollectorRule> rule = Lists.newArrayList();
	private Integer minInterval;
	private String credentials;
	private boolean notification_enabled = true;
	
	public HttpCollector(){
		
	}
	public HttpCollector(String name, ArrayList<String> includedNode, List<CollectorRule> rule, boolean notification_enabled) {
		this.name = name;
		this.includedNode = includedNode;
		this.rule = rule;
		this.notification_enabled = notification_enabled;
	}

	@Override
	public String toString()
	{
		return "HttpCollector [name=" + name + "]";
	}

	public String credentials() {
		return credentials;
	}

	public List<CollectorRule> rule() {
		return rule;
	}

	public Integer minInterval() {
		return minInterval;
	}

	public String name() {
		return name;
	}
	public boolean notification_enabled() {
		return notification_enabled;
	}

}
