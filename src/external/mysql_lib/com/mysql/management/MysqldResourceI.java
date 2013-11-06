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

import java.io.File;
import java.util.Map;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: MysqldResourceI.java,v 1.2 2007-04-22 09:57:53 nambar Exp $
 */
@SuppressWarnings({ "rawtypes"})
public interface MysqldResourceI {
    public static final String PORT = "port";

    public static final String PID_FILE = "pid-file";

    public static final String BASEDIR = "basedir";

    public static final String DATADIR = "datadir";

    public static final String SOCKET = "socket";

    public static final String MYSQLD_VERSION = "mysql-version";

    public static final String USE_DEFAULT_ARCHITECTURE = "use-default-architecture";

    void setVersion(String version);

    String getVersion();

    void start(String threadName, Map mysqldArgs);

    void start(String threadName, Map mysqldArgs, boolean populateAllOptions);

    void shutdown();

    Map getServerOptions();

    boolean isRunning();

    boolean isReadyForConnections();

    void setKillDelay(int millis);

    void addCompletionListenser(Runnable listener);

    File getBaseDir();

    File getDataDir();

    int getPort();
}
