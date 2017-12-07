package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.List;

public class LogCommand extends Command {

    private final String containerId;

    public LogCommand(String containerId) {
        this.containerId = containerId;
    }


    public String execute() {
        List<String> args = new ArrayList<>();
        args.add("logs");
        args.addAll(flags);
        args.add(containerId);

        return dockerExe.execute(args);
    }


    public LogCommand withNumberOfLines(Integer numberOfLines){
        this.addFlags("--tail", numberOfLines.toString());
        return this;
    }
}
