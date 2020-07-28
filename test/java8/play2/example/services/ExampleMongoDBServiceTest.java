package java8.play2.example.services;

import java8.play2.example.domain.ExampleContentObj1;
import java8.play2.example.persistence.ExampleMongoDao;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;
import org.bson.Document;
import com.mongodb.MongoException;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.junit.Assert.assertTrue;

public class ExampleMongoDBServiceTest {

    ExampleMongoDBService exampleMongoDBService;

    @Mock
    ExampleMongoDao exampleMongoDao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        exampleMongoDBService = new ExampleMongoDBService(exampleMongoDao);
    }

    @Test
    public void testSaveEntity() {
        ExampleContentObj1 exampleContentObj1 = new ExampleContentObj1();
        exampleContentObj1.setExampleId(12345);
        exampleContentObj1.setTimeStamp(1234567890L);
        exampleContentObj1.setIsFake("false");

        Document expectedUpdate = new Document();
        expectedUpdate.append("_id", 12345);
        expectedUpdate.append("timestamp", 1234567890L);
        expectedUpdate.append("isFake", "false");

        Bson expectedFilter = Filters.eq("_id", 12345);

        Document actualUpdatedDocument = exampleMongoDBService.saveEntity(exampleContentObj1);

        verify(exampleMongoDao).updateOne(Mockito.refEq(expectedFilter), Mockito.refEq(expectedUpdate));

        assertTrue("Example Id should be correct", actualUpdatedDocument.getInteger("exampleId").equals(12345));
        assertTrue("Timestamp should be correct", actualUpdatedDocument.getLong("timestamp") >= System.currentTimeMillis() - 100);
        assertTrue("IsFake should be correct'", actualUpdatedDocument.getString("isFake").equals("false"));
    }

    @Test
    public void testSaveEntityHandlesFailure() {
        ExampleContentObj1 exampleContentObj1 = new ExampleContentObj1();

        doThrow(new RuntimeException("Example test mongodb error.")).when(exampleMongoDao).updateOne(any(), any());

        Boolean expectedException = false;

        try {
            exampleMongoDBService.saveEntity(exampleContentObj1);
        } catch (RuntimeException e) {
            expectedException = true;
        }

        verify(exampleMongoDao).updateOne(any(), any());
        assertTrue("Expected exception should be thrown", expectedException);
    }
}