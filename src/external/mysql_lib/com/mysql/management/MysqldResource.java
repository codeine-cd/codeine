/*
 Copyright (C) 2004 MySQL AB

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License version 2 as 
 published by the Free Software Foundation.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 */
package com.mysql.management;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.mysql.jdbc.Driver;
import com.mysql.jdbc.MysqlErrorNumbers;
import com.mysql.management.util.ListToString;
import com.mysql.management.util.Platform;
import com.mysql.management.util.ProcessUtil;
import com.mysql.management.util.Shell;
import com.mysql.management.util.Streams;
import com.mysql.management.util.Utils;

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MysqldResource implements MysqldResourceI {
    private static final Logger log = Logger.getLogger(MysqldResource.class);

	public static final String MYSQL_C_MXJ = "mysql-c.mxj";

    public static final String DATA = "data";

    protected String versionString;

    private Map options;

    private Shell shell;

    protected final File baseDir;

    protected final File dataDir;

    private final File pidFile;

    private final File portFile;

    private String msgPrefix;

    private String pid;

    private String osName;

    private String osArch;

    protected PrintStream out;

    protected PrintStream err;

    private Exception trace;

    private int killDelay;

    private List completionListensers;

    private boolean readyForConnections;

    // collaborators
    private HelpOptionsParser optionParser;

    protected Utils utils;

	private String binDir;

//    public MysqldResource() {
//        this(new Files().nullFile());
//    }
//
//    public MysqldResource(File baseDir) {
//        this(baseDir, new Files().nullFile());
//    }
//
//    public MysqldResource(File baseDir, File dataDir) {
//        this(baseDir, dataDir, null);
//    }
//
//    public MysqldResource(File baseDir, File dataDir, String mysqlVersionString) {
//        this(baseDir, dataDir, mysqlVersionString, true);
//    }
//
//    public MysqldResource(File baseDir, File dataDir,
//            String mysqlVersionString, boolean guessArch) {
//        this(baseDir, dataDir, mysqlVersionString, guessArch, System.out,
//                System.err);
//    }
//
//    public MysqldResource(File baseDir, File dataDir,
//            String mysqlVersionString, boolean guessArch, PrintStream out,
//            PrintStream err) {
//        this(baseDir, dataDir, mysqlVersionString, guessArch, out, err,
//                new Utils());
//    }
//
//    public MysqldResource(File baseDir, File dataDir, String mysqlVersionString,
//            boolean guessArch, PrintStream out, PrintStream err, Utils util) {
//    }

    public MysqldResource(File baseDir, File dataDir, String mysqlVersionString,
            boolean guessArch, PrintStream out, PrintStream err, Utils util, String binDir) {
    	this.out = out;
    	this.err = err;
    	this.utils = util;
		this.binDir = binDir;
    	this.optionParser = new HelpOptionsParser(err, utils);
    	this.killDelay = 30000;
    	this.baseDir = utils.files().validCononicalDir(baseDir,
    			utils.files().tmp(MYSQL_C_MXJ));
    	this.dataDir = utils.files().validCononicalDir(dataDir,
    			new File(this.baseDir, DATA));
    	String className = utils.str().shortClassName(getClass());
    	this.pidFile = utils.files().cononical(
    			new File(this.dataDir, className + ".pid"));
    	this.portFile = new File(dataDir, "port");
    	setVersion(false, mysqlVersionString);
    	this.msgPrefix = "[" + utils.str().shortClassName(getClass()) + "] ";
    	this.options = new HashMap();
    	this.setShell(null);
    	setOsAndArch(System.getProperty(Platform.OS_NAME), guessArch, System
    			.getProperty(Platform.OS_ARCH));
    	this.completionListensers = new ArrayList();
    	initTrace();
	}

    protected String binDir() {
		return binDir;
	}
    
	private void initTrace() {
        this.trace = new Exception();
    }

    /**
     * Starts mysqld passing it the parameters specified in the arguments map.
     * No effect if MySQL is already running
     */
    @Override
	public synchronized void start(String threadName, Map mysqldArgs) {
        start(threadName, mysqldArgs, false);
    }

    @Override
	public synchronized void start(String threadName, Map mysqldArgs,
            boolean populateAllOptions) {
        if ((getShell() != null) || processRunning()) {
            printMessage("mysqld already running (process: " + pid() + ")");
            return;
        }

        mysqldArgs = new HashMap(mysqldArgs);

        int port = 3306;
        Object portArg = mysqldArgs.get(MysqldResourceI.PORT);
        if (portArg != null) {
            port = Integer.parseInt(portArg.toString());
        }
        String portStr = "" + port;
        mysqldArgs.put(MysqldResourceI.PORT, portStr);
        mysqldArgs.remove(MysqldResourceI.MYSQLD_VERSION);
        mysqldArgs.remove(MysqldResourceI.USE_DEFAULT_ARCHITECTURE);

        if (populateAllOptions) {
            options = optionParser.getOptionsFromHelp(getHelp(mysqldArgs));
        } else {
            options = new HashMap();
            options.putAll(mysqldArgs);
        }

        // printMessage("mysqld : " +
        // services.str().toString(mysqldArgs.entrySet()));
        out.flush();
        addCompletionListenser(new Runnable() {
            @Override
			public void run() {
                setReadyForConnection(false);
                setShell(null);
                completionListensers.remove(this);
            }
        });
        setShell(exec(threadName, mysqldArgs, out, err, true));

        reportPid();
        utils.files().writeString(portFile, portStr);

        boolean ready = canConnectToServer(port, killDelay);
        setReadyForConnection(ready);
    }

    // Will wait 250 miliseconds between each try.
    boolean canConnectToServer(int port, int milisecondsBeforeGivingUp) {
        int triesBeforeGivingUp = 1 + (milisecondsBeforeGivingUp / 1000) * 4;
        utils.str().classForName(Driver.class.getName());
        Connection conn = null;
        String bogusUser = "Connector/MXJ";
        String password = "Bogus Password";
        String url = "jdbc:mysql://localhost:"
                + port
                + "/test"
                + "?connectTimeout=150"
                + "&socketFactory=com.mysql.management.util.PatchedStandardSocketFactory";

        for (int i = 0; i < triesBeforeGivingUp; i++) {
            try {
                conn = DriverManager.getConnection(url, bogusUser, password);
                return true; /* should never happen */
            } catch (SQLException e) {
                if (e.getErrorCode() == MysqlErrorNumbers.ER_ACCESS_DENIED_ERROR) {
                    return true;
                }
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            utils.threads().pause(100);
        }
        return false;
    }

    private void setReadyForConnection(boolean ready) {
        readyForConnections = ready;
    }

    @Override
	public synchronized boolean isReadyForConnections() {
        return readyForConnections;
    }

    private void reportPid() {
        boolean printed = false;
        for (int i = 0; !printed && i < 50; i++) {
            if (pidFile.exists() && pidFile.length() > 0) {
                utils.threads().pause(25);
                printMessage("mysqld running as process: " + pid());

                out.flush();
                printed = true;
            }
            utils.threads().pause(100);
        }

        reportIfNoPidfile(printed);
    }

    synchronized String pid() {
        if (pid == null) {
            if (!pidFile.exists()) {
                return "No PID";
            }

            pid = utils.files().asString(pidFile).trim();
        }
        return pid;
    }

    void reportIfNoPidfile(boolean pidFileFound) {
        if (!pidFileFound) {
            printWarning("mysqld pid-file not found:  " + pidFile);
        }
    }

    /**
     * Kills the MySQL process.
     */
    @Override
	public synchronized void shutdown() {
        boolean haveShell = (getShell() != null);
        if (!pidFile.exists() && !haveShell) {
            printMessage("Mysqld not running. No file: " + pidFile);
            return;
        }
        printMessage("stopping mysqld (process: " + pid() + ")");

        issueNormalKill();

        if (processRunning()) {
            issueForceKill();
        }

        if (shellRunning()) {
            destroyShell();
        }
        setShell(null);

        if (processRunning()) {
            printWarning("Process " + pid + "still running; not deleting "
                    + pidFile);
        } else {
            utils.threads().pause(150);
            System.gc();
            utils.threads().pause(150);
            pidFile.deleteOnExit();
            pidFile.delete();
            pid = null;
        }

        setReadyForConnection(false);

        printMessage("clearing options");
        options.clear();
        out.flush();

        printMessage("shutdown complete");
    }

    void destroyShell() {
        String shellName = getShell().getName();
        printWarning("attempting to destroy thread " + shellName);
        getShell().destroyProcess();
        waitForShellToDie();
        String msg = (shellRunning() ? "not " : "") + "destroyed.";
        printWarning(shellName + " " + msg);
    }

    void issueForceKill() {
        printWarning("attempting to \"force kill\" " + pid());
        new ProcessUtil(pid(), err, err, baseDir, utils).forceKill();

        waitForProcessToDie();
        if (processRunning()) {
            String msg = (processRunning() ? "not " : "") + "killed.";
            printWarning(pid() + " " + msg);
        } else {
            printMessage("force kill " + pid() + " issued.");
        }
    }

    private void issueNormalKill() {
        if (!pidFile.exists()) {
            printWarning("Not running? File not found: " + pidFile);
            return;
        }

        new ProcessUtil(pid(), err, err, baseDir, utils).killNoThrow();
        waitForProcessToDie();
    }

    private void waitForProcessToDie() {
        long giveUp = System.currentTimeMillis() + killDelay;
        while (processRunning() && System.currentTimeMillis() < giveUp) {
            utils.threads().pause(250);
        }
    }

    private void waitForShellToDie() {
        long giveUp = System.currentTimeMillis() + killDelay;
        while (shellRunning() && System.currentTimeMillis() < giveUp) {
            utils.threads().pause(250);
        }
    }

    @Override
	public synchronized Map getServerOptions() {
        if (options.isEmpty()) {
            options = optionParser.getOptionsFromHelp(getHelp(new HashMap()));
        }
        return new HashMap(options);
    }

    @Override
	public synchronized boolean isRunning() {
        return shellRunning() || processRunning();
    }

    private boolean processRunning() {
        if (!pidFile.exists()) {
            return false;
        }
        return new ProcessUtil(pid(), out, err, baseDir, utils).isRunning();
    }

    private boolean shellRunning() {
        return (getShell() != null) && (getShell().isAlive());
    }

    @Override
	public synchronized String getVersion() {
        return versionString;
    }

    private String getVersionDir() {
        return getVersion().replaceAll("\\.", "-");
    }

    protected synchronized void setVersion(boolean checkRunning,
            String mysqlVersionString) {
        if (checkRunning && isRunning()) {
            throw new IllegalStateException("Already running");
        }

        if (mysqlVersionString == null || mysqlVersionString.equals("")) {
            versionString = utils.streams()
                    .getSystemPropertyWithDefaultFromResource(MYSQLD_VERSION,
                            "connector-mxj.properties", err);
        } else {
            versionString = mysqlVersionString;
        }
        versionString.trim();
    }

    @Override
	public synchronized void setVersion(String mysqlVersionString) {
        setVersion(true, mysqlVersionString);
    }

    private void printMessage(String msg) {
        println(out, msg);
        log.info(msg);
    }

    private void printWarning(String msg) {
        println(err, "");
        println(err, msg);
        log.warn(msg);
    }

    private void println(PrintStream stream, String msg) {
        stream.println(msgPrefix + msg);
    }

    /* called from constructor, over-ride with care */
    final void setOsAndArch(String osName, boolean defaultArch, String osArch) {
        /*
         * FIXME: Remove use of defaultArch and "Win" shortcuts.
         * 
         * PROBLEM: If on Linux-ppc, we shouldn't even try Linux-i386.
         * 
         * SOLUTION: Replace current code with a resource table of os-arch
         * combinations and only remap if an entry exists in the table. Consider
         * wild-card matching. This resource table should be in a text editable
         * file, not a class file.
         * 
         * SOLUTION DOWN-SIDES: Long-term, we may wish to provide a way to get
         * more information than simply os name and architecture. For instance,
         * "SunOS 8 or SunOS 10" or maybe "sparc 32 or sparc 64". At that time
         * we may wish to provide a real interface with user-plugablity, but
         * there is no immediate demand.
         */
        String name = osName;
        if (osName.indexOf("Win") != -1) {
            name = "Win";
            osArch = defaultArch ? "x86" : stripUnwantedChars(osArch);
        } else if (osName.indexOf("Linux") != -1) {
            osArch = defaultArch ? "i386" : stripUnwantedChars(osArch);
        }
        this.osName = stripUnwantedChars(name);
        this.osArch = stripUnwantedChars(osArch);
    }

    String stripUnwantedChars(String str) {
        return str.replace(' ', '_').replace('/', '_').replace('\\', '_');
    }

    private Shell exec(String threadName, Map mysqldArgs,
            PrintStream outStream, PrintStream errStream, boolean withListeners) {

        makeMysqld();
        ensureEssentialFilesExist();

        adjustParameterMap(mysqldArgs);
        String[] args = constructArgs(mysqldArgs);
        outStream.println(new ListToString().toString(args));
        log.info("starting mysqld: " + new ListToString().toString(args));
        Shell launch = utils.shellFactory().newShell(args, threadName,
                outStream, errStream);
        if (withListeners) {
            for (int i = 0; i < completionListensers.size(); i++) {
                Runnable listener = (Runnable) completionListensers.get(i);
                launch.addCompletionListener(listener);
            }
        }
        launch.setDaemon(true);

        printMessage("launching mysqld (" + threadName + ")");

        launch.start();
        return launch;
    }

    private void adjustParameterMap(Map mysqldArgs) {
        ensureDir(mysqldArgs, baseDir, MysqldResourceI.BASEDIR);
        ensureDir(mysqldArgs, dataDir, MysqldResourceI.DATADIR);
        mysqldArgs.put(MysqldResourceI.PID_FILE, pidFile.getPath());
        ensureSocket(mysqldArgs);
    }

    protected File makeMysqld() {
        final File mysqld = getMysqldFilePointer();
        if (!mysqld.exists()) {
            mysqld.getParentFile().mkdirs();
            utils.streams().createFileFromResource(getResourceName(), mysqld);
        }
        utils.files().addExecutableRights(mysqld, out, err);
        return mysqld;
    }

    String getResourceName() {
        String dir = os_arch();
        String name = executableName();
        return getVersionDir() + Streams.RESOURCE_SEPARATOR + dir
                + Streams.RESOURCE_SEPARATOR + name;
    }

    String os_arch() {
        return osName + "-" + osArch;
    }

    private String executableName()
    {
        return "mysqld";
    }

    protected boolean isWindows()
    {
        return osName.equals("Win");
    }

    protected File getMysqldFilePointer() {
        File path = new File(binDir);
        return new File(path, executableName());
    }

    public void ensureEssentialFilesExist() {
        utils.streams().expandResourceJar(dataDir,
                getVersionDir() + Streams.RESOURCE_SEPARATOR + "data_dir.jar");
        utils.streams().expandResourceJar(baseDir,
                getVersionDir() + Streams.RESOURCE_SEPARATOR + shareJar());
    }

    void ensureSocket(Map mysqldArgs) {
        String socketString = (String) mysqldArgs.get(MysqldResourceI.SOCKET);
        if (socketString != null) {
            return;
        }
        mysqldArgs.put(MysqldResourceI.SOCKET, "mysql.sock");
    }

    private void ensureDir(Map mysqldArgs, File expected, String key) {
        String dirString = (String) mysqldArgs.get(key);
        if (dirString != null) {
            File asConnonical = utils.files().validCononicalDir(
                    new File(dirString));
            if (!expected.equals(asConnonical)) {
                String msg = dirString + " not equal to " + expected;
                throw new IllegalArgumentException(msg);
            }
        }
        mysqldArgs.put(key, utils.files().getPath(expected));
    }

    protected String[] constructArgs(Map mysqldArgs) {
        List strs = new ArrayList();
        strs.add(utils.files().getPath(getMysqldFilePointer()));

        strs.add("--no-defaults");
        if (isWindows()) {
            strs.add("--console");
        }
        Iterator it = mysqldArgs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            StringBuffer buf = new StringBuffer("--");
            buf.append(key);
            if (value != null) {
                buf.append("=");
                buf.append(value);
            }
            strs.add(buf.toString());
        }

        return utils.str().toStringArray(strs);
    }

    @Override
	protected void finalize() throws Throwable {
        if (getShell() != null) {
            printWarning("resource released without closure.");
            trace.printStackTrace(err);
        }
        super.finalize();
    }

    String shareJar() {
        String shareJar = "share_dir.jar";
        if (isWindows()) {
            shareJar = "win_" + shareJar;
        }
        return shareJar;
    }

    void setShell(Shell shell) {
        this.shell = shell;
    }

    Shell getShell() {
        return shell;
    }

    @Override
	public File getBaseDir() {
        return baseDir;
    }

    @Override
	public File getDataDir() {
        return dataDir;
    }

    @Override
	public synchronized void setKillDelay(int millis) {
        this.killDelay = millis;
    }

    @Override
	public synchronized void addCompletionListenser(Runnable listener) {
        completionListensers.add(listener);
    }

    private String getHelp(Map params) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream capturedOut = new PrintStream(bos);

        params.put("help", null);
        params.put("verbose", null);

        exec("getOptions", params, capturedOut, capturedOut, false).join();

        params.remove("help");
        params.remove("verbose");

        utils.threads().pause(500);
        capturedOut.flush();
        capturedOut.close(); // should flush();

        return new String(bos.toByteArray());
    }

    @Override
	public synchronized int getPort() {
        if (isRunning()) {
            String portStr = utils.files().asString(portFile).trim();
            return Integer.parseInt(portStr);
        }
        return 0;
    }

    // ---------------------------------------------------------
    static void printUsage(PrintStream out) {
        String command = "java " + MysqldResource.class.getName();
        String basedir = " --" + MysqldResourceI.BASEDIR;
        String datadir = " --" + MysqldResourceI.DATADIR;
        out.println("Usage to start: ");
        out.println(command + " [ server options ]");
        out.println();
        out.println("Usage to shutdown: ");
        out.println(command + " --shutdown [" + basedir
                + "=/full/path/to/basedir ]");
        out.println();
        out.println("Common server options include:");
        out.println(basedir + "=/full/path/to/basedir");
        out.println(datadir + "=/full/path/to/datadir");
        out.println(" --" + MysqldResourceI.SOCKET
                + "=/full/path/to/socketfile");
        out.println();
        out.println("Example:");
        out.println(command + basedir + "=/home/duke/dukeapp/db" + datadir
                + "=/data/dukeapp/data" + " --max_allowed_packet=65000000");
        out.println(command + " --shutdown" + basedir
                + "=/home/duke/dukeapp/db");
        out.println();
    }

   
}
