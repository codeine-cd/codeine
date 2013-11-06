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

import java.io.PrintStream;
import java.sql.SQLException;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: Exceptions.java,v 1.2 2007-04-22 09:57:53 nambar Exp $
 */
public class Exceptions {
    private PrintStream log;

    public Exceptions() {
        this(null);
    }

    public Exceptions(PrintStream log) {
        this.log = log;
    }

    /**
     * Convienence funciton to convert checked exceptions to RuntimeException
     * 
     * @param e
     *            java.lang.Exception
     * @return a RuntimeException if e is already a Runtime Exception, e is
     *         simply returned otherwise e is wrapped by a RuntimeException: new
     *         WrappedException(e)
     * 
     * This is final simply as a hint to the compiler, it may be un-finalized
     * safely.
     */
    public final RuntimeException toRuntime(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new WrappedException(e);
    }

    /**
     * This is final simply as a hint to the compiler, it may be un-finalized
     * safely.
     */
    public final SQLException toSQLException(Exception e) {
        if (e instanceof SQLException) {
            return (SQLException) e;
        }
        return new CausedSQLException(e);
    }

    /**
     * This is final simply as a hint to the compiler, it may be un-finalized
     * safely.
     */
    protected final void log(Exception e) {
        if (log != null) {
            e.printStackTrace(log);
        }
    }

    // ------------------------------------

    public static abstract class Block extends Exceptions {
        abstract protected Object inner() throws Exception;

        public Object exec() {
            try {
                return inner();
            } catch (Exception e) {
                log(e);
                throw toRuntime(e);
            }
        }
    }

    public static abstract class StringBlock extends Exceptions {
        abstract protected String inner() throws Exception;

        public String exec() {
            try {
                return inner();
            } catch (Exception e) {
                log(e);
                throw toRuntime(e);
            }
        }
    }

    public static abstract class SQLBlock extends Exceptions {
        public SQLBlock(PrintStream log) {
            super(log);
        }

        abstract protected Object inner() throws Exception;

        public Object exec() throws SQLException {
            try {
                return inner();
            } catch (Exception e) {
                log(e);
                throw toSQLException(e);
            }
        }
    }

    public static abstract class VoidBlock extends Exceptions {
        abstract protected void inner() throws Exception;

        public void exec() {
            try {
                inner();
            } catch (Exception e) {
                log(e);
                throw toRuntime(e);
            }
        }

        public void execNotThrowingExceptions(PrintStream err) {
            try {
                inner();
            } catch (Exception e) {
                e.printStackTrace(err);
            }
        }
    }
}
