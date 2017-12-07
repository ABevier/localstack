package cloud.localstack.docker.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cloud.localstack.docker.DockerExe;

public abstract class Command {

    protected final DockerExe dockerExe = new DockerExe();

    protected List<String> flags = new ArrayList<>();

    protected void addFlags(String ...items) {
        flags.addAll(Arrays.asList(items));
    }
}
