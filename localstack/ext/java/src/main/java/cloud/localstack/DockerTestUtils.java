package cloud.localstack;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;

import cloud.localstack.docker.LocalstackDockerTestRunner;

public class DockerTestUtils {

    public static AmazonKinesis getClientKinesis() {
        return AmazonKinesisClientBuilder.standard().
                withEndpointConfiguration(getEndpointConfigurationKinesis()).
                withCredentials(TestUtils.getCredentialsProvider()).build();
    }


    protected static AwsClientBuilder.EndpointConfiguration getEndpointConfigurationKinesis() {
        return TestUtils.getEndpointConfiguration(LocalstackDockerTestRunner.getEndpointKinesis());
    }
}
