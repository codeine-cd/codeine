package com.mysql.management.util;

import junit.framework.TestCase;

public class RuntimeTest extends TestCase {

    public void testImplemented() {
        Runtime realRuntime = Runtime.getRuntime();
        RuntimeI runtime = new RuntimeI.Default();
        assertEquals(realRuntime.availableProcessors(), runtime
                .availableProcessors());
        assertEquals(realRuntime.freeMemory(), runtime.freeMemory());
        assertEquals(realRuntime.maxMemory(), runtime.maxMemory());
        assertEquals(realRuntime.totalMemory(), runtime.totalMemory());
    }

    public void testStub() throws Exception {
        new TestUtil().assertObjStubsInterface(new RuntimeI.Stub(),
                RuntimeI.class);
    }
}
