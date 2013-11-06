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

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

@SuppressWarnings({ "rawtypes"})
public class TestUtil {
    private int port;

    public TestUtil() {
        this(Integer.parseInt(System.getProperty("c-mxj_test_port", "3336")));
    }

    public TestUtil(int port) {
        this.port = port;
    }

    public int testPort() {
        return port;
    }

    public void assertContainsIgnoreCase(String searchIn, String searchFor) {
        if (new Str().containsIgnoreCase(searchIn, searchFor)) {
            return;
        }
        String msg = "<" + searchFor + "> not found in <" + searchIn + ">";
        throw new AssertionFailedError(msg);
    }

    public void assertConnectViaJDBC(String url, boolean dbInUrl)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {

        assertConnectViaJDBC(url, "root", "", dbInUrl);
    }

    public void assertConnectViaJDBC(String url) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {

        assertConnectViaJDBC(url, false);
    }

    public void assertConnectViaJDBC(String url, String user, String password)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, SQLException {

        assertConnectViaJDBC(url, user, password, false);
    }

    public void assertConnectViaJDBC(String url, String user, String password,
            boolean dbInUrl) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {

        String name = com.mysql.jdbc.Driver.class.getName();
        Class c = Class.forName(name);
        c.newInstance();

        Connection conn = DriverManager.getConnection(url, user, password);
        try {
            if (!dbInUrl) {
                useDbTest(conn);
            }
            checkVersion(conn);
            checkBigInt(conn);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void assertNotImplemented(Object stub, Method method) {
        try {
            invoke(stub, method);
        } catch (NotImplementedException e) {
            return;
        } catch (Exception e) {
            if (isNotImplementedMsg(e.getMessage())) {
                return;
            }
            Throwable cause = e.getCause();
            if (cause instanceof NotImplementedException) {
                return;
            }
            if (cause != null) {
                if (isNotImplementedMsg(cause.getMessage())) {
                    return;
                }
            }
            new Exceptions().toRuntime(e);
        }
        throw new RuntimeException("This is now implemented.");
    }

    public void assertObjStubsInterface(Object stub, Class anInterface) {
        Method[] methods = anInterface.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            assertNotImplemented(stub, methods[i]);
        }
    }

    private boolean isNotImplementedMsg(String msg) {
        if (msg == null) {
            return false;
        }
        String serachFor = "Not implemented".toLowerCase();
        return msg.toLowerCase().indexOf(serachFor) >= 0;
    }

    private void invoke(final Object target, final Method method)
            throws Exception {
        Class[] paramTypes = method.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            params[i] = newParamObject(paramTypes[i]);
        }
        method.invoke(target, params);
    }

    private Object newParamObject(Class paramType) {
        if (paramType.equals(int.class) || paramType.equals(Integer.class)) {
            return Integer.valueOf(0);
        }

        if (paramType.equals(boolean.class) || paramType.equals(Boolean.class)) {
            return Boolean.FALSE;
        }

        if (paramType.equals(Object[].class)
                || paramType.equals(String[].class)) {
            return new String[0];
        }

        if (paramType.equals(Runnable.class) || paramType.equals(Thread.class)) {
            return new Thread();
        }

        return null;
    }

    /** basic check to see if the database is there, selects the version */
    private void checkVersion(Connection conn) throws SQLException {
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT VERSION()");
            Assert.assertTrue(rs.next());
            String version = rs.getString(1);
            Assert.assertTrue(version, version.startsWith("5."));
        } 
        finally 
        {
        	try
        	{
        		if(rs != null)
            	{
            		rs.close();
            	}
                if (stmt != null) 
                {
                	stmt.close();
                }
        	}
        	catch (SQLException e)
        	{
                e.printStackTrace();
            }
        }
    }

    private void useDbTest(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        try {
            stmt.executeUpdate("use test");
        } finally {
            stmt.close();
        }
    }

    /** creates table, inserts, selects, drops table */
    private void checkBigInt(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = null;
        String tableName = "bigIntRegression";
        String col1 = "bigIntCol";
        long testVal = 6692730313872877584L;
        try {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
            stmt.executeUpdate("CREATE TABLE " + tableName + " (" + col1
                    + " BIGINT NOT NULL)");
            stmt.executeUpdate("INSERT INTO " + tableName + " VALUES ("
                    + testVal + ")");
            rs = stmt.executeQuery("SELECT " + col1 + " FROM " + tableName);

            int rows = 0;
            while (rs.next()) {
                long retrieveAsLong = rs.getLong(1);
                Assert.assertEquals(testVal, retrieveAsLong);
                rows++;
            }

            Assert.assertEquals(1, rows);
        } 
        finally 
        {
            stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName);
            rs.close();
            stmt.close();
        }
    }
}
