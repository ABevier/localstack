package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.List;

public class RunCommand extends Command {

    private final String imageName;

    public RunCommand(String imageName) {
        this.imageName = imageName;
    }


    public String execute() {
        List<String> args = new ArrayList<>();
        args.add("run");
        args.add("-d");
        args.addAll(flags);
        args.add(imageName);

        return dockerExe.execute(args);
    }


    public RunCommand withExposedPorts(String portsToExpose) {
        addFlags("-p", ":" + portsToExpose);
        return this;
    }
}
