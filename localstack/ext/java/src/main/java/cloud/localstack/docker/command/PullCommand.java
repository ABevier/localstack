package cloud.localstack.docker.command;

import java.util.Arrays;

import cloud.localstack.docker.DockerExe;

public class PullCommand {

    private static final String LATEST_TAG = "latest";

    private final DockerExe dockerExe;
    private final String imageName;

    public PullCommand(DockerExe dockerExe, String imageName) {
        this.dockerExe = dockerExe;
        this.imageName = imageName;
    }


    public void execute() {
        String image = String.format("%s:%s", imageName, LATEST_TAG);
        dockerExe.execute(Arrays.asList("pull", image));
    }
}
