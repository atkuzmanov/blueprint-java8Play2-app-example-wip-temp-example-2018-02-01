package java8.play2.example.aws;

import java8.play2.example.utilities.ExampleUtils;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.regions.Regions;
import com.amazonaws.regions.Region;

import java.util.Arrays;
import javax.inject.Singleton;

@Singleton
public class ExampleCloudWatchObserver {
    private final Logger log = LoggerFactory.getLogger(ExampleCloudWatchObserver.class);

    private final AmazonCloudWatchClient defaultClientForCloudWatch;
    private final ExampleAwsCredentialsConfigurator defaultAwsCredentialsConfigurator = new ExampleAwsCredentialsConfigurator();

    private static final String AWS_CLOUDWATCH_URL = "monitoring.eu-west-1.amazonaws.com";

    public ExampleCloudWatchObserver() {
        defaultClientForCloudWatch = new AmazonCloudWatchClient(defaultAwsCredentialsConfigurator.awsCredentialsProvider, defaultAwsCredentialsConfigurator.awsClientConfiguration);
        defaultClientForCloudWatch.setEndpoint(AWS_CLOUDWATCH_URL);
        defaultClientForCloudWatch.setRegion(Region.getRegion(Regions.EU_WEST_1));
        log.info("Instantiated ExampleCloudWatchObserver.");
    }

    public void shutdown() {
        try {
            log.info("Shutting down cloudwatch");
            defaultClientForCloudWatch.shutdown();
            log.info("Aws CloudWatch client shutdown.");
        } catch(Exception e) {
            log.warn("Error shutting down AWS CloudWatch client: ", e);
        }
    }

    /**
     * When alarm is below a certain threshold - update the alarm state to OK.
     * @param nameOfAlarm
     */
    public synchronized void alarmOn(String nameOfAlarm){
        putMetrics(nameOfAlarm, 0.01);
        log.info("Alarm is on for " + nameOfAlarm + ".");
    }

    /**
     * When alarm is above a certain threshold - 0.5 - raise an alarm.
     * @param nameOfAlarm
     */
    public synchronized void alarmOff(String nameOfAlarm){
        putMetrics(nameOfAlarm, 1.0);
        log.info("Alarm gone off for " + nameOfAlarm + ".");
    }

    private void putMetrics(String nameOfAlarm, Double value) {
        try{
            PutMetricDataRequest defaultPutMetricDataRequest = new PutMetricDataRequest();
            defaultPutMetricDataRequest.setNamespace("DEFAULT/example-aws-cloudwatch-namespace");

            MetricDatum defaultMetricDatum = new MetricDatum();
            defaultMetricDatum.setUnit(StandardUnit.Count);
            defaultMetricDatum.setValue(value);
            defaultMetricDatum.setMetricName(nameOfAlarm);

            Dimension dimension = new Dimension();
            dimension.setName("Environment");

            dimension.setValue(ExampleUtils.exampleEnviroNameWithFallback(defaultAwsCredentialsConfigurator.getEnvironment()));

            defaultMetricDatum.setDimensions(Arrays.asList(dimension));
            defaultPutMetricDataRequest.getMetricData().add(defaultMetricDatum);
            defaultClientForCloudWatch.putMetricData(defaultPutMetricDataRequest);
        } catch (Exception e) {
            log.info("Putting metrics exception: " + e);
        }
    }

    // Intended as an example:
    public ListMetricsResult defaultListMetricsExample() {
        ListMetricsResult listMetricsResult = defaultClientForCloudWatch.listMetrics(); //defaultClientForCloudWatch.listMetrics(new ListMetricsRequest().withNamespace("DEFAULT/example-aws-cloudwatch-namespace"));
        System.out.println("Metrics namespace: "+ defaultClientForCloudWatch.getServiceName() + " list of metrics " + listMetricsResult.toString());
        return listMetricsResult;
    }
}
