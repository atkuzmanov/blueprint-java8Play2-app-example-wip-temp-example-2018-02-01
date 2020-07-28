package java8.play2.example.services;

import java.io.IOException;

import com.amazonaws.util.IOUtils;

public class ExampleServiceWithStubForCucumberTestsMockServerImplSTUB implements ExampleServiceWithStubForCucumberTestsMockServer {
    public String getSomeExampleString() {
        ClassLoader classLoader = getClass().getClassLoader();
        String someString = null;
        try {
            someString = IOUtils.toString(classLoader.getResourceAsStream("name-of-example-file.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return someString;
    }
}
