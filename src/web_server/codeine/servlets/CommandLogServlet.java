package codeine.servlets;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import codeine.command_peer.CommandStatusJson;
import codeine.configuration.Links;
import codeine.configuration.PathHelper;
import codeine.jsons.CommandDataJson;
import codeine.model.Constants;
import codeine.servlet.AbstractServlet;
import codeine.utils.FilesUtils;
import codeine.utils.TextFileUtils;

import com.google.common.collect.Lists;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;

public class CommandLogServlet extends AbstractServlet {
	private static final Logger log = Logger.getLogger(CommandLogServlet.class);
	private static final long serialVersionUID = 1L;

	@Inject private PathHelper pathHelper;
	@Inject private Links links;
	
	@Override
	protected void myGet(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = getWriter(response);
		String projectName = request.getParameter("project");
		List<CommandStatusJson> active = get(projectName);
		writer.println(gson().toJson(active));
	}

	private List<CommandStatusJson> get(String projectName) {
		List<CommandStatusJson> $ = Lists.newArrayList();
		String parentDir = pathHelper.getPluginsOutputDir(projectName);
		List<String> filesInDir = FilesUtils.getFilesInDir(parentDir);
		for (String dir : filesInDir) {
			try {
				CommandDataJson j = gson().fromJson(TextFileUtils.getContents(parentDir + "/" + dir + Constants.JSON_COMMAND_FILE_NAME), CommandDataJson.class); 
				String link = links.getCommandOutputGuiLink(j);
				int size = j.nodes_list().size();
				int successSize = j.success_list().size();
				int failSize = j.fail_list().size();
				$.add(new CommandStatusJson(j.command(), link, projectName, size, successSize * 100 / size, failSize * 100 / size, j.start_time() ,j.id(), j.finished()));
			} catch (JsonSyntaxException e) {
				log.warn("failed in command " +  dir, e);
			}
		}
		Comparator<CommandStatusJson> comp = new Comparator<CommandStatusJson>() {
			@Override
			public int compare(CommandStatusJson o1, CommandStatusJson o2) {
				return (int) (o2.id() - o1.id());
			}
		};
		Collections.sort($, comp);
		return $;
	}

}
