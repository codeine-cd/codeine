package com.mysql.management.util;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public interface RuntimeI {

    /**
     * @see java.lang.Runtime#addShutdownHook(Thread hook) {
     */
    void addShutdownHook(Thread hook);

    /**
     * @see java.lang.Runtime#availableProcessors()
     */
    int availableProcessors();

    /**
     * @see java.lang.Runtime#exec(java.lang.String, java.lang.String[],
     *      java.io.File)
     */
    Process exec(String command, String[] envp, File dir);

    /**
     * @see java.lang.Runtime#exec(java.lang.String, java.lang.String[])
     */
    Process exec(String cmd, String[] envp);

    /**
     * @see java.lang.Runtime#exec(java.lang.String)
     */
    Process exec(String command);

    /**
     * @see java.lang.Runtime#exec(java.lang.String[], java.lang.String[],
     *      java.io.File)
     */
    Process exec(String[] cmdarray, String[] envp, File dir);

    /**
     * @see java.lang.Runtime#exec(java.lang.String[], java.lang.String[])
     */
    Process exec(String[] cmdarray, String[] envp);

    /**
     * @see java.lang.Runtime#exec(java.lang.String[])
     */
    Process exec(String[] cmdarray);

    /**
     * @see java.lang.Runtime#exit(int)
     */
    void exit(int status);

    /**
     * @see java.lang.Runtime#freeMemory()
     */
    long freeMemory();

    /**
     * @see java.lang.Runtime#gc()
     */
    void gc();

    /**
     * @see java.lang.Runtime#getLocalizedInputStream(java.io.InputStream)
     * @deprecated As of JDK&nbsp;1.1, the preferred way to translate a byte
     *             stream in the local encoding into a character stream in
     *             Unicode is via the <code>InputStreamReader</code> and
     *             <code>BufferedReader</code> classes.
     */
    InputStream getLocalizedInputStream(InputStream in);

    /**
     * @see java.lang.Runtime#getLocalizedOutputStream(java.io.OutputStream)
     * @deprecated As of JDK&nbsp;1.1, the preferred way to translate a Unicode
     *             character stream into a byte stream in the local encoding is
     *             via the <code>OutputStreamWriter</code>,
     *             <code>BufferedWriter</code>, and <code>PrintWriter</code>
     *             classes.
     */
    OutputStream getLocalizedOutputStream(OutputStream out);

    /**
     * @see java.lang.Runtime#halt(int)
     */
    void halt(int status);

    /**
     * @see java.lang.Runtime#load(java.lang.String)
     */
    void load(String filename);

    /**
     * @see java.lang.Runtime#loadLibrary(java.lang.String)
     */
    void loadLibrary(String libname);

    /**
     * @see java.lang.Runtime#maxMemory()
     */
    long maxMemory();

    /**
     * @see java.lang.Runtime#removeShutdownHook(java.lang.Thread)
     */
    boolean removeShutdownHook(Thread hook);

    /**
     * @see java.lang.Runtime#runFinalization()
     */
    void runFinalization();

    /**
     * @see java.lang.Runtime#totalMemory()
     */
    long totalMemory();

    /**
     * @see java.lang.Runtime#traceInstructions(boolean)
     */
    void traceInstructions(boolean on);

    /**
     * @see java.lang.Runtime#traceMethodCalls(boolean)
     */
    void traceMethodCalls(boolean on);

    // -------------------------------------
    public static final class Default extends RuntimeI.Stub {
        Runtime runtime = Runtime.getRuntime();

        // public void addShutdownHook(Thread hook) {
        // runtime.addShutdownHook(hook);
        // }

        @Override
		public int availableProcessors() {
            return runtime.availableProcessors();
        }

        // public Process exec(String command, String[] envp, File dir)
        // {
        // return runtime.exec(command, envp, dir);
        // }

        // public Process exec(String cmd, String[] envp) {
        // return runtime.exec(cmd, envp);
        // }

        // public Process exec(String command) {
        // return runtime.exec(command);
        // }

        @Override
		public Process exec(final String[] cmdarray, final String[] envp,
                final File dir) {
            Exceptions.Block block = new Exceptions.Block() {
                @Override
				protected Object inner() throws Exception {
                    return runtime.exec(cmdarray, envp, dir);
                }
            };
            return (Process) block.exec();
        }

        // public Process exec(String[] cmdarray, String[] envp)
        // {
        // return runtime.exec(cmdarray, envp);
        // }

        // public Process exec(String[] cmdarray) {
        // return runtime.exec(cmdarray);
        // }

        // public void exit(int status) {
        // runtime.exit(status);
        // }

        @Override
		public long freeMemory() {
            return runtime.freeMemory();
        }

        // public void gc() {
        // runtime.gc();
        // }

        // /** @deprecated */
        // public InputStream getLocalizedInputStream(InputStream in) {
        // return runtime.getLocalizedInputStream(in);
        // }

        // /** @deprecated */
        // public OutputStream getLocalizedOutputStream(OutputStream out) {
        // return runtime.getLocalizedOutputStream(out);
        // }

        // public void halt(int status) {
        // runtime.halt(status);
        // }

        // public void load(String filename) {
        // runtime.load(filename);
        // }

        // public void loadLibrary(String libname) {
        // runtime.loadLibrary(libname);
        // }

        @Override
		public long maxMemory() {
            return runtime.maxMemory();
        }

        // public boolean removeShutdownHook(Thread hook) {
        // return runtime.removeShutdownHook(hook);
        // }

        // public void runFinalization() {
        // runtime.runFinalization();
        // }

        @Override
		public long totalMemory() {
            return runtime.totalMemory();
        }

        // public void traceInstructions(boolean on) {
        // runtime.traceInstructions(on);
        // }

        // public void traceMethodCalls(boolean on) {
        // runtime.traceMethodCalls(on);
        // }
    }

    // -------------------------------------
    public static class Stub implements RuntimeI {

        @Override
		public void addShutdownHook(Thread hook) {
            throw new NotImplementedException(hook);
        }

        @Override
		public int availableProcessors() {
            throw new NotImplementedException();
        }

        @Override
		public Process exec(String command, String[] envp, File dir) {
            throw new NotImplementedException(command, envp, dir);
        }

        @Override
		public Process exec(String cmd, String[] envp) {
            throw new NotImplementedException(cmd, envp);
        }

        @Override
		public Process exec(String command) {
            throw new NotImplementedException(command);
        }

        @Override
		public Process exec(String[] cmdarray, String[] envp, File dir) {
            throw new NotImplementedException(cmdarray, envp, dir);
        }

        @Override
		public Process exec(String[] cmdarray, String[] envp) {
            throw new NotImplementedException(cmdarray, envp);
        }

        @Override
		public Process exec(String[] cmdarray) {
            throw new NotImplementedException(cmdarray);
        }

        @Override
		public void exit(int status) {
            throw new NotImplementedException(Integer.valueOf(status));
        }

        @Override
		public long freeMemory() {
            throw new NotImplementedException();
        }

        @Override
		public void gc() {
            throw new NotImplementedException();
        }

        @Override
		public InputStream getLocalizedInputStream(InputStream in) {
            throw new NotImplementedException(in);
        }

        @Override
		public OutputStream getLocalizedOutputStream(OutputStream out) {
            throw new NotImplementedException(out);
        }

        @Override
		public void halt(int status) {
            throw new NotImplementedException(Integer.valueOf(status));
        }

        @Override
		public void load(String filename) {
            throw new NotImplementedException(filename);
        }

        @Override
		public void loadLibrary(String libname) {
            throw new NotImplementedException(libname);
        }

        @Override
		public long maxMemory() {
            throw new NotImplementedException();
        }

        @Override
		public boolean removeShutdownHook(Thread hook) {
            throw new NotImplementedException(hook);
        }

        @Override
		public void runFinalization() {
            throw new NotImplementedException();
        }

        @Override
		public long totalMemory() {
            throw new NotImplementedException();
        }

        @Override
		public void traceInstructions(boolean on) {
            throw new NotImplementedException(Boolean.valueOf(on));
        }

        @Override
		public void traceMethodCalls(boolean on) {
            throw new NotImplementedException(Boolean.valueOf(on));
        }
    }
}
