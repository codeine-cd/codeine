package yami;

import static com.google.common.collect.Maps.newHashMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import yami.configuration.CollectorRule;
import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.configuration.VersionCollector;
import yami.model.Constants;
import yami.model.Result;
import yami.utils.ProcessExecuter;

import com.google.common.base.Stopwatch;

public class RunMonitors implements Task
{
	private Node node;
	private static final Logger log = Logger.getLogger(RunMonitors.class);
	private Map<String, Long> lastRun = newHashMap();
	
	public RunMonitors(Node node)
	{
		this.node = node;
	}
	
	@Override
	public void exec()
	{
		for (File monitor : getMonitors())
		{
			HttpCollector c = ConfigurationManager.getInstance().getConfiguredProject().getCollector(monitor.getName());
			if (null == c)
			{
			    if (monitor.getName().equals(new VersionCollector().name))
			    {
			    	c = new VersionCollector();
			    }
			}
			runOnce(monitor, c);
		}
	}

	protected void runOnce(File monitor, HttpCollector c) {
	    if (null == c)
	    {
	    	log.debug("no collector defined for monitor file " + monitor.getName() + ", skipping exectution");
	    	return;
	    }
	    Long lastRuntime = lastRun.get(c.name);
	    if (lastRuntime == null || System.currentTimeMillis() - lastRuntime > minInterval(c))
	    {
	    	runMonitor(monitor, c);
	    	lastRun.put(c.name, System.currentTimeMillis());
	    }
	}

	private int minInterval(HttpCollector c)
	{
		if (null == c.minInterval)
		{
			return 20000;
		}
		return c.minInterval  * 60000;
	}
	
	private void runMonitor(File monitor, HttpCollector c)
	{
		if (monitor.isFile() && monitor.canExecute())
		{
			try
			{
				List<String> cmd = buildCmd(monitor, c);
				log.info("Will execute " + cmd);
				Stopwatch stopwatch = new Stopwatch().start();
				Result res = ProcessExecuter.execute(cmd);
				stopwatch.stop();
//				long millis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
				log.info(monitor.getName() + " ended with res=" + res.success() + " that took: " + stopwatch);
				writeResult(res, monitor.getName(), c , stopwatch, cmd);
			}
			catch (Exception e)
			{
				log.warn("got exception when executing monitor ", e);
			}
		}
	}
	
	private List<String> buildCmd(File monitor, HttpCollector c)
	{
		List<String> cmd = new ArrayList<String>();
		cmd.add(monitor.getAbsolutePath());
		for (CollectorRule r : c.rules)
		{
			if (r.shouldApplyForNode(node.name))
			{
				for (String arg : r.arg)
				{
					cmd.add(arg);
				}
			}
		}
		return cmd;
	}
	
	private void writeResult(Result res, String outputFileName, HttpCollector c, Stopwatch stopwatch, List<String> cmd) throws IOException
	{
		BufferedWriter out = getWriter(outputFileName);
		out.write("+------------------------------------------------------------------+\n");
		out.write("| command: " + cmd + "\n");
		out.write("| exitstatus: " + res.exit() + "\n");
		out.write("| completed at: " + new Date() + "\n");
		out.write("| length: " + stopwatch + "\n");
		out.write("+------------------------------------------------------------------+\n");
		out.write(res.output);
		out.close();
	}
	
	private BufferedWriter getWriter(String name) throws IOException
	{
		String out = Constants.getInstallDir() + Constants.NODES_DIR + node.name + "/" + name + ".txt";
		log.debug("Output for " + name + " will be written to: " + out);
		FileWriter fstream = new FileWriter(out);
		BufferedWriter bw = new BufferedWriter(fstream);
		return bw;
	}
	
	private List<File> getMonitors()
	{
		String dir = Constants.getInstallDir() + Constants.MONITORS_DIR;
		log.debug("Collecting monitors from " + dir);
		File folder = new File(dir);
		List<File> files = Arrays.asList(folder.listFiles());
		if (files.isEmpty())
		{
			log.debug("No files found to execute under " + dir);
		}
		else
		{
			log.debug("Found monitors: " + files);
		}
		return files;
	}
	
}
