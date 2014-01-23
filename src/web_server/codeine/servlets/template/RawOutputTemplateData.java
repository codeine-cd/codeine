package codeine.servlets.template;

import java.text.SimpleDateFormat;
import java.util.Date;

import codeine.jsons.CommandExecutionStatusInfo;
import codeine.servlet.TemplateData;

@SuppressWarnings("unused")
public class RawOutputTemplateData extends TemplateData {

	private String content;
	private CommandExecutionStatusInfo commandInfo;
	private String startDate;
	private int nodesCount;
	private int failedNodesCount;

	public RawOutputTemplateData(CommandExecutionStatusInfo commandInfo, String content) {
		super();
		this.commandInfo = commandInfo;
		this.content = content;
		this.startDate = new SimpleDateFormat("dd/MM/yyyy @ H:mm:ss").format(new Date());
		this.nodesCount = commandInfo.nodes_list().size();
		this.failedNodesCount = commandInfo.fail_list().size();
	}

}
