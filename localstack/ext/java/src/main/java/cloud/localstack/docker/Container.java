package cloud.localstack.docker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import cloud.localstack.docker.command.PortCommand;
import cloud.localstack.docker.command.RunCommand;
import cloud.localstack.docker.command.StopCommand;

public class Container {

    private static final Logger LOG = Logger.getLogger(Container.class.getName());

    private static final String LOCALSTACK_NAME = "localstack/localstack";
    private static final String LOCALSTACK_PORTS = "4567-4583";

    private final String containerId;
    private final List<PortMapping> ports;

    public static Container create() {
        DockerExe dockerExe = new DockerExe();

        String containerId = new RunCommand(dockerExe, LOCALSTACK_NAME, LOCALSTACK_PORTS).execute();
        List<PortMapping> portMappings = new PortCommand(dockerExe, containerId).execute();

        return new Container(containerId, portMappings);
    }


    private Container(String containerId, List<PortMapping> ports) {
        this.containerId = containerId;
        this.ports = Collections.unmodifiableList(ports);
    }


    public List<PortMapping> getPorts() {
        return ports;
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
        LOG.info("All ports successfully opened.");
    }


    private void waitForPort(String ip, PortMapping port) {
        int attempts = 0;
        do {
            if(isPortOpen(ip, port)) {
                return;
            }
            attempts++;
        }
        while(attempts < 10);

        throw new IllegalStateException("Could not open port:" + port.getExternalPort() + " on ip:" + port.getIp());
    }


    private boolean isPortOpen(String ip, PortMapping port) {
        try (Socket socket = new Socket()) {
            LOG.info("##### Attempting to open connection to port: " + port.getExternalPort() + "on ip:" + ip);
            socket.connect(new InetSocketAddress(ip, port.getExternalPort()), 500);
            LOG.info("External Port '" + port.getExternalPort() + "' on ip '" + ip + "' was open");
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public void stop(){
        DockerExe dockerExe = new DockerExe();
        new StopCommand(dockerExe, containerId).execute();
    }
}
