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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * A thread to read from the input stream and write to the output stream.
 * 
 * Not buffered.
 * 
 * This class is final simply as a hint to the compiler, it may be un-finalized
 * safely.
 * 
 * @author Eric Herman <eric@mysql.com>
 * @version $Id: StreamConnector.java,v 1.2 2007-04-22 09:57:54 nambar Exp $
 */
public final class StreamConnector extends Thread {
    private static int count = 0;

    private InputStream from;

    private OutputStream to;

    public StreamConnector(InputStream from, OutputStream to, String name) {
        super("StreamConnector " + count() + ": " + name);
        this.from = from;
        this.to = to;
    }

    @Override
	public void run() {
        new Exceptions.VoidBlock() {
            @Override
			public void inner() throws Exception {
                new Streams().copy(from, to, false, true);
            }
        }.exec();
    }

    private static synchronized int count() {
        return count++;
    }
}
