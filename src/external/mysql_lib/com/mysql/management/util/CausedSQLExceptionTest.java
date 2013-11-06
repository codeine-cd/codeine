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

import java.sql.SQLException;

import junit.framework.TestCase;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: CausedSQLExceptionTest.java,v 1.1 2005/02/16 21:46:10 eherman
 *          Exp $
 */
public class CausedSQLExceptionTest extends TestCase {

    public void testGetCause() {
        Exception runtime = new RuntimeException("test");
        Exception sqlException = new CausedSQLException(runtime);
        assertSame(runtime, sqlException.getCause());
        assertTrue(sqlException instanceof SQLException);
    }
}
