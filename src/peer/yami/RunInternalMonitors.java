package yami;

import java.io.File;

import yami.configuration.KeepaliveCollector;
import yami.configuration.Node;
import yami.model.Constants;

public class RunInternalMonitors extends RunMonitors
{
	public RunInternalMonitors(Node node)
	{
		super(node);
	}
	@Override
	public void exec()
	{
	    runOnce(new File(Constants.getInstallDir() + Constants.MONITORS_DIR + File.separator + "keepalive"), new KeepaliveCollector());
	}
	
}
