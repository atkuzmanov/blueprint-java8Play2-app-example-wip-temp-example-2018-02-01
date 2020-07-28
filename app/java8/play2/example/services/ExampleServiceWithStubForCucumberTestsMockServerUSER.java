package java8.play2.example.services;

import com.google.inject.Inject;

public class ExampleServiceWithStubForCucumberTestsMockServerUSER {

    private final ExampleServiceWithStubForCucumberTestsMockServer exampleServiceWithStubForCucumberTestsMockServer;

    @Inject
    public ExampleServiceWithStubForCucumberTestsMockServerUSER(ExampleServiceWithStubForCucumberTestsMockServer exampleServiceWithStubForCucumberTestsMockServer) {
        this.exampleServiceWithStubForCucumberTestsMockServer = exampleServiceWithStubForCucumberTestsMockServer;
    }

    public String getSomeDefaultExampleString() {
        return exampleServiceWithStubForCucumberTestsMockServer.getSomeExampleString();
    }
}
