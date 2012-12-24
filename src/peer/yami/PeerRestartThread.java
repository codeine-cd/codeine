package yami;

import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;

import yami.configuration.ConfigurationManager;
import yami.configuration.GlobalConfiguration;
import yami.model.Constants;

import com.google.common.base.Joiner;

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
		
		try
		{
			log.info("going to shutdown HTTP server");
			peerHTTPserver.stop();
			log.info("HTTP server stopped successfully");
			Thread.sleep(TimeUnit.SECONDS.toMillis(5));
			String[] cmd = createRestartCmd();
			log.info("restart command: (" + Joiner.on(' ').join(cmd) + ")");
			Runtime.getRuntime().exec(cmd);
			System.exit(0);
		}
		
		catch (NullPointerException e)
		{
			writer.println("null at restart");
			log.warn("null pointer exception in restart",e);
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
	
	private String[] createRestartCmd()
	{
		// TODO: yshabi - hardcoded strings
		ConfigurationManager cm = ConfigurationManager.getInstance();
		GlobalConfiguration gc = cm.getConfFromFile(Constants.getConfPath()).conf;
		String[] cmd = {
				"/usr/bin/nohup", Constants.getInstallDir() + "/bin/startYamiClient.pl", gc.getJavaPath(), gc.getRsyncPath(), gc.getRsyncUser(), gc.getClientPort() + "", gc.getServerPort() + "", Constants.getInstallDir(), "yami.conf.xml", gc.getRsyncSource()
		};
		return cmd;
	}
}
