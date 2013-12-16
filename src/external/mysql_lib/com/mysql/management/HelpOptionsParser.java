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

import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mysql.management.util.Utils;

/*
 * Replace option parsing with pre-parsed options table read from resources.
 */

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: HelpOptionsParser.java,v 1.2 2007-04-22 09:57:53 nambar Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
final class HelpOptionsParser {

    private static final String DIVIDER = "--------------------------------- -----------------------------";

    private static final String END_TEXT = "To see what values a running";

    private static final String NO_DEFAULT_VALUE = "(No default value)";

    private PrintStream err;

    private Utils utils;

    HelpOptionsParser(PrintStream err, Utils utils) {
        this.err = err;
        this.utils = utils;
    }

    public Map getOptionsFromHelp(String help) {
        String trimmed = trimToOptions(help);

        Map map = new LinkedHashMap();
        String[] lines = utils.str().splitLines(trimmed);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.indexOf(' ') <= 0) {
                continue;
            }
            String key = line.substring(0, line.indexOf(' '));
            String val = line.substring(key.length()).trim();
            if (val.equals(NO_DEFAULT_VALUE)) {
                val = "";
            }
            map.put(key, val);
        }

        map.remove("help");
        map.remove("verbose");

        return map;
    }

    String trimToOptions(String help) {
        boolean success = false;
        try {
            String trimmedHelp = trimToOptionsInner(help);
            success = true;
            return trimmedHelp;
        } finally {
            if (!success) {
                synchronized (err) {
                    printMsg(err, "parsing unseccessful:");
                    printMsg(err, "===== BEGIN MYSQLD HELP OPTIONS TEXT =====");
                    err.println(help);
                    printMsg(err, "===== END MYSQLD HELP OPTIONS TEXT =====");
                }
            }
        }
    }

    private String trimToOptionsInner(String help) {
        int dividerPos = help.indexOf(DIVIDER);
        int start = dividerPos + DIVIDER.length();
        int stop = help.indexOf(END_TEXT);
        if (dividerPos == -1) {
            synchronized (err) {
                printMsg(err, "Divider=\"" + DIVIDER + "\"");
                printMsg(err, "found at: " + dividerPos);
                printMsg(err, "Start Position:" + start);
                printMsg(err, "End Text=\"" + END_TEXT + "\"");
                printMsg(err, "found at: " + stop);
                printMsg(err, "HELP TEXT BEGIN");
                printMsg(err, help);
                printMsg(err, "HELP TEXT END");
            }
            throw new RuntimeException("could not parse help text");
        }
        if (stop < start) {
            stop = help.length();
        }
        String options = help.substring(start, stop);
        return options + System.getProperty("line.separator");
    }

    private void printMsg(PrintStream ps, String msg) {
        synchronized (ps) {
            ps.print("[");
            ps.print(utils.str().shortClassName(this));
            ps.print("] ");
            ps.println(msg);
        }
    }
}
