package com.mysql.management.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: ListToString.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
@SuppressWarnings({ "rawtypes" })
public final class ListToString {
    private String prefix;

    private String separator;

    private String postfix;

    public ListToString() {
        this("[", "][", "]");
    }

    public ListToString(String prefix, String separator, String postfix) {
        this.prefix = prefix;
        this.separator = separator;
        this.postfix = postfix;
    }

    /**
     * returns the contentents of the collection as a string a collections with
     * "a", "b", null, and Integer.valueOf(1) would return: {[a][b][null][1]}
     */
    public String toString(Object[] objs) {
        if (objs == null) {
            return String.valueOf(null);
        }
        return toString(Arrays.asList(objs));
    }

    public String toString(Map map) {
        if (map == null) {
            return String.valueOf(null);
        }
        return toString(map.entrySet());
    }

    /**
     * returns the contentents of the collection as a string a collections with
     * "a", "b", null, and Integer.valueOf(1) would return: {[a][b][null][1]}
     * 
     * @param objs
     *            collection
     * @param prefix
     * @param separator
     * @param postfix
     * @return the contentents of the collection as a string
     */
    public String toString(Collection objs) {
        if (objs == null) {
            return String.valueOf(objs);
        }
        StringBuffer buf = new StringBuffer(prefix);
        for (Iterator iter = objs.iterator(); iter.hasNext();) {
            buf.append(toString(iter.next()));
            if (iter.hasNext()) {
                buf.append(separator);
            }
        }
        buf.append(postfix);
        return buf.toString();
    }

    public String toString(Object obj) {
        if (obj instanceof Object[]) {
            return toString((Object[]) obj);
        }
        if (obj instanceof Collection) {
            return toString((Collection) obj);
        }
        if (obj instanceof Map) {
            return toString((Map) obj);
        }
        if (obj instanceof Map.Entry) {
            Map.Entry entry = (Map.Entry) obj;
            return entry.getKey() + "=" + toString(entry.getValue());
        }
        return String.valueOf(obj);
    }
}
