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
package com.mysql.management.util;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mysql.management.MysqldResourceI;

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: CommandLineOptionsParser.java,v 1.1 2005/02/17 21:20:45 eherman
 *          Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class CommandLineOptionsParser {

    private Map params;

    private Files fileUtil;

    public CommandLineOptionsParser(String[] args) {
        this(Arrays.asList(args));
    }

    public CommandLineOptionsParser(List args) {
        this.params = new HashMap();
        this.fileUtil = new Files();

        for (int i = 0; i < args.size(); i++) {
            String arg = (String) args.get(i);
            if (arg.startsWith("--")) {
                arg = arg.substring(2);
            }
            int equalsPos = arg.indexOf("=");
            if (equalsPos == -1) {
                equalsPos = arg.length();
            }
            String key = arg.substring(0, equalsPos).trim();
            String value = null;
            if (arg.length() > equalsPos) {
                value = arg.substring(equalsPos + 1, arg.length()).trim();
            }

            params.put(key, value);
        }
    }

    public boolean containsKey(String key) {
        return asMap().containsKey(key);
    }

    public Object remove(String key) {
        return asMap().remove(key);
    }

    public Map asMap() {
        return params;
    }

    public File getBaseDir() {
        return newFile(MysqldResourceI.BASEDIR);
    }

    public File getDataDir() {
        return newFile(MysqldResourceI.DATADIR);
    }

    private File newFile(String key) {
        if (!params.containsKey(key))
            return fileUtil.nullFile();
        return fileUtil.newFile(params.get(key));
    }

    public String getVersion() {
        return (String) params.get(MysqldResourceI.MYSQLD_VERSION);
    }

    public boolean isShutdown() {
        return containsKey("shutdown");
    }

    public boolean defaultArchitecture() {
        String guessArchStr = (String) params
                .get(MysqldResourceI.USE_DEFAULT_ARCHITECTURE);
        if (guessArchStr == null) {
            return true;
        }
        return Boolean.valueOf(guessArchStr).booleanValue();
    }
}
