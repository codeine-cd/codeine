package com.mysql.management.util;

import java.io.File;

public final class Utils {
    private Files files;

    private Streams streams;

    private Shell.Factory shellFactory;

    private Threads threads;

    private Str str;

    public Utils() {
        this.shellFactory = new Shell.Factory();
        this.str = new Str();
        this.streams = new Streams();
        this.threads = new Threads();
        this.files = new Files(shellFactory, File.separatorChar, streams);
    }

    public Utils(Files files, Shell.Factory shellFactory, Streams streams,
            Threads threads, Str str) {
        this.files = files;
        this.shellFactory = shellFactory;
        this.str = str;
        this.streams = streams;
        this.threads = threads;
    }

    public Files files() {
        return files;
    }

    public Streams streams() {
        return streams;
    }

    public Shell.Factory shellFactory() {
        return shellFactory;
    }

    public Threads threads() {
        return threads;
    }

    public Str str() {
        return str;
    }

    public void setFiles(Files files) {
        this.files = files;
    }
}
