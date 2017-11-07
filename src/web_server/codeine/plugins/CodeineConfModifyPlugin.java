package codeine.plugins;

import codeine.jsons.global.ExperimentalConfJsonStore;
import codeine.model.Constants;
import codeine.model.Result;
import codeine.utils.StringUtils;
import codeine.utils.os_process.ProcessExecuter.ProcessExecuterBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.log4j.Logger;

public class CodeineConfModifyPlugin {

    @Inject
    private ExperimentalConfJsonStore experimentalConfJsonStore;

    private static final Logger log = Logger.getLogger(CodeineConfModifyPlugin.class);

    public enum Step {pre, post}

    public void call(Step step, String username) {
        String codeine_conf_modify_plugin = experimentalConfJsonStore.get().codeine_conf_modify_plugin();
        if (!StringUtils.isEmpty(codeine_conf_modify_plugin)) {
            log.info("calling codeine conf modify by  user " + username + " in step " + step.toString());
            List<String> cmd = Lists.newArrayList(codeine_conf_modify_plugin);
            Map<String, String> env = Maps.newHashMap();
            env.put(Constants.EXECUTION_ENV_CONFIGURATION_STEP, String.valueOf(step));
            env.put(Constants.EXECUTION_ENV_USER_NAME, username);
            log.info("executing " + codeine_conf_modify_plugin);
            Result result = new ProcessExecuterBuilder(cmd).timeoutInMinutes(2).env(env).build().execute();
            log.info("calling codeine_conf_modify_plugin finished " + result.toStringLong());
        }
    }
}
