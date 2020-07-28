package java8.play2.example.aws.dummies;

import java8.play2.example.aws.ExampleDynamoDbDaoLayer;
import java8.play2.example.domain.ExampleContentObj2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DummyDefaultDynamoDbDaoLayer extends ExampleDynamoDbDaoLayer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DummyDefaultDynamoDbDaoLayer.class);

    public static Map<Integer, ExampleContentObj2> dynamoEntries = new HashMap<>();

    @Override
    public ExampleContentObj2 retrieveItemDefault(Integer someId) {
        return dynamoEntries.get(someId);
    }

    @Override
    public void save(ExampleContentObj2 exampleContentObj1) {
        LOGGER.info("DummyDefaultDynamoDbDaoLayer save: " + exampleContentObj1);
        dynamoEntries.put(exampleContentObj1.getSomeInt(), exampleContentObj1);
    }
}
