package codeine.jsons.global;

import java.util.List;

import codeine.configuration.EmailConfiguration;
import codeine.jsons.auth.AuthenticationMethod;
import codeine.model.Constants;

import com.google.common.collect.Lists;

public class GlobalConfigurationJson {

    private Integer web_server_port = Constants.DEFAULT_WEB_SERVER_PORT;
    private Integer directory_port = 12348;
    private Integer mail_server_port = 12349;
    private String directory_host;
    private String admin_mail;
    private String server_name;
    private EmailConfiguration email_configuration;
    private String web_server_host;
    private AuthenticationMethod authentication_method = AuthenticationMethod.Disabled;
    private List<MysqlConfigurationJson> mysql = Lists.newArrayList();
    private String[] roles = {};
    private boolean large_deployment;
    private boolean disable_auto_select_mysql;
    private Integer max_db_pool_size = 2;
    private Integer min_db_pool_size = 2;
    private boolean prometheus_enabled = true;
    private Integer connectivity_check_timeout_ms = 3000;
    private boolean consul_registration;
    private Integer node_interval_seconds = 60;

    public GlobalConfigurationJson() {
    }

    public GlobalConfigurationJson(String hostName) {
        web_server_host = hostName;
        directory_host = hostName;

    }

    public Integer web_server_port() {
        return web_server_port;
    }

    public String admin_mail() {
        return admin_mail;
    }

    public String directory_host() {
        return directory_host;
    }

    public Integer directory_port() {
        return directory_port;
    }

    public String web_server_host() {
        return web_server_host;
    }

    public EmailConfiguration email_configuration() {
        return email_configuration;
    }

    public int mail_server_port() {
        return mail_server_port;
    }

    public AuthenticationMethod authentication_method() {
        return authentication_method;
    }

    public List<MysqlConfigurationJson> mysql() {
        return mysql;
    }

    public String[] roles() {
        return roles;
    }

    public boolean large_deployment() {
        return large_deployment;
    }

    public String server_name() {
        return server_name;
    }

    public void web_server_port(int web_server_port) {
        this.web_server_port = web_server_port;

    }

    public boolean disable_auto_select_mysql() {
        return disable_auto_select_mysql;
    }

    public Integer max_db_pool_size() {
        return max_db_pool_size;
    }

    public Integer min_db_pool_size() {
        return min_db_pool_size;
    }

    public boolean prometheus_enabled() {
        return prometheus_enabled;
    }

    public boolean consul_registration() {
        return consul_registration;
    }

    public Integer connectivity_check_timeout_ms() {
        return connectivity_check_timeout_ms;
    }

    public Integer node_interval_seconds() {
        return node_interval_seconds;
    }

}
