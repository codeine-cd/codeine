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

import junit.framework.TestCase;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: StrTest.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
public class StrTest extends TestCase {
    private Str str;

    @Override
	protected void setUp() {
        str = new Str();
    }

    public void testContainsIgnoreCase() throws Exception {
        assertTrue(str.containsIgnoreCase("foobarbaz", "bar"));
        assertTrue(str.containsIgnoreCase("foobarbaz", "BAR"));
        assertTrue(str.containsIgnoreCase("fooBARbaz", "bar"));

        assertFalse(str.containsIgnoreCase("foobarbaz", "whiz"));
    }

    public void testShortName() {
        assertEquals("String", str.shortClassName(String.class));
    }
}
