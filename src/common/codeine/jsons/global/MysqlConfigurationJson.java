package codeine.jsons.global;

import codeine.db.mysql.MysqlConstants;
import codeine.utils.StringUtils;

public class MysqlConfigurationJson {

	private String host;
	private Integer port;
	private String dir;
	private String bin_dir;
	private String user;
	private String password;
	private boolean managed_by_codeine = true;

	public MysqlConfigurationJson() {

	}

	public MysqlConfigurationJson(String host, Integer port, String dir, String bin_dir) {
		super();
		this.host = host;
		this.port = port;
		this.dir = dir;
		this.bin_dir = bin_dir;
	}

	public String dir() {
		return dir;
	}

	public Integer port() {
		return port;
	}

	public String host() {
		return host;
	}

	public String bin_dir() {
		return bin_dir;
	}

	public boolean managed_by_codeine() {
		return managed_by_codeine;
	}

	public String user() {
		if (StringUtils.isEmpty(user)) {
			return MysqlConstants.DB_USER;
		}
		return user;
	}
	public String password() {
		if (StringUtils.isEmpty(password)) {
			return MysqlConstants.DB_PASSWORD;
		}
		return password;
	}

	@Override
	public String toString() {
		return "MysqlConfigurationJson [host=" + host + ", port=" + port + ", user=" + user + ", managed_by_codeine="
				+ managed_by_codeine + ", dir=" + dir + ", bin_dir=" + bin_dir + "]";
	}

	
}
