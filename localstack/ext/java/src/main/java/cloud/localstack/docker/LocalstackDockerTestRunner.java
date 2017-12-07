package cloud.localstack.docker;

import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class LocalstackDockerTestRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOG = Logger.getLogger(LocalstackDockerTestRunner.class.getName());


    public static final int API_GATEWAY_PORT = 4567;
    public static final int KINESIS_PORT = 4568;
    public static final int DYNAMO_PORT = 4569;
    public static final int DYNAMO_STREAMS_PORT = 4570;
    public static final int ELASTICSEARCH_PORT = 4571;
    public static final int S3_PORT = 4572;
    public static final int FIREHOSE_PORT = 4573;
    public static final int LAMBDA_PORT = 4574;
    public static final int SNS_PORT = 4575;
    public static final int SQS_PORT = 4576;
    public static final int REDSHIFT_PORT = 4577;
    public static final int ES_PORT = 4578;
    public static final int SES_PORT = 4579;
    public static final int ROUTE53_PORT = 4580;
    public static final int CLOUDFORMATION_PORT = 4581;
    public static final int CLOUDWATCH_PORT = 4582;
    public static final int SSM_PORT = 4583;


    private static final Pattern READY_TOKEN = Pattern.compile("Ready\\.");


    private static Container localStackContainer;

    public static Container getLocalStackContainer() {
        return localStackContainer;
    }


    public LocalstackDockerTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }


    @Override
    public void run(RunNotifier notifier) {
        localStackContainer = Container.create();
        try {

            LOG.info("Waiting for localstack container to be ready...");
            localStackContainer.waitForLogToken(READY_TOKEN);

            super.run(notifier);
        }
        finally {
            localStackContainer.stop();
        }
    }


    public static String getEndpointS3() {
        String s3Endpoint = endpointForPort(S3_PORT);
        /*
         * Use the domain name wildcard *.localhost.atlassian.io which maps to 127.0.0.1
         * We need to do this because S3 SDKs attempt to access a domain <bucket-name>.<service-host-name>
         * which by default would result in <bucket-name>.localhost, but that name cannot be resolved
         * (unless hardcoded in /etc/hosts)
         */
        s3Endpoint = s3Endpoint.replace("localhost", "test.localhost.atlassian.io");
        return s3Endpoint;
    }


    public static String getEndpointKinesis() {
        return endpointForPort(KINESIS_PORT);
    }

    public static String getEndpointLambda() {
        return endpointForPort(LAMBDA_PORT);
    }

    public static String getEndpointDynamoDB() {
        return endpointForPort(DYNAMO_PORT);
    }

    public static String getEndpointDynamoDBStreams() {
        return endpointForPort(DYNAMO_STREAMS_PORT);
    }

    public static String getEndpointAPIGateway() {
        return endpointForPort(API_GATEWAY_PORT);
    }

    public static String getEndpointElasticsearch() {
        return endpointForPort(ELASTICSEARCH_PORT);
    }

    public static String getEndpointElasticsearchService() {
        return endpointForPort(ES_PORT);
    }

    public static String getEndpointFirehose() {
        return endpointForPort(FIREHOSE_PORT);
    }

    public static String getEndpointSNS() {
        return endpointForPort(SNS_PORT);
    }

    public static String getEndpointSQS() {
        return endpointForPort(SQS_PORT);
    }

    public static String getEndpointRedshift() {
        return endpointForPort(REDSHIFT_PORT);
    }

    public static String getEndpointSES() {
        return endpointForPort(SES_PORT);
    }

    public static String getEndpointRoute53() {
        return endpointForPort(ROUTE53_PORT);
    }

    public static String getEndpointCloudFormation() {
        return endpointForPort(CLOUDFORMATION_PORT);
    }

    public static String getEndpointCloudWatch() {
        return endpointForPort(CLOUDWATCH_PORT);
    }

    public static String getEndpointSSM() {
        return endpointForPort(SSM_PORT);
    }


    public static String endpointForPort(int port) {
        if (localStackContainer != null) {
            int externalPort = localStackContainer.getExternalPortFor(port);
            return String.format("http://%s:%s", "localhost", externalPort);
        }

        throw new RuntimeException("Container not started");
    }
}