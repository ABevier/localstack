package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cloud.localstack.docker.DockerExe;

public class RunCommand {

    private final DockerExe dockerExe;
    private final String imageName;
    private final String portsToExpose;


    public RunCommand(DockerExe dockerExe, String imageName, String portsToExpose) {
        this.dockerExe = dockerExe;
        this.imageName = imageName;
        this.portsToExpose = portsToExpose;
    }


    public String execute() {
        List<String> args = new ArrayList<>();
        args.add("run");
        args.add("-d");
        addPorts(args);
        args.add(imageName);

        String containerId = dockerExe.execute(args);
        //LOG.info("Created container with id =" + containerId);
        return containerId;
    }

    private void addPorts(List<String> args) {
        if(portsToExpose != null) {
            args.addAll(Arrays.asList("-p", ":" + portsToExpose));
        }
    }
}
