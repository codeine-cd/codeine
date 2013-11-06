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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: DefaultsMapTest.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultsMapTest extends TestCase {
    DefaultsMap map;

    @Override
	protected void setUp() {
        map = new DefaultsMap();
        map.put("foo", "bar");
        map.put("stay", "unchanged");
        map.put("foo", "baz");
    }

    public void testGetChanged() throws Exception {
        assertEquals(1, map.getChanged().size());
        assertEquals("baz", map.getChanged().get("foo"));

        map.put("foo", "bar");
        assertFalse(map.getChanged().containsKey("foo"));
    }

    public void testContainsValue() throws Exception {
        assertTrue(map.containsValue("baz"));
        assertFalse(map.containsValue("bar"));
        assertFalse(map.containsValue(null));
        map.put("null", null);
        assertTrue(map.containsValue(null));
    }

    public void testGetAndGetDefault() throws Exception {
        assertEquals("bar", map.getDefault("foo"));
        assertEquals("baz", map.get("foo"));
        assertEquals("unchanged", map.get("stay"));
    }

    public void testContainsKey() throws Exception {
        assertTrue(map.containsKey("foo"));
        assertFalse(map.containsKey("bogus"));
    }

    public void testClearAndIsEmpty() {
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
        assertEquals(0, map.getChanged().size());
        assertEquals(0, map.size());
    }

    public void testEntrySet() throws Exception {
        Set entrySet = map.entrySet();
        assertEquals(2, entrySet.size());

        List entries = new ArrayList(entrySet);
        Map.Entry one = (Map.Entry) entries.get(0);
        assertEquals("foo", one.getKey());
        assertEquals("baz", one.getValue());

        Map.Entry two = (Map.Entry) entries.get(1);
        assertEquals("stay", two.getKey());
        assertEquals("unchanged", two.getValue());
    }

    public void testPutAll() {
        Map other = new HashMap();
        other.put("foo", "wiz");
        other.put("new", "val");

        map.putAll(other);
        assertEquals("wiz", map.get("foo"));
        assertEquals("val", map.get("new"));
    }

    public void testRemove() {
        assertEquals(1, map.getChanged().size());
        map.remove("foo");
        assertEquals(0, map.getChanged().size());
        assertFalse(map.containsKey("foo"));

        map.put("foo", "two");
        assertEquals(0, map.getChanged().size());
        map.remove("foo");
        assertFalse(map.containsKey("foo"));
    }

    public void testValues() {
        Collection values = map.values();
        assertEquals(2, values.size());
        assertTrue(map.containsValue("baz"));
        assertTrue(map.containsValue("unchanged"));
    }

    public void testPutUnchangedOriginal() {
        assertEquals(1, map.getChanged().size());
        map.put("stay", "unchanged");
        assertEquals(1, map.getChanged().size());
    }
}
