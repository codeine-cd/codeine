package codeine.db.mysql;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


public class MXJController
{
	public static int MYSQL_KILL_DELAY = 60000;

	private static Logger log = Logger.getLogger(MXJController.class);
	
	public static int MAX_STARTUP_INTERVAL = 80 * 1000; 
	private final int RESTART_INTERVAL = 2 * 60 * 1000;

	public static String MAX_CONNECTIONS = "1000";
	public static String QUERY_CACHE_SIZE = "134217728"; //128M
	public static String MAX_ALLOWED_PACKET = "4M";
	public static String WAIT_TIMEOUT = "31536000";
	public static String KEY_BUFFER_SIZE = "67108864"; //64M
	public static String TABLE_OPEN_CACHE = "256";
	public static String SORT_BUFFER_SIZE = "4194304"; //4M
	public static String READ_BUFFER_SIZE = "1048576"; //1M
	public static String TMP_TABLE_SIZE 	 = "67108864"; //64M
	public static String MAX_HEAP_TABLE_SIZE = "67108864"; //64M
	public static String MAX_SEEKS_FOR_KEY = "1000";
	
	public static Boolean LOW_PRIORITY_UPDATES = false;
	public static boolean DUMP_QUERIES_LOG = false;
	
	private Map<String, String> m_mpAdditionalOptions = new HashMap<String, String>();
	private SpecificMysqldResource m_mysqld = null;
	private String m_sBasePath;
	private int m_iPort;
	private int m_iServerID;
	private boolean m_bEnableBinaryLog;
	private static long m_lastRestart = 0;
	private String binDir;
	
	public MXJController(String basePath, int port, String binDir)
	{
		m_sBasePath = basePath;
		m_iPort = port;
		this.binDir = binDir;
	}
	
	public synchronized boolean start()
	{
		if(m_mysqld != null)
		{
			log.debug("MySQL database already started");
			return false;
		}
		
		try
		{
			File baseDir = new File(getBaseDir());
			File dataDir = new File(getDataDir());
			dataDir.mkdirs();
			File outFile = new File(dataDir, "mysql.out");

			PrintStream mysqlOut = new PrintStream(new FileOutputStream(outFile));
			
	        m_mysqld = new SpecificMysqldResource(baseDir, dataDir, null, false, mysqlOut, binDir);
	        Map<String, String> options = buildOptions();
	        
	        if(m_mysqld.isRunning())
	        {
	        	log.info("Found old MySQL running database - terminating...");
	        	m_mysqld.setKillDelay(1);
	        	m_mysqld.shutdown();
	        	
	        }
	        
	        log.info("Starting MySQL on port " + m_iPort);
        	m_mysqld.setKillDelay(MAX_STARTUP_INTERVAL);
	        m_mysqld.start("MySQL Controller", options);
	        return true;
		}
		catch(Exception e)
		{
			log.error("cannot open database", e);
			m_mysqld = null;
			return false;
		}
	}

	public void addOptions(Map<String, String> mpAddtional)
	{
		m_mpAdditionalOptions.putAll(mpAddtional);
	}
	
	protected Map<String, String> buildOptions()
	{
		Map<String,String> options = new HashMap<String,String>();
		options.put("port",String.valueOf(m_iPort));
		options.put("max_seeks_for_key", MAX_SEEKS_FOR_KEY);
		options.put("max_allowed_packet", MAX_ALLOWED_PACKET);
		options.put("max_connections", MAX_CONNECTIONS);
		options.put("wait_timeout", WAIT_TIMEOUT);
		options.put("query_cache_size", QUERY_CACHE_SIZE);
		options.put("key_buffer_size", KEY_BUFFER_SIZE);
		options.put("table_open_cache", TABLE_OPEN_CACHE);
		options.put("sort_buffer_size", SORT_BUFFER_SIZE);
		options.put("read_buffer_size", READ_BUFFER_SIZE);
		options.put("innodb_buffer_pool_size", String.valueOf(512L * 1024 * 1024));
		options.put("tmp_table_size", TMP_TABLE_SIZE);
		options.put("max_heap_table_size", MAX_HEAP_TABLE_SIZE);
		
		if (LOW_PRIORITY_UPDATES)
		{
			options.put("low_priority_updates", "true");
		}
		
		options.putAll(m_mpAdditionalOptions);

		if(isEnableBinaryLog())
		{
			options.put("log-bin", "mysql-bin");
		}
		if(getServerID() > 0)
		{
			options.put("server-id", String.valueOf(getServerID()));
		}
//	        options.put("skip-innodb", null);
		options.put("user", MysqlConstants.DB_USER);
		if (DUMP_QUERIES_LOG)
		{
			options.put("log", System.getProperty("mysql.datadir") + File.separator + "queries.log");
		}
		return options;
	}
	

	public synchronized void stopMysqld()
	{
		if(m_mysqld != null)
		{
			System.out.println("Shutting down, please wait...");
			log.info("Shutting down MySql");
			m_mysqld.setKillDelay(MYSQL_KILL_DELAY);
			m_mysqld.shutdown();
		}
		m_mysqld = null;
	}
	
	public void stop()
	{
		try
		{
			stopMysqld();
			System.out.println("done");
		}
		catch(Throwable t)
		{
			log.warn("MXJShutdownThread.run() - caught exception", t);
		}
	}
	
	private synchronized void restartMysqld()
	{
		
		stopMysqld();
		delay(10);
		start();	
		delay(10);
	}

	/**
	 * TODO - nambar : Auto restart of mysql database causing issues
	 * it looks like the feeder starts the mysql database when not needed
	 * Auto restart was originally made since mysqld does not know to recover from NFS issues 
	 */
	public void restartMysqldIfNotRestartedLately()
	{
		if(m_lastRestart + RESTART_INTERVAL < System.currentTimeMillis())
		{
			restartMysqld();
			m_lastRestart = System.currentTimeMillis();
		}
	}
	
	private void delay(int sec)
	{
		try
		{
			Thread.sleep(sec * 1000);
		}
		catch (InterruptedException e)
		{
			log.error("delay() - interrupted", e);
		}
	}

	public boolean isEnableBinaryLog()
	{
		return m_bEnableBinaryLog;
	}

	public void setEnableBinaryLog(boolean enableBinaryLog)
	{
		m_bEnableBinaryLog = enableBinaryLog;
	}

	public int getServerID()
	{
		return m_iServerID;
	}

	public void setServerID(int serverID)
	{
		m_iServerID = serverID;
	}

	public int getPort()
	{
		return m_iPort;
	}

	public void setPort(int port)
	{
		m_iPort = port;
	}
	
	public String getDataDir()
	{
		return m_sBasePath + File.separator + "data";
	}

	public String getBaseDir()
	{
		return m_sBasePath + File.separator + "mysql";
	}
	
	public void cleanReplicationLogs()
	{
		try
		{
			Runtime.getRuntime().exec(new String[] {"/bin/sh", "-c", "/bin/rm " + getDataDir() + "/*-relay-*"});
		}
		catch (IOException e)
		{
			log.warn("unable to cleanup replication logs", e);
		}
	}
	
	public boolean isRunning()
	{
		return m_mysqld.isRunning();
	}

}
