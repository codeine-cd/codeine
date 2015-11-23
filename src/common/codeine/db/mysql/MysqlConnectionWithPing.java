package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;
import codeine.utils.StringUtils;

public class MysqlConnectionWithPing {
    private MysqlConfigurationJson configuration;
    private long pingTime;

    public MysqlConnectionWithPing(MysqlConfigurationJson configuration, Long pingTime) {
        this.configuration = configuration;
        this.pingTime = pingTime;
    }

    public long getPingTime() {
        return pingTime;
    }

    public MysqlConfigurationJson getConfiguration() {
        return configuration;
    }

    @Override
    public String toString() {
        return configuration + " Time: " + StringUtils.formatTimePeriod(pingTime);
    }
}
