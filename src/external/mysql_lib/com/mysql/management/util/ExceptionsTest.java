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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;

import junit.framework.TestCase;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: ExceptionsTest.java,v 1.2 2007-04-22 09:57:55 nambar Exp $
 */
public class ExceptionsTest extends TestCase {
    Exceptions exceptions;

    @Override
	protected void setUp() {
        exceptions = new Exceptions();
    }

    public void testToRuntime() {
        Exception runtime = new RuntimeException("test");
        assertSame(runtime, exceptions.toRuntime(runtime));

        Exception checkedException = new SQLException("test");
        assertFalse(checkedException instanceof RuntimeException);
        Exception returned = exceptions.toRuntime(checkedException);
        assertTrue(returned instanceof RuntimeException);
        assertEquals(checkedException, returned.getCause());
    }

    public void testToSQLException() {
        Exception testSQLE = new SQLException("test");
        assertSame(testSQLE, exceptions.toSQLException(testSQLE));

        Exception checkedException = new Exception("test");
        assertFalse(checkedException instanceof SQLException);
        Exception returned = exceptions.toSQLException(checkedException);
        assertTrue(returned instanceof SQLException);
        assertEquals(checkedException, returned.getCause());
    }

    public void testReturnBlock() {
        final String expected = "foo";

        Exceptions.StringBlock block = new Exceptions.StringBlock() {
            @Override
			public String inner() throws Exception {
                return expected;
            }
        };

        assertEquals(expected, block.exec());
    }

    public void testReturnBlockThrows() {
        Exception expected = null;
        final Exception orig = new Exception("test");

        Exceptions.StringBlock block = new Exceptions.StringBlock() {
            @Override
			public String inner() throws Exception {
                throw orig;
            }
        };

        try {
            block.exec();
        } catch (Exception e) {
            expected = e;
        }

        assertNotNull(expected);
        assertEquals(orig, expected.getCause());
    }

    public void testVoidBlock() {
        final Exception orig = new SQLException();
        assertNull(orig.getMessage());
        Exception caught = null;
        Exceptions.VoidBlock block = new Exceptions.VoidBlock() {
            @Override
			public void inner() throws Exception {
                throw orig;
            }
        };
        try {
            block.exec();
        } catch (WrappedException converted) {
            caught = converted;
        }
        assertNotNull(caught);
        assertEquals(orig, caught.getCause());
    }

    public void testRunSQLBlock() {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        String msg = "search for me";
        final Exception orig = new Exception(msg);
        SQLException caught = null;
        Exceptions.SQLBlock block = new Exceptions.SQLBlock(
                new PrintStream(log)) {
            @Override
			public Object inner() throws Exception {
                throw orig;
            }
        };
        try {
            block.exec();
        } catch (SQLException e) {
            caught = e;
        }
        assertNotNull(caught);
        assertEquals(orig, caught.getCause());
        assertTrue(log.toString().indexOf(msg) >= 0);
    }

    public void testConstructions() {
        new NotImplementedException();
        new NotImplementedException("a");
        new NotImplementedException("a", "b");
        new NotImplementedException("a", "b", "c");
        new NotImplementedException("a", "b", "c", "d");
        new NotImplementedException("a", "b", "c", "d", "e");
        // new NotImplementedException("a", "b", "c", "d", "e", "f");
    }

    public void testSwallowExceptionBlockExec() {
        ByteArrayOutputStream log = new ByteArrayOutputStream();
        PrintStream fakeErr = new PrintStream(log);
        final Exception orig = new SQLException("foo");
        Exceptions.VoidBlock block = new Exceptions.VoidBlock() {
            @Override
			public void inner() throws Exception {
                throw orig;
            }
        };
        block.execNotThrowingExceptions(fakeErr);
        assertTrue(log.toString().indexOf("foo") >= 0);
    }
}
