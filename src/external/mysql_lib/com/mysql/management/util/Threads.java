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

/**
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: Threads.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
public final class Threads {

    /**
     * Convienence funciton to wrap a try catch around Thread.sleep(millis);
     * 
     * @param millis
     */
    public void pause(final int millis) {
        new Exceptions.VoidBlock() {
            @Override
			public void inner() throws InterruptedException {
                Thread.sleep(millis);
            }
        }.exec();
    }

    public String newName() {
        String baseName = Thread.currentThread().getName();
        return newName(baseName);
    }

    public String newName(String baseName) {
        return baseName + "_" + System.currentTimeMillis();
    }
}
