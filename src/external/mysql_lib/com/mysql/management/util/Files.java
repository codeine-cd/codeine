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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: Files.java,v 1.2 2007-04-22 09:57:53 nambar Exp $
 */
public class Files {

    public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

    public static final String USE_TEST_DIR = "c-mxj.files.use-test-dir";

    private Shell.Factory shellFactory;

    private char separatorChar;

    private Streams streams;

    public Files() {
        this(new Shell.Factory(), File.separatorChar, new Streams());
    }

    Files(Shell.Factory shellFactory, char separatorChar, Streams streams) {
        this.shellFactory = shellFactory;
        this.separatorChar = separatorChar;
        this.streams = streams;
    }

    public File testDir() {
        return new File(tmp(), "test-c.mxj");
    }

    public File tmp() {
        return cononical(new File(System.getProperty(JAVA_IO_TMPDIR)));
    }

    public File tmp(String subdir) {
        String useTestDir = System.getProperty(USE_TEST_DIR);
        if (Boolean.TRUE.toString().equalsIgnoreCase(useTestDir)) {
            return new File(testDir(), subdir);
        }
        return new File(tmp(), subdir);
    }

    /**
     * Depth First traversal of the directory. Attempts to delete every file in
     * the structure.
     * 
     * @return true if the file passed in is successfully deleted
     */
    public boolean deleteTree(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteTree(files[i]);
            }
        }
        return file.delete();
    }

    /* TODO - ygez  make this more platform independant */
    /**
     * On UNIX systems, in order for a file to be executable, it needs to have
     * the execute bit set. This method executes a "chmod +x filename"
     */
    public void addExecutableRights(File executable, PrintStream out,
            PrintStream err) {
        if (isWindows()) {
            return;
        }
        String[] args = { "/bin/chmod", "+x", executable.getPath() };
        String tName = "make " + executable + " runable";
        shellFactory.newShell(args, tName, out, err).run();
    }

    public boolean isWindows() {
        return separatorChar == '\\';
    }

    public String asString(final File file) {
        return new Exceptions.StringBlock() {
            @Override
			public String inner() throws IOException {
                FileInputStream fis = new FileInputStream(file);
                try {
                    return streams.readString(fis);
                } finally {
                    fis.close();
                }
            }
        }.exec();
    }

    public void writeString(final File file, final String str) {
        new Exceptions.VoidBlock() {
            @Override
			public void inner() throws IOException {
                FileWriter fw = null;
                PrintWriter pw = null;
                try {
                    fw = new FileWriter(file);
                    pw = new PrintWriter(fw);
                    pw.print(str);
                    pw.flush();
                } finally {
                    try {
                        if (pw != null) {
                            pw.close();
                        }
                    } finally {
                        if (fw != null) {
                            fw.close();
                        }
                    }
                }
            }
        }.exec();
    }

    public File nullFile() {
        return new File("");
    }

    public File newFile(Object fileName) {
        if (fileName == null) {
            return nullFile();
        }
        return cononical(new File(fileName.toString()));
    }

    public File cononical(final File file) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }

        return (File) new Exceptions.Block() {
            @Override
			protected Object inner() throws Exception {
                return file.getCanonicalFile();
            }
        }.exec();
    }

    public String getPath(final File file) {
        return (String) new Exceptions.Block() {
            @Override
			protected Object inner() throws Exception {
                return file.getCanonicalPath();
            }
        }.exec();
    }

    public boolean cleanTestDir() {
        return deleteTree(testDir());
    }

    public File validCononicalDir(File dir, File defaultDir) {
        if (dir == null || dir.equals(nullFile())) {
            dir = defaultDir;
        }
        return validCononicalDir(dir);
    }

    public File validCononicalDir(final File dir) {
        File cononical = cononical(dir);

        if (!cononical.exists()) {
            cononical.mkdirs();
        }
        if (!cononical.isDirectory()) {
            throw new IllegalArgumentException(cononical + " not a directory");
        }
        return cononical;
    }
}
