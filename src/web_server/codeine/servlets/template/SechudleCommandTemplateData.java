package codeine.servlets.template;

import java.util.List;

import codeine.api.CommandExcutionType;
import codeine.api.DurationUnits;
import codeine.api.NodeWithPeerInfo;
import codeine.api.RatioType;
import codeine.jsons.command.CommandInfo;
import codeine.servlet.TemplateData;
import codeine.utils.StringUtils;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

@SuppressWarnings("unused")
public class SechudleCommandTemplateData extends TemplateData {
	
	private String project_name;
	private String command_name;
	private String command_title;
	private String nodesJson;
	private List<VersionNodesJson> versionNodes;
	private List<String> commandExcutionTypeOptions, ratioOptions, durationUnitsOptions;
	private String command_info; 

	public SechudleCommandTemplateData(String projectName, CommandInfo commandInfo, List<VersionNodesJson> versionNodes) {
		super();
		this.command_info = new Gson().toJson(commandInfo);
		List<NodeWithPeerInfo> nodes = Lists.newArrayList();
		for (VersionNodesJson versionNode : versionNodes) {
			nodes.addAll(versionNode.node());
		}
		this.nodesJson = new Gson().toJson(nodes);
		this.project_name = projectName;
		this.command_name = commandInfo.command_name();
		this.command_title = commandInfo.title();
		this.versionNodes = versionNodes;
		this.ratioOptions = Lists.newArrayList(StringUtils.getEnumNames(RatioType.class));
		this.commandExcutionTypeOptions = Lists.newArrayList(StringUtils.getEnumNames(CommandExcutionType.class));
		this.durationUnitsOptions = Lists.newArrayList(StringUtils.getEnumNames(DurationUnits.class));
	}
	
	

}
