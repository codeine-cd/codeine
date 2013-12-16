package com.mysql.management.util;

public final class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotImplementedException() {
        super();
    }

    public NotImplementedException(Object arg0) {
        super("args: " + arg0);
    }

    public NotImplementedException(Object[] arg0) {
        this(new ListToString().toString(arg0));
    }

    public NotImplementedException(Object arg0, Object arg1) {
        this(new Object[] { arg0, arg1 });
    }

    public NotImplementedException(Object arg0, Object arg1, Object arg2) {
        this(new Object[] { arg0, arg1, arg2 });
    }

    public NotImplementedException(Object arg0, Object arg1, Object arg2,
            Object arg3) {
        this(new Object[] { arg0, arg1, arg2, arg3 });
    }

    public NotImplementedException(Object arg0, Object arg1, Object arg2,
            Object arg3, Object arg4) {
        this(new Object[] { arg0, arg1, arg2, arg3, arg4 });
    }
}
