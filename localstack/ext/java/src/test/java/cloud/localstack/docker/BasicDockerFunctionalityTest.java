package cloud.localstack.docker;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ListStreamsResult;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.TestUtils;

@RunWith(LocalstackDockerTestRunner.class)
public class BasicDockerFunctionalityTest {

    static {
        TestUtils.setEnv("AWS_CBOR_DISABLE", "1");
    }

    @Before
    public void setup() {
        Container container = LocalstackDockerTestRunner.getLocalStackContainer();
        container.waitForAllPorts();
    }


    @Test
    public void test1() {
        String endpoint = LocalstackDockerTestRunner.getEndpointDynamo();

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(TestUtils.getCredentialsProvider())
                .build();

        System.out.println("Test 1 READY - endpoint = " + endpoint);

        ListTablesResult tablesResult = dynamoDB.listTables();
        System.out.println("Dynamo -" + Arrays.toString(tablesResult.getTableNames().toArray()));

        AmazonKinesis kinesis = DockerTestUtils.getClientKinesis();
        ListStreamsResult streams = kinesis.listStreams();
        System.out.println("Kinesis -" + Arrays.toString(streams.getStreamNames().toArray()));
    }

    @Test
    public void test2() {
        System.out.println("2");
        int result = 3 + 3;
        assertEquals(6, result);
    }

    @Test
    public void test3() {
        System.out.println("3");
        int result = 4 + 4;
        assertEquals(8, result);
    }
}
