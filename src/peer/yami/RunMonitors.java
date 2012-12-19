package yami;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import yami.configuration.CollectorRule;
import yami.configuration.ConfigurationManager;
import yami.configuration.HttpCollector;
import yami.configuration.Node;
import yami.model.Constants;
import yami.model.Result;
import yami.utils.ProcessExecuter;

public class RunMonitors implements Task
{
	private Node node;
	private static final Logger log = Logger.getLogger(RunMonitors.class);
	
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
				log.debug("no collector defined for monitor file " + monitor.getName() + ", skipping exectution");
				continue;
			}
			runMonitor(monitor, c);
		}
	}
	
	private void runMonitor(File monitor, HttpCollector c)
	{
		if (monitor.isFile() && monitor.canExecute())
		{
			try
			{
				List<String> cmd = buildCmd(monitor, c);
				log.info("Will execute " + cmd);
				Result res = ProcessExecuter.execute(cmd);
				log.info(monitor.getName() + " ended with res=" + res.success());
				writeResult(res, monitor.getName());
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
		// cmdList.addAll();
		return cmd;
	}
	
	private void writeResult(Result res, String outputFileName) throws IOException
	{
		BufferedWriter out = getWriter(outputFileName);
		out.write(res.success() ? "True\n" : "False\n");
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
