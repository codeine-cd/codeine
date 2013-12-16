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
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ProcessUtil {

    private String pid;

    private PrintStream out;

    private PrintStream err;

    private String killCommand;

    private Utils utils;

    private File installDir;

    public ProcessUtil(String pid, PrintStream out, PrintStream err,
            File installDir) {
        this(pid, out, err, installDir, new Utils());
    }

    public ProcessUtil(String pid, PrintStream out, PrintStream err,
            File installDir, Utils utils) {
        this.installDir = installDir;
        this.pid = (pid != null) ? pid.trim() : "-1";
        this.out = out;
        this.err = err;
        this.utils = utils;
        if (utils.files().isWindows()) {
            this.killCommand = getWindowsKillFile().getPath();
        } else {
            this.killCommand = "kill";
        }
    }

    /* called from constructor */
    final File getWindowsKillFile() {
        File parent = new File(installDir, "c-mxj-utils");
        File kill = new File(parent, "kill.exe");
        if (!kill.exists()) {
            utils.streams().createFileFromResource("kill.exe", kill);
        }
        return kill;
    }

    String pid() {
        return pid;
    }

    public void kill() {
        kill(false);
    }

    public void forceKill() {
        Exceptions.VoidBlock block = new Exceptions.VoidBlock() {
            @Override
			public void inner() {
                kill(true);
            }
        };
        block.execNotThrowingExceptions(err);
    }

    /**
     * @param force
     */
    private void kill(boolean force) {
        String threadName = "killing process " + pid;
        if (force) {
            threadName = "force " + threadName;
        }
        launchShell(threadName, killArgs(force), 10);
    }

    String[] killArgs(boolean force) {
        List args = new ArrayList();
        args.add(killCommand);
        if (force) {
            args.add("-9");
        }
        args.add(pid);
        return utils.str().toStringArray(args);
    }

    public boolean isRunning() {
        String threadName = "is_process_" + pid + "_running";
        Shell shell = launchShell(threadName, isRunningArgs(), 5);
        if (!shell.hasReturned()) {
            return false;
        }
        return shell.returnCode() == 0;
    }

    private Shell launchShell(String threadName, String[] args, int seconds) {
        Shell shell = utils.shellFactory().newShell(args, threadName, out, err);
        shell.start();
        int fraction = 20;
        int loops = (fraction * seconds);
        do {
            utils.threads().pause((1000 / fraction));
        } while (!shell.hasReturned() && loops-- > 0);

        if (!shell.hasReturned()) {
            err.println("Thread \"" + threadName + "\" may be hung");
            err.println("(did not return after " + seconds + " seconds)");
            err.println("command line used: ");
            err.println(new ListToString("", " ", "").toString(args));
        }
        return shell;
    }

    String[] isRunningArgs() {
        return new String[] { killCommand, "-0", pid };
    }

    public void killNoThrow() {
        Exceptions.VoidBlock block = new Exceptions.VoidBlock() {
            @Override
			public void inner() {
                kill();
            }
        };
        block.execNotThrowingExceptions(err);
    }
}
