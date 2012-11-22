package yami;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

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
		// list files under local "../monitors" dir:
		List<File> files = getMonitors();
		for (File file : files)
		{
			// execute monitors and capture:
			if (file.isFile() && file.canExecute())
			{
				try
				{
					log.info("Will execute " + file.getAbsolutePath());
					Result res = ProcessExecuter.execute(file.getAbsolutePath());
					log.debug(file.getName() +" ended with res=" +res.success());
					FileWriter fstream = new FileWriter(Constants.getInstallDir() +"/nodes/"+ node.name +"/" +file.getName() + ".txt");
					BufferedWriter out = new BufferedWriter(fstream);
					out.write(res.success() ? "True\n" : "False\n");
					out.write(res.output);
					out.close();
				}
				catch (Exception e)
				{
					log.warn("got exception when executing monitor ", e);
				}
			}
		}
		
	}
	
	private List<File> getMonitors(){
		log.debug("Collecting monitors from " + Constants.getInstallDir() + "/monitors/");
		File folder = new File(Constants.getInstallDir() + "/monitors/");
		List<File> files = Arrays.asList(folder.listFiles());
		if (files.isEmpty()){
			log.debug("No files found to execute under "+ Constants.getInstallDir() + "/monitors/");
		}else{
			log.debug("Found monitors: " + files);
		}
		return files;		
	}
	
}
