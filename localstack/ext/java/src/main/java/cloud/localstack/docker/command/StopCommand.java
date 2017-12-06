package cloud.localstack.docker.command;

import java.util.Arrays;

import cloud.localstack.docker.DockerExe;

public class StopCommand {

    private final DockerExe dockerExe;
    private final String containerId;

    public StopCommand(DockerExe dockerExe, String containerId) {
        this.dockerExe = dockerExe;
        this.containerId = containerId;
    }

    public void execute() {
        dockerExe.execute(Arrays.asList("stop", containerId));
    }
}
