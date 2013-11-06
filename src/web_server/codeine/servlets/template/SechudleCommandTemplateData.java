package codeine.servlets.template;

import java.util.List;

import codeine.api.NodeDataJson;
import codeine.command_peer.CommandExcutionType;
import codeine.command_peer.DurationUnits;
import codeine.command_peer.RatioType;
import codeine.servlet.TemplateData;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class SechudleCommandTemplateData extends TemplateData {
	
	private String projectName;
	private String commandName;
	private String nodesJson;
	private List<VersionNodesJson> versionNodes;
	private List<String> commandExcutionTypeOptions, ratioOptions, durationUnitsOptions; 

	public SechudleCommandTemplateData(String projectName, String commandName, List<VersionNodesJson> versionNodes) {
		super();
		List<NodeDataJson> nodes = Lists.newArrayList();
		for (VersionNodesJson versionNode : versionNodes) {
			nodes.addAll(versionNode.node());
		}
		this.nodesJson = new Gson().toJson(nodes);
		this.projectName = projectName;
		this.commandName = commandName;
		this.versionNodes = versionNodes;
		this.ratioOptions = Lists.newArrayList(StringUtils.getEnumNames(RatioType.class));
		this.commandExcutionTypeOptions = Lists.newArrayList(StringUtils.getEnumNames(CommandExcutionType.class));
		this.durationUnitsOptions = Lists.newArrayList(StringUtils.getEnumNames(DurationUnits.class));
	}
	
	

}
