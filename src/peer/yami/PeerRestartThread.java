package yami;

import java.io.*;
import java.util.concurrent.*;

import org.apache.log4j.*;
import org.eclipse.jetty.server.*;

import yami.configuration.*;
import yami.model.*;

public class PeerRestartThread
{
	private static final Logger log = Logger.getLogger(PeerRestartThread.class);
	private Thread thread;
	private Server peerHTTPserver;
	private PrintWriter writer;
	public PeerRestartThread(Server peerHTTPserver, PrintWriter writer)
	{
		this.peerHTTPserver = peerHTTPserver;
		this.writer = writer;
		
		thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				restart();
			}
		});
		thread.start();
	}
	
	private void restart()
	{
		ConfigurationManager cm = ConfigurationManager.getInstance();
		try
		{
			GlobalConfiguration gc = cm.getConfFromFile(Constants.getConfPath()).conf; // if exception thrown, conf is
																						// bad // bad.
			log.info("going to shutdown HTTP server");
			peerHTTPserver.stop();
			log.info("HTTP server stopped successfully");
			Thread.sleep(TimeUnit.SECONDS.toMillis(5));
			// fix yshabi - hardcoded strings
			String[] cmd = {
					"/usr/bin/nohup", Constants.getInstallDir() + "/bin/startYamiClient.pl", gc.getJavaPath(), gc.getRsyncPath(), gc.clientport + "", gc.serverport + "", Constants.getInstallDir(), "yami.conf.xml", "itstl1078:/nfs/site/disks/iec_sws3/dist/workspace/misc/yami/"
			};
			log.info("restart command: " + cmd);
			Runtime.getRuntime().exec(cmd);
			System.exit(0);
		}
		catch (RuntimeException e)
		{
			writer.println("failed to read new configuration from file " + Constants.getConfPath());
			log.warn("failed to read new configuration from file " + Constants.getConfPath(), e);
		}
		catch (Exception e)
		{
			log.warn("failed to stop current http server", e);
		}
	}
	
}
