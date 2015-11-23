package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import com.google.common.collect.Lists;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class NearestHostSelectorTest {
    private IMysqlConnectionsProvider mock = mock(IMysqlConnectionsProvider.class);
    private static long prevThreshold;

    @BeforeClass
    public static void before() {
        prevThreshold = NearestHostSelector.DIFF_THRESHOLD;
        NearestHostSelector.DIFF_THRESHOLD = 5;
    }

    @AfterClass
    public static void after() {
        NearestHostSelector.DIFF_THRESHOLD = prevThreshold;
    }

    @Test
    public void testOneHostSelect() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add( new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
    }

    @Test
    public void test2HostSelect() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD / 2));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
    }

    @Test
    public void test2HostSelectedBecomesInvalid() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD / 2));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
        list.remove(0);
        assertHost(target, list.get(0).getConfiguration());
    }

    @Test(expected=RuntimeException.class)
    public void testAllHostsAreInvalid() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        target.select();
    }

    @Test(expected=RuntimeException.class)
    public void testLastSqlBecomesInvalid() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        target.select();
        list.remove(0);
        target.select();
    }


    @Test
    public void test2HostSmallDiffSelect() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD / 2));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
        list.remove(0);
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"),  NearestHostSelector.DIFF_THRESHOLD));
        assertHost(target, list.get(1).getConfiguration());
    }

    @Test
    public void test3Host() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), 0L));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host3", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD / 2));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"),  NearestHostSelector.DIFF_THRESHOLD));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
        list.remove(0);
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD * 5));
        assertHost(target, list.get(0).getConfiguration());
    }

    @Test
    public void test2HostChangeHost() throws Exception {
        List<MysqlConnectionWithPing> list = Lists.newArrayList();
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), 0L));
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host2", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD / 2));
        when(mock.getMysqlConnections()).thenReturn(list);
        NearestHostSelector target = new NearestHostSelector(mock);
        assertHost(target, list.get(0).getConfiguration());
        list.remove(0);
        list.add(new MysqlConnectionWithPing(new MysqlConfigurationJson("host", 1, "dir", "bin_dir"), NearestHostSelector.DIFF_THRESHOLD * 5));
        assertHost(target, list.get(0).getConfiguration());
    }

    private void assertHost(NearestHostSelector target, MysqlConfigurationJson json) {
        Assert.assertEquals(json.host(), target.select().host());
    }
}