package java8.play2.example.services;

import java8.play2.example.monitoring.ExampleJavaStatsD;
import java8.play2.example.utilities.ExampleUtils;

import com.google.inject.Inject;

public class ExampleServiceWithStubForCucumberTestsMockServerImpl implements ExampleServiceWithStubForCucumberTestsMockServer {
    private ExampleJavaStatsD statsDClient;

    @Inject
    public ExampleServiceWithStubForCucumberTestsMockServerImpl() {
        this.statsDClient = new ExampleJavaStatsD();
    }

    public String getSomeExampleString() {
        String someExampleString = "";
        try {
            someExampleString =
                    String.format("insert-in-string-here-%s-string-text-after-insert",
                            ExampleUtils.exampleGetSystemProp("some prop key name"));

        } catch (Exception e) {
            statsDClient.exampleIncrementDefaultErrorCounter();
        }
        return someExampleString;
    }
}
