package codeine.db.mysql;

import java.util.HashMap;
import java.util.Map;

public class MysqlProcessConfiguration {

	public Map<String, String> getSqlOptions()
	{
		Map<String, String> options = new HashMap<String, String>();
		options.put("skip-innodb-use-native-aio", null);
		options.put("max_allowed_packet", "16M");
		return options;
	}
}
