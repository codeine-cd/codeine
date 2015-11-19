package codeine.plugins;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AfterProjectModifyPlugin {

	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	
	private static final Logger log = Logger.getLogger(AfterProjectModifyPlugin.class);

	public enum StatusChange {add,remove,modify}
	
	public void call(ProjectJson projectJson, StatusChange newStatus, String username) {
		String after_project_modify_plugin = experimentalConfJsonStore.get().after_project_modify_plugin();
		if (null != after_project_modify_plugin) {
			log.info("calling after_project_modify_plugin for project " + projectJson.name() + " by  user " + username
					+ " with status " + newStatus.toString());
			List<String> cmd = Lists.newArrayList(after_project_modify_plugin);
			Map<String, String> env = Maps.newHashMap();
			env.put(Constants.EXECUTION_ENV_PROJECT_STATUS, String.valueOf(newStatus));
			env.put(Constants.EXECUTION_ENV_PROJECT_NAME, projectJson.name());
			env.put(Constants.EXECUTION_ENV_USER_NAME, username);
			log.info("executing " + after_project_modify_plugin);
			Result result = new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
			log.info("calling after_project_modify_plugin for project " + projectJson.name() + " finished " + result.toStringLong());
		}
	}

}
