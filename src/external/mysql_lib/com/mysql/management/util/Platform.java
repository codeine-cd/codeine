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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: Platform.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Platform {
    public static final String OS_NAME = "os.name";

    public static final String OS_ARCH = "os.arch";

    PrintWriter writer;

    public Platform(PrintWriter writer) {
        this.writer = writer;
    }

    public void report() {
        report(platformProps());
    }

    void report(Collection propertyKeys) {
        for (Iterator iter = propertyKeys.iterator(); iter.hasNext();) {
            String property = (String) iter.next();
            writer.print(property);
            writer.print('=');
            writer.println(System.getProperty(property));
            writer.flush();
        }
    }

    List platformProps() {
        List list = new ArrayList();
        list.add("java.vm.vendor");
        list.add("java.vm.version");
        list.add(OS_NAME);
        list.add(OS_ARCH);
        list.add("os.version");
        return list;
    }

    public static void main(String[] args) {
        new Platform(new PrintWriter(System.out)).report();
    }
}
