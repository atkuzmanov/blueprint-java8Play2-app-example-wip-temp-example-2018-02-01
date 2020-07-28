package java8.play2.example.persistence;

import java8.play2.example.monitoring.ExampleJavaStatsD;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.bson.conversions.Bson;
import org.bson.Document;

import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

public class ExampleMongoDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExampleMongoDao.class);

    private ExampleJavaStatsD statsDClient;

    private MongoCollection<Document> exampleMongoDBCollection;

    public ExampleMongoDao() {
        MongoClient mongoDBClient = ExampleMongoDBClientFactory.createMongoClient();
        MongoDatabase exampleMongoDBDatabase = mongoDBClient.getDatabase("example-collection-name");
        this.exampleMongoDBCollection = exampleMongoDBDatabase.getCollection("statuses");

        this.statsDClient = new ExampleJavaStatsD();
    }

    public void insertOne(Document bsonDocument) {
        LOGGER.debug("Creating document: " + bsonDocument);

        statsDClient.exampleIncrementMongoWrite();

        try {
            exampleMongoDBCollection.insertOne(bsonDocument);
        } catch (Exception e) {
            exampleHandleExceptionCommonCode("insertOne", e);
        }
    }

    public void updateOne(Bson bsonFilter, Document bsonDocument) {
        LOGGER.debug("Updating document: " + bsonDocument);

        statsDClient.exampleIncrementMongoWrite();

        try {
            Document exampleUpdatedDocument = new Document("$set", bsonDocument);
            UpdateOptions exampleUpdateOptions = new UpdateOptions().upsert(true);
            exampleMongoDBCollection.updateOne(bsonFilter, exampleUpdatedDocument, exampleUpdateOptions);
        } catch (Exception e) {
            exampleHandleExceptionCommonCode("updateOne", e);
        }
    }

    private void exampleHandleExceptionCommonCode(String nameOfFunction, Exception ex) {
        String errorMessage = "Failed executing function " + nameOfFunction + "(...): ";
        LOGGER.error(errorMessage, ex);
        statsDClient.incrementMongoError(nameOfFunction);
        throw new MongoException(errorMessage + ex.getMessage(), ex);
    }
}
