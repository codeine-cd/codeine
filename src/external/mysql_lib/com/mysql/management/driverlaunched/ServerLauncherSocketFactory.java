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
package com.mysql.management.driverlaunched;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.mysql.jdbc.SocketFactory;
import com.mysql.jdbc.StandardSocketFactory;
import com.mysql.management.MysqldFactory;
import com.mysql.management.MysqldResourceI;
import com.mysql.management.util.Files;

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ServerLauncherSocketFactory implements SocketFactory {

    public static final String SERVER_DOT = "server.";

    private static int launchCount = 0;

    private MysqldFactory resourceFactory;

    private SocketFactory socketFactory;

    public ServerLauncherSocketFactory() {
        setResourceFactory(new MysqldFactory.Default());
        setSocketFactory(new StandardSocketFactory());
    }

    @Override
	public Socket connect(String host, int portNumber, Properties props)
            throws SocketException, IOException {
        ensureMysqlStarted(portNumber, props);

        return getSocketFactory().connect(host, portNumber, props);
    }

    private void ensureMysqlStarted(int port, Properties props) {
        Map serverOpts = new HashMap();
        for (Enumeration enums = props.propertyNames(); enums.hasMoreElements();) {
            String key = enums.nextElement().toString();
            if (key.startsWith(SERVER_DOT)) {
                String val = replaceNullStringWithNull(props.getProperty(key));
                serverOpts.put(key.substring(SERVER_DOT.length()), val);
            }
        }
        serverOpts.put(MysqldResourceI.PORT, Integer.toString(port));
        Object baseDirStr = serverOpts.get(MysqldResourceI.BASEDIR);
        File baseDir = new Files().newFile(baseDirStr);

        String dataDirString = (String) serverOpts.get(MysqldResourceI.DATADIR);

        File dataDir = null;
        if (dataDirString != null) {
            File ddir = new File(dataDirString);
            dataDir = new Files().validCononicalDir(ddir);
        }

        boolean guessArch = true;
        String guessArchStr = (String) serverOpts
                .get(MysqldResourceI.USE_DEFAULT_ARCHITECTURE);
        if (guessArchStr != null) {
            guessArch = Boolean.valueOf(guessArchStr).booleanValue();
        }

        String mysqldVersion = (String) serverOpts
                .get(MysqldResourceI.MYSQLD_VERSION);

        MysqldResourceI mysqld = resourceFactory.newMysqldResource(baseDir,
                dataDir, mysqldVersion, guessArch);

        if (mysqld.isRunning()) {
            int runningPort = mysqld.getPort();
            if (port != runningPort) {
                String location = mysqld.getBaseDir().getPath();
                if (dataDir != null) {
                    location += " with data at " + dataDir;
                }
                String msg = "Mysqld at " + location + " is running on port "
                        + runningPort + " not " + port;
                throw new RuntimeException(msg);
            }
            return;
        }

        launchCount++;
        String threadName = "driver_launched_mysqld_" + launchCount;
        mysqld.start(threadName, serverOpts);
    }

    String replaceNullStringWithNull(String str) {
        return String.valueOf((Object) null).equals(str) ? null : str;
    }

    @Override
	public Socket afterHandshake() throws SocketException, IOException {
        return getSocketFactory().afterHandshake();
    }

    @Override
	public Socket beforeHandshake() throws SocketException, IOException {
        return getSocketFactory().beforeHandshake();
    }

    void setResourceFactory(MysqldFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    MysqldFactory getResourceFactory() {
        return resourceFactory;
    }

    void setSocketFactory(SocketFactory socketFactory) {
        this.socketFactory = socketFactory;
    }

    SocketFactory getSocketFactory() {
        return socketFactory;
    }

    // -------------------------------------------------------------
    public synchronized static boolean shutdown(File baseDir, File dataDir) {
    	throw new UnsupportedOperationException();
//        MysqldResource mysqld = new MysqldResource(baseDir, dataDir);
//        mysqld.shutdown();
//        return mysqld.isRunning();
    }
}
