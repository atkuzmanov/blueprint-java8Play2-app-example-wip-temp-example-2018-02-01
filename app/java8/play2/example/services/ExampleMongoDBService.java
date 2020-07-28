package java8.play2.example.services;

import java8.play2.example.persistence.ExampleMongoDao;
import java8.play2.example.domain.ExampleContentObj1;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.bson.conversions.Bson;
import org.bson.Document;
import com.mongodb.client.model.Filters;
import com.mongodb.MongoException;

import com.google.inject.Inject;

public class ExampleMongoDBService {

    private final Logger LOGGER = LoggerFactory.getLogger(ExampleMongoDBService.class);

    private static final String ID_KEY = "_id";
    private static final String MONGO_DUPLICATE_KEY_ERROR_CODE = "E11000";

    private ExampleMongoDao exampleMongoDao;

    @Inject
    ExampleMongoDBService(ExampleMongoDao mongoDao) {
        this.exampleMongoDao = mongoDao;
    }

    public void saveEntity(ExampleContentObj1 recordToSave) {
        Integer exampleId = recordToSave.getExampleId();

        exampleSaveEmptyObjInMongoDB(exampleId);

        recordToSave.setTimeStamp(System.currentTimeMillis());
        recordToSave.setIsFake("false");


        Bson exampleFilter = Filters.eq(ID_KEY, exampleId);
        Document exampleUpdateQuery = exampleUpdateWithNewInfo(recordToSave);
        exampleMongoDao.updateOne(exampleFilter, exampleUpdateQuery);

        LOGGER.info("Saved updated entity: " + exampleId);
    }

    private void exampleSaveEmptyObjInMongoDB(Integer someId) {
        try {
            exampleMongoDao.insertOne(exampleConvertContentObjToDocument(createEmptyExampleContentObj(someId)));
        } catch (MongoException e) {
            if (e.getMessage().contains(MONGO_DUPLICATE_KEY_ERROR_CODE)) {
                LOGGER.info("Record with id: (" + someId + ") already exists. Skipping creation.");
            } else {
                throw new MongoException("Creation of record failed: " + e.getMessage(), e);
            }
        }
    }

    private ExampleContentObj1 createEmptyExampleContentObj(Integer someId) {
        ExampleContentObj1 emptyDefaultExampleContentObj1 = new ExampleContentObj1();
        emptyDefaultExampleContentObj1.setExampleId(someId);
        emptyDefaultExampleContentObj1.setTimeStamp(System.currentTimeMillis());
        emptyDefaultExampleContentObj1.setIsFake("false");
        return  emptyDefaultExampleContentObj1;
    }

    private Document exampleConvertContentObjToDocument(ExampleContentObj1 exampleContentObj1) {
        Document convertedContentObjDocument = new Document();
        convertedContentObjDocument.append(ID_KEY, exampleContentObj1.getExampleId());
        convertedContentObjDocument.append("timestamp", exampleContentObj1.getTimeStamp());
        convertedContentObjDocument.append("isFake", exampleContentObj1.getIsFake());
        return convertedContentObjDocument;
    }

    private Document exampleUpdateWithNewInfo(ExampleContentObj1 status) {
        Document updatedWithNewInfo = new Document();
        updatedWithNewInfo.append(ID_KEY, status.getExampleId());
        updatedWithNewInfo.append("timestamp", status.getTimeStamp());
        updatedWithNewInfo.append("isFake", status.getIsFake());
        return updatedWithNewInfo;
    }
}
