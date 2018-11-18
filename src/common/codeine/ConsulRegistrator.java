package codeine;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import org.apache.log4j.Logger;

public class ConsulRegistrator {

    private static final Logger log = Logger.getLogger(ConsulRegistrator.class);
    private final Consul client;

    public ConsulRegistrator() {
        client = Consul.builder().build();
    }

    public boolean consulExists() {
        try {
            client.statusClient().getLeader();
            return true;
        } catch (RuntimeException ex) {
            log.error("Failed to get status from consul", ex);
            return false;
        }
    }

    public void register(String name, int port) {
        log.info("Will register " + name + " with port " + port);
        AgentClient agentClient = client.agentClient();
        Registration service = ImmutableRegistration.builder()
            .id(name)
            .name(name)
            .port(port)
            .check(Registration.RegCheck.tcp("localhost:" + port, 10, 5))
            .build();
        agentClient.register(service);
    }

    public void deregister(String name) {
        log.info("Will de register " + name);
        AgentClient agentClient = client.agentClient();
        agentClient.deregister(name);
    }
}
