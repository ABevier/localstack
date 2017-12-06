package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.List;

import cloud.localstack.docker.DockerExe;

public class LogCommand {

    private final DockerExe dockerExe;
    private final String containerId;
    private final Integer numberOfLines;

    public LogCommand(DockerExe dockerExe, String containerId, Integer numberOfLines) {
        this.dockerExe = dockerExe;
        this.containerId = containerId;
        this.numberOfLines = numberOfLines;
    }

    public String execute() {
        List<String> args = new ArrayList<>();
        args.add("logs");
        addNumberOfLines(args);
        args.add(containerId);

        return dockerExe.execute(args);
    }


    private void addNumberOfLines(List<String> args){
        if(numberOfLines != null) {
            args.add("--tail");
            args.add(numberOfLines.toString());
        }
    }
}
