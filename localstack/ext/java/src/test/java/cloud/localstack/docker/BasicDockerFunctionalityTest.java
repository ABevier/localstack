package cloud.localstack.docker;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.CreateStreamResult;
import com.amazonaws.services.kinesis.model.ListStreamsResult;

import cloud.localstack.DockerTestUtils;
import cloud.localstack.TestUtils;

@RunWith(LocalstackDockerTestRunner.class)
public class BasicDockerFunctionalityTest {

    static {
        TestUtils.setEnv("AWS_CBOR_DISABLE", "1");
    }


    @Test
    public void testKinesis() throws Exception {
        AmazonKinesis kinesis = DockerTestUtils.getClientKinesis();

        ListStreamsResult streamsResult = kinesis.listStreams();
        assertThat(streamsResult.getStreamNames().size(), is(0));

        CreateStreamRequest createStreamRequest = new CreateStreamRequest()
                .withStreamName("test-stream")
                .withShardCount(2);

        kinesis.createStream(createStreamRequest);

        streamsResult = kinesis.listStreams();
        assertThat(streamsResult.getStreamNames(), hasItem("test-stream"));
    }


    @Test
    public void testDynamo() throws Exception {
        String endpoint = LocalstackDockerTestRunner.getEndpointDynamo();

        AmazonDynamoDB dynamoDB = AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, null))
                .withCredentials(TestUtils.getCredentialsProvider())
                .build();

        ListTablesResult tablesResult = dynamoDB.listTables();
        assertThat(tablesResult.getTableNames().size(), is(0));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName("test.table")
                .withKeySchema(new KeySchemaElement("identifier", KeyType.HASH))
                .withAttributeDefinitions(new AttributeDefinition("identifier", ScalarAttributeType.S))
                .withProvisionedThroughput(new ProvisionedThroughput(10L, 10L));
        dynamoDB.createTable(createTableRequest);

        tablesResult = dynamoDB.listTables();
        assertThat(tablesResult.getTableNames(), hasItem("test.table"));
    }


    @Test
    public void testS3() throws Exception {
        throw new Exception("not implemented yet");
    }
}
