package codeine;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import codeine.configuration.IConfigurationManager;
import codeine.executer.Task;
import codeine.nodes.NodesRunner;

public class ConfigurationGetter implements Task {

	public static final long INTERVAL = TimeUnit.MINUTES.toMillis(55);
	@Inject
	private IConfigurationManager configurationManager;
	@Inject
	private NodesRunner nodesRunner;

	@Override
	public void run() {
		configurationManager.refresh();
		nodesRunner.run();
	}

}
