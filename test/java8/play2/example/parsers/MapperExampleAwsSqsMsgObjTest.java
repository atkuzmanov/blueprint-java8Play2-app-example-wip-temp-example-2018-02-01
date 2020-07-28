package java8.play2.example.parsers;

import java8.play2.example.domain.aws.ExampleAwsSqsMsgObj;
import java8.play2.example.parsers.ExamplePlay2FrameworkJsonMapper;

import org.junit.Test;
import org.junit.Before;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import static org.junit.Assert.assertEquals;

public class MapperExampleAwsSqsMsgObjTest {

    private final String someId = "example-id-123";
    private final String msg = "some example msg";
    private String importedText = null;

    @Before
    public void before() {
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            importedText = IOUtils.toString(classLoader.getResourceAsStream("example_dir/some-example-file.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void parseExampleAwsSqsMsgObj() {
        ExamplePlay2FrameworkJsonMapper mapper = new ExamplePlay2FrameworkJsonMapper();
        ExampleAwsSqsMsgObj exampleAwsSqsMsgObj = (ExampleAwsSqsMsgObj) mapper.read(importedText, ExampleAwsSqsMsgObj.class);

        assertEquals(someId, exampleAwsSqsMsgObj.getSomeId());
        assertEquals(msg, exampleAwsSqsMsgObj.getMsg());
    }

}
