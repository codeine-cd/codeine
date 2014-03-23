package codeine.db.mysql;

import codeine.jsons.global.MysqlConfigurationJson;

public class StaticMysqlHostSelector implements MysqlHostSelector {

	private MysqlConfigurationJson mysql;

	
	public StaticMysqlHostSelector(MysqlConfigurationJson mysql) {
		super();
		this.mysql = mysql;
	}


	@Override
	public MysqlConfigurationJson mysql() {
		return mysql;
	}

}
