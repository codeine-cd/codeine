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

import java.util.Map;

/**
 * Simple (and obvious) implementation of java.util.Map.Entry
 * 
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: MapEntry.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
@SuppressWarnings({ "rawtypes"})
public final class MapEntry implements Map.Entry {
    private Object key;

    private Object value;

    private Equals equals;

    public MapEntry(Object key, Object value) {
        this.key = key;
        this.value = value;
        this.equals = new Equals();
    }

    @Override
	public Object getKey() {
        return key;
    }

    @Override
	public Object getValue() {
        return value;
    }

    @Override
	public Object setValue(Object value) {
        Object oldVal = this.value;
        this.value = value;
        return oldVal;
    }

    @Override
	public boolean equals(Object obj) {
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        return equals((Map.Entry) obj);
    }

    public boolean equals(Map.Entry other) {
        if (other == this) {
            return true;
        }

        if ((other == null) || (hashCode() != other.hashCode())) {
            return false;
        }

        return equals.nullSafe(key, other.getKey())
                && equals.nullSafe(value, other.getValue());
    }

    /**
     * XOR of the key and value hashCodes. (Zero used for nulls) as defined by
     * Map.Entry java doc.
     */
    @Override
	public int hashCode() {
        int keyHashCode = (key == null) ? 0 : key.hashCode();
        int valHashCode = (value == null) ? 0 : value.hashCode();
        return keyHashCode ^ valHashCode;
    }

    @Override
	public String toString() {
        return getKey() + "=" + getValue();
    }
}
