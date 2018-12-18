package codeine;

import codeine.model.Constants;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.ConsulException;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import org.apache.log4j.Logger;

public class ConsulRegistrator {

    private static final Logger log = Logger.getLogger(ConsulRegistrator.class);
    private Consul client;
    private boolean connected;

    public ConsulRegistrator() {

    }

    public void init() {
        try {
            client = Consul.builder().build();
            connected = true;
            log.info("Consul was found");
        } catch (ConsulException ex) {
            log.error("Failed to connect to consul", ex);
        }
    }


    public boolean consulExists() {
        return connected;
    }

    public void register(String name, int port) {
        log.info("Will register " + name + " with port " + port);
        AgentClient agentClient = client.agentClient();
        Registration service = ImmutableRegistration.builder().id(name).name(name).port(port)
            .check(Registration.RegCheck.http("http://localhost:" + port + Constants.HEALTH_CONTEXT, 60, 5)).build();
        agentClient.register(service);
    }

    public void deregister(String name) {
        log.info("Will de register " + name);
        AgentClient agentClient = client.agentClient();
        agentClient.deregister(name);
    }
}
