package java8.play2.example.aws;

import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.monitoring.ExampleJavaStatsD;
import java8.play2.example.utilities.ExampleEnvConfiguration;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.*;

import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;

import org.apache.commons.lang.StringUtils;

public class ExampleDynamoDbDaoLayer {
    private static final Logger LOG = LoggerFactory.getLogger(ExampleDynamoDbDaoLayer.class);

    private final AmazonDynamoDBClient defaultExampleAwsDynamoDBClient;
    private final ExampleAwsCredentialsConfigurator defaultAwsCredentialsConfigurator = new ExampleAwsCredentialsConfigurator();

    private final DynamoDBMapperConfig awsDynamoDBMapConfig;
    private final DynamoDBMapper defaultAwsDynamoDBMapper;

    private final String dynamoDBTableName;
    private String systemRunningEnv;

    private ExampleJavaStatsD statsDClient;

    public ExampleDynamoDbDaoLayer() {
        this.defaultExampleAwsDynamoDBClient = new AmazonDynamoDBClient(defaultAwsCredentialsConfigurator.awsCredentialsProvider, defaultAwsCredentialsConfigurator.awsClientConfiguration);
        this.defaultExampleAwsDynamoDBClient.setRegion(Region.getRegion(Regions.EU_WEST_1));
        this.defaultAwsDynamoDBMapper = new DynamoDBMapper(this.defaultExampleAwsDynamoDBClient);
        this.dynamoDBTableName = defaultGetDynamoDBTableName();
        this.awsDynamoDBMapConfig = new DynamoDBMapperConfig(new DynamoDBMapperConfig.TableNameOverride(dynamoDBTableName));
        systemRunningEnv = ExampleEnvConfiguration.systemRunningEnvironment();

        statsDClient = new ExampleJavaStatsD();
    }

    private String addProdLiveSuffix(String name) {
        String systemEnv = ExampleEnvConfiguration.systemRunningEnvironment();
        if (systemEnv != null && systemEnv.equals("production")) {
            name = name + ".liveProductionEnv";
        }
        return name;
    }

    private String defaultGetDynamoDBTableName() {
        String dynamoDBTableNameFromCloudConfig = addProdLiveSuffix("default.example.dynamo.tablename");
        String dynamoDBTableName = System.getProperty(dynamoDBTableNameFromCloudConfig);
        if (StringUtils.isBlank(dynamoDBTableName)) {
            LOG.error("DynamoDB table name is empty for key " + dynamoDBTableNameFromCloudConfig);
            try {
                Config config = ConfigFactory.load(systemRunningEnv + ".config");
                dynamoDBTableName = config.getString(dynamoDBTableNameFromCloudConfig);
            } catch (Exception e) {
                LOG.error("Error occured while loading local config for environment: " + systemRunningEnv + ".config");
            }
            return dynamoDBTableName;
        }
        LOG.info("DynamoDB table name: " + dynamoDBTableName);
        return dynamoDBTableName;
    }

    public void save(ExampleContentObj2 exampleContentObj1) {
        try {
            this.defaultAwsDynamoDBMapper.save(exampleContentObj1, this.awsDynamoDBMapConfig);
        } catch (ProvisionedThroughputExceededException e) {

            statsDClient.exampleIncrementDynamoDBWriteProvisionCount();

            LOG.error("DynamoDB ProvisionedThroughputExceededException writes: ", e);
        } catch (Exception e) {

            statsDClient.exampleIncrementDynamoDBWriteErrorCount();

            LOG.error("Exception occurred while writing data то DynamoDb: ", e);
        }

        statsDClient.exampleIncrementDynamoDBWriteCount();
    }

    public List<ExampleContentObj2> retrieveItemDefaultsSinceDate(Date dateTimeStamp) {
        List<ExampleContentObj2> list = Collections.emptyList();

        long start = System.currentTimeMillis();
        try {
            Map<String, AttributeValue> expAttrVals = new HashMap<>();
            expAttrVals.put(":val1", new AttributeValue().withN(Long.toString(dateTimeStamp.getTime())));

            DynamoDBScanExpression exampleDynamoScanExp = new DynamoDBScanExpression()
                    .withFilterExpression("modifiedTime > :val1")
                    .withExpressionAttributeValues(expAttrVals);

            PaginatedScanList<ExampleContentObj2> paginatedScanList = defaultAwsDynamoDBMapper.scan(ExampleContentObj2.class, exampleDynamoScanExp, this.awsDynamoDBMapConfig);

            // Forcing the DB client to eagerly load all data from DynamoDB.
            paginatedScanList.loadAllResults();

            list = new ArrayList<>(paginatedScanList.size());
            for (ExampleContentObj2 element : paginatedScanList) {
                list.add(element);
            }
        } catch (ProvisionedThroughputExceededException ex) {
            LOG.error("DynamoDB ProvisionedThroughputExceededException reads: ", ex);
        } catch (Exception e) {
            LOG.error("Exception occurred while trying to readNumberOfSQSMsgs data: ", e);
        }

        long time = System.currentTimeMillis() - start;

        statsDClient.exampleRecordDynamoDBTableScanTime(time);
        statsDClient.exampleIncrementDynamoDBReadSinceCount();

        LOG.info("Retrieved " + list.size() + " objects from DynamoDB in " + time + "ms.");
        return list;
    }

    public ExampleContentObj2 retrieveItemDefault(Integer dynamoObjectId) {
        ExampleContentObj2 exampleDynamoDBContObj = null;
        try {
            Map<String, AttributeValue> queryAttributesValues = new HashMap<>();
            queryAttributesValues.put(":defaultVal1", new AttributeValue().withN(Integer.toString(dynamoObjectId)));

            DynamoDBQueryExpression<ExampleContentObj2> dynamoQueryExpression = new DynamoDBQueryExpression<ExampleContentObj2>()
                    .withKeyConditionExpression("dynamoObjectId = :defaultVal1")
                    .withExpressionAttributeValues(queryAttributesValues);

            List<ExampleContentObj2> responses = defaultAwsDynamoDBMapper.query(ExampleContentObj2.class, dynamoQueryExpression, this.awsDynamoDBMapConfig);
            if (responses.size() == 0) {
                return null;
            }
            exampleDynamoDBContObj = responses.get(0);
        } catch (ProvisionedThroughputExceededException e) {
            statsDClient.exampleIncrementDynamoDBReadProvisionCount();
            LOG.error("DynamoDB ProvisionedThroughputExceededException reads: ", e);
        } catch (Exception ex) {
            statsDClient.exampleIncrementDynamoDBReadErrorCount();
            LOG.error("Exception occurred while trying to readNumberOfSQSMsgs data: ", ex);
        }
        statsDClient.exampleIncrementDynamoDBSingleReadCount();
        return exampleDynamoDBContObj;
    }
}
