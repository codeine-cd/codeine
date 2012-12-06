package yami;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import yami.configuration.*;
import yami.model.*;
import yami.utils.*;

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
			runMonitor(monitor);
		}		
	}

	private void runMonitor(File monitor)
	{
		if (monitor.isFile() && monitor.canExecute())
		{
			try
			{
				log.info("Will execute " + monitor.getAbsolutePath());
				Result res = ProcessExecuter.execute(monitor.getAbsolutePath());
				log.debug(monitor.getName() +" ended with res=" +res.success());
				writeResult(res, monitor.getName());
			}
			catch (Exception e)
			{
				log.warn("got exception when executing monitor ", e);
			}
		}
	}

	private void writeResult(Result res, String outputFileName) throws IOException
	{
		BufferedWriter out = getWriter(outputFileName);
		out.write(res.success()  ? "True\n" : "False\n");
		out.write(res.output);
		out.close();
	}

	private BufferedWriter getWriter(String name) throws IOException
	{
		String out = Constants.getInstallDir() +  Constants.NODES_DIR + node.name +"/" +name + ".txt";
		log.debug("Output for " + name + " will be written to: "+ out);
		FileWriter fstream = new FileWriter(out);
		BufferedWriter bw = new BufferedWriter(fstream);
		return bw;
	}
	
	private List<File> getMonitors(){
		String dir = Constants.getInstallDir() + Constants.MONITORS_DIR;
		log.debug("Collecting monitors from " + dir);
		File folder = new File(dir);
		List<File> files = Arrays.asList(folder.listFiles());
		if (files.isEmpty()){
			log.debug("No files found to execute under " + dir);
		}else{
			log.debug("Found monitors: " + files);
		}
		return files;		
	}
	
}
