package java8.play2.example.services;

import java8.play2.example.aws.ExampleCloudWatchObserver;
import java8.play2.example.domain.ExampleContentObj2;
import java8.play2.example.domain.ExampleContentObj3;
import java8.play2.example.parsers.ExamplePlay2FrameworkJsonMapper;

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.Config;
import com.google.inject.Inject;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import play.libs.ws.WSRequest;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.libs.F;

public class ExampleServiceApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleServiceApi.class);

    private final Config config = ConfigFactory.load();

    private final ExampleCloudWatchObserver defaultCloudWatchObserver;

    @Inject
    public ExampleServiceApi(ExampleCloudWatchObserver defaultCloudWatchObserver) {
        this.defaultCloudWatchObserver = defaultCloudWatchObserver;
    }

    private WSRequest createRestRequest(String requestUrl) {
        return WS.url(requestUrl)
                .setHeader("User-Agent", "default-example-user-agent")
                .setHeader("Cache-Control", "no-cache")
                .setHeader("Accept", "application/json")
                .setRequestTimeout(config.getInt("default.example.ws.request.timeout"));
    }

    public String makeHTTPCallToGetJsonResponse(String requestUrl) {
        F.Promise<String> promise = createRestRequest(requestUrl).get().
                map(response -> {
                    monitorStatus(response);
                    return response.getBody();
                });

        return promise.get(config.getInt("default.example.ws.request.timeout"));
    }

    private void monitorStatus(WSResponse response) {
        if (response.getStatus() == 200) {
            defaultCloudWatchObserver.alarmOn("RestApiNotResponding");
        } else {
            defaultCloudWatchObserver.alarmOff("RestApiNotResponding");
            LOGGER.warn("Error with rest-api request for uri: " + response.getUri() + " the status code was:" + response.getStatus() + " " + response.getStatusText());
        }
    }

    public ExampleContentObj2 createSomeExampleContentObj1(String jsonAsString) {
        ExampleContentObj2 someExampleContentObj1 = null;
        try {
            ExamplePlay2FrameworkJsonMapper mapper = new ExamplePlay2FrameworkJsonMapper();
            ExampleContentObj3 exampleContentObj2 = (ExampleContentObj3) mapper.read(jsonAsString, ExampleContentObj3.class);
        } catch (Exception ex) {
            defaultCloudWatchObserver.alarmOff("ExampleContentObj2 parsing failed.");
            LOGGER.error("Exception encountered while parsing ExampleContentObj2 jsonAsString: " + jsonAsString + " ", ex);
        }
        return someExampleContentObj1;
    }
}

