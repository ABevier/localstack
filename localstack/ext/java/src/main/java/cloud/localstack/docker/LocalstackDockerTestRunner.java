package cloud.localstack.docker;

import java.util.logging.Logger;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class LocalstackDockerTestRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOG = Logger.getLogger(LocalstackDockerTestRunner.class.getName());

    /**
     * Creates a BlockJUnit4ClassRunner to run {@code klass}
     *
     * @param klass
     * @throws InitializationError if the test class is malformed.
     */
    public LocalstackDockerTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }


    private static Container localStackContainer;

    public static Container getLocalStackContainer() {
        return localStackContainer;
    }

    private static final int KINESIS_PORT = 4568;
    private static final int DYNAMO_PORT = 4569;

    public static String getEndpointKinesis() {
        return endpointForPort(KINESIS_PORT);
    }

    public static String getEndpointDynamo() {
        return endpointForPort(DYNAMO_PORT);
    }


    private static String endpointForPort(int port) {
        if (localStackContainer != null) {
            int externalPort = localStackContainer.getExternalPortFor(port);
            return String.format("http://%s:%s", "127.0.0.1", externalPort);
        }

        throw new RuntimeException("Container not started");
    }


    @Override
    public void run(RunNotifier notifier) {
        LOG.info("BEFORE");

//        String dockerExeLocation = getDockerExeLocation();
//        LOG.info("EXE=" + dockerExeLocation);
//        String containerId = startDocker(dockerExeLocation);
//        List<PortMapping> mappings = getPortMappings(dockerExeLocation, containerId);
//        LOG.info(Arrays.toString(mappings.toArray()));

        //waitForAllPorts("127.0.0.1", mappings);

        localStackContainer = Container.create();

        super.run(notifier);

        localStackContainer.stop();
        LOG.info("AFTER");
    }
}