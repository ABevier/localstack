package cloud.localstack.docker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cloud.localstack.docker.command.LogCommand;
import cloud.localstack.docker.command.PortCommand;
import cloud.localstack.docker.command.PullCommand;
import cloud.localstack.docker.command.RunCommand;
import cloud.localstack.docker.command.StopCommand;

public class Container {

    private static final Logger LOG = Logger.getLogger(Container.class.getName());

    private static final String LOCALSTACK_NAME = "localstack/localstack";
    private static final String LOCALSTACK_PORTS = "4567-4583";

    private static final int MAX_PORT_CONNECTION_ATTEMPTS = 10;

    private static final int MAX_LOG_COLLECTION_ATTEMPTS = 120;
    private static final long POLL_INTERVAL = 1000;
    private static final int NUM_LOG_LINES = 10;


    private final String containerId;
    private final List<PortMapping> ports;


    public static Container create() {
        LOG.info("Pulling latest image...");
        new PullCommand(LOCALSTACK_NAME).execute();

        String containerId = new RunCommand(LOCALSTACK_NAME).withExposedPorts(LOCALSTACK_PORTS).execute();
        LOG.info("Started container: " + containerId);

        List<PortMapping> portMappings = new PortCommand(containerId).execute();
        return new Container(containerId, portMappings);
    }


    private Container(String containerId, List<PortMapping> ports) {
        this.containerId = containerId;
        this.ports = Collections.unmodifiableList(ports);
    }


    public int getExternalPortFor(int internalPort) {
        return ports.stream()
                .filter(port -> port.getInternalPort() == internalPort)
                .map(PortMapping::getExternalPort)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Port: " + internalPort + " does not exist"));
    }


    public void waitForAllPorts() {
        waitForAllPorts("127.0.0.1");
    }


    public void waitForAllPorts(String ip) {
        ports.forEach(port -> waitForPort(ip, port));
    }


    private void waitForPort(String ip, PortMapping port) {
        int attempts = 0;
        do {
            if(isPortOpen(ip, port)) {
                return;
            }
            attempts++;
        }
        while(attempts < MAX_PORT_CONNECTION_ATTEMPTS);

        throw new IllegalStateException("Could not open port:" + port.getExternalPort() + " on ip:" + port.getIp());
    }


    private boolean isPortOpen(String ip, PortMapping port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port.getExternalPort()), 1000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void waitForLogToken(Pattern pattern) {
        int attempts = 0;
        do {
            if(logContainsPattern(pattern)) {
                return;
            }
            waitForLogs();
            attempts++;
        }
        while(attempts < MAX_LOG_COLLECTION_ATTEMPTS);

        throw new IllegalStateException("Could not find token: " + pattern.toString() + " in docker logs.");
    }


    private boolean logContainsPattern(Pattern pattern) {
        String logs = new LogCommand(containerId).withNumberOfLines(NUM_LOG_LINES).execute();
        return pattern.matcher(logs).find();
    }


    private void waitForLogs(){
        try {
            //TODO: better way to do this?
            Thread.sleep(POLL_INTERVAL);
        }
        catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }


    public void stop(){
        new StopCommand(containerId).execute();
        LOG.info("Stopped container: " + containerId);
    }
}
