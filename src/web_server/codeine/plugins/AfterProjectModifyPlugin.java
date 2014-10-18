package codeine.plugins;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.jsons.project.ProjectJson;
import codeine.model.Constants;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AfterProjectModifyPlugin {

	@Inject private ExperimentalConfJsonStore experimentalConfJsonStore;
	
	private static final Logger log = Logger.getLogger(AfterProjectModifyPlugin.class);

	public enum StatusChange {add,remove,modify}
	
	public void call(ProjectJson projectJson, StatusChange newStatus) {
		String after_project_modify_plugin = experimentalConfJsonStore.get().after_project_modify_plugin();
		if (null != after_project_modify_plugin) {
			log.info("calling after_project_modify_plugin for project " + projectJson.name());
			List<String> cmd = Lists.newArrayList(after_project_modify_plugin);
			Map<String, String> env = Maps.newHashMap();
			env.put(Constants.EXECUTION_ENV_PROJECT_EXISTS, String.valueOf(newStatus));
			env.put(Constants.EXECUTION_ENV_PROJECT_NAME, projectJson.name());
			new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
		}
	}

}
