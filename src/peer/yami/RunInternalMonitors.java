package yami;

import java.io.File;

import yami.configuration.ConfigurationManager;
import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.model.Constants;

public class RunInternalMonitors extends RunMonitors {

	public RunInternalMonitors(Node node, ConfigurationManager configurationManager) {
		super(node, configurationManager);
	}

	@Override
	public void exec() {
		runOnce(new File(Constants.getInstallDir() + Constants.MONITORS_DIR + File.separator + "keepalive"),
				new KeepaliveCollector());
	}

}
