package cloud.localstack.docker.annotation;

import com.amazonaws.util.EC2MetadataUtils;

public class EC2HostNameResolver implements IHostNameResolver {

    @Override
    public String getHostName() {
        return EC2MetadataUtils.getLocalHostName();
    }

}
