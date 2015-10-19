package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import com.google.common.collect.Lists;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Mockito.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;


public class NearestHostSelectorTest {
    private IDBConnection mock = mock(IDBConnection.class);
    private static long prevThreshold;

    @BeforeClass
    public static void before() {
        prevThreshold = NearestHostSelector.DIFF_THRESHOLD;
        NearestHostSelector.DIFF_THRESHOLD = 10;
    }

    @AfterClass
    public static void after() {
        NearestHostSelector.DIFF_THRESHOLD = prevThreshold;
    }

    @Test
    public void testOneHostSelect() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 0);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(0));
    }

    @Test
    public void test2HostSelect() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 0);
        mockCheckConnection(list.get(1), 5);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(0));
    }
    @Test
    public void test2HostSelectedBecomesInvalid() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 0);
        mockCheckConnection(list.get(1), 5);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        mockCheckConnectionFails(list.get(0));
        assertHost(target, list.get(1));
    }
    @Test(expected=RuntimeException.class)
    public void testAllHostsAreInvalid() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        NearestHostSelector target = new NearestHostSelector(list, mock);
        mockCheckConnectionFails(list.get(0));
        target.select();
    }
    @Test(expected=RuntimeException.class)
    public void testLastSqlBecomesInvalid() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        NearestHostSelector target = new NearestHostSelector(list, mock);
        mockCheckConnection(list.get(0),0);
        target.select();
        mockCheckConnectionFails(list.get(0));
        target.select();
    }

    @Test
    public void test2HostSmallDiffSelect() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 0);
        mockCheckConnection(list.get(1), 5);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(0));
        mockCheckConnection(list.get(0), 10);
        assertHost(target, list.get(0));
    }

    @Test
    public void test3Host() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host3", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 10);
        mockCheckConnection(list.get(1), 0);
        mockCheckConnection(list.get(2), 5);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(1));
        mockCheckConnection(list.get(1), 25);
        assertHost(target, list.get(2));
    }

    @Test
    public void test2HostDifferenceOrder() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 5);
        mockCheckConnection(list.get(1), 0);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(1));
        mockCheckConnection(list.get(1), 10);
        assertHost(target, list.get(1));
    }

    @Test
    public void test2HostChangeHost() throws Exception {
        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        list.add(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 5);
        mockCheckConnection(list.get(1), 0);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        assertHost(target, list.get(1));
        mockCheckConnection(list.get(1), 25);
        assertHost(target, list.get(0));
    }

    @Test
    public void testNotCheckingSameConnectionTwice() throws Exception {

        List<MysqlConfigurationJson> list = Lists.newArrayList();
        list.add(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"));
        mockCheckConnection(list.get(0), 0);
        NearestHostSelector target = new NearestHostSelector(list, mock);
        target.select();
        target.select();
        verify(mock, times(2)).checkConnection(anyString(), anyInt(), anyString(), anyString());
    }

    private void assertHost(NearestHostSelector target, MysqlConfigurationJson json) {
        Assert.assertEquals(json.host(), target.select().host());
    }

    private void mockCheckConnection(MysqlConfigurationJson json, final long timeToSleep) {
        when(mock.checkConnection(json.host(), json.port(), json.user(), json.password())).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                Thread.sleep(timeToSleep);
                return true;
            }
        });
    }
    private void mockCheckConnectionFails(MysqlConfigurationJson json) {
        when(mock.checkConnection(json.host(), json.port(), json.user(), json.password())).thenReturn(false);
    }


}