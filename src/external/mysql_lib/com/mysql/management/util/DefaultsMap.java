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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Map which retains the first value a key is set to as a "default" until the
 * key is removed
 * 
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: DefaultsMap.java,v 1.2 2007-04-22 09:57:53 nambar Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class DefaultsMap implements Map {

    private Map changed;

    private Map original;

    private Equals equals;

    public DefaultsMap() {
        this.original = new LinkedHashMap();
        this.changed = new LinkedHashMap();
        this.equals = new Equals();
    }

    public Map getChanged() {
        return new HashMap(changed);
    }

    public Object getDefault(Object key) {
        return original.get(key);
    }

    @Override
	public void clear() {
        changed.clear();
        original.clear();
    }

    @Override
	public boolean containsKey(Object key) {
        return original.containsKey(key);
    }

    @Override
	public boolean containsValue(Object value) {
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            Object val = get(iter.next());
            if (value == null) {
                if (val == null) {
                    return true;
                }
            } else {
                if (value.equals(val)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
	public Set entrySet() {
        Set entries = new LinkedHashSet();
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            Object key = iter.next();
            entries.add(new MapEntry(key, get(key)));
        }
        return entries;
    }

    @Override
	public Object get(Object key) {
        if (changed.containsKey(key)) {
            return changed.get(key);
        }
        return original.get(key);
    }

    @Override
	public boolean isEmpty() {
        return original.size() == 0;
    }

    @Override
	public Set keySet() {
        return original.keySet();
    }

    @Override
	public Object put(Object key, Object value) {
        if (!original.containsKey(key)) {
            return original.put(key, value);
        }
        Object originalVal = original.get(key);
        if (!equals.nullSafe(originalVal, value)) {
            return changed.put(key, value);
        }
        if (changed.containsKey(key)) {
            return changed.remove(key);
        }
        return original.put(key, value);
    }

    @Override
	public void putAll(Map t) {
        for (Iterator iter = t.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry) iter.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
	public Object remove(Object key) {
        Object oldVal = original.remove(key);
        if (changed.containsKey(key)) {
            return changed.remove(key);
        }
        return oldVal;
    }

    @Override
	public int size() {
        return original.size();
    }

	@Override
	public Collection values() {
        List values = new ArrayList(original.size());
        for (Iterator iter = keySet().iterator(); iter.hasNext();) {
            values.add(get(iter.next()));
        }
        return values;
    }
}
