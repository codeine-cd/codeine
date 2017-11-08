package codeine.plugins;

import codeine.executer.ThreadPoolUtils;
import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class AfterProjectModifyPlugin {

	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	
	private static final Logger log = Logger.getLogger(AfterProjectModifyPlugin.class);
	private ThreadPoolExecutor executor = ThreadPoolUtils
		.newThreadPool(1, "AfterProjectModifyPlugin");

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
			if (experimentalConfJsonStore.get().after_project_modify_plugin_async()) {
				log.info("Queuing after_project_modify_plugin run");
				executor.execute(
					new PluginRunnable(after_project_modify_plugin, cmd, env, projectJson));
			}
			else {
				log.info("executing " + after_project_modify_plugin);
				Result result = new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
				log.info("calling after_project_modify_plugin for project " + projectJson.name() + " finished " + result.toStringLong());
			}


		}
	}

	private static class PluginRunnable implements Runnable {

		private final String after_project_modify_plugin;
		private final List<String> cmd;
		private final Map<String, String> env;
		private final ProjectJson projectJson;

		PluginRunnable(String after_project_modify_plugin, List<String> cmd,
			Map<String, String> env,
			ProjectJson projectJson) {
			this.after_project_modify_plugin = after_project_modify_plugin;
			this.cmd = cmd;
			this.env = env;
			this.projectJson = projectJson;
		}

		@Override
        public void run() {
            log.info("executing " + after_project_modify_plugin);
            Result result = new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
            log.info("calling after_project_modify_plugin for project " + projectJson.name() + " finished " + result.toStringLong());
        }
	}
}
