package scala.steps

import java8.play2.example.aws.dummies.{DummyDefaultDynamoDbDaoLayer, DummyExampleAWSSQSClient}
import java8.play2.example.persistence.ExampleMongoDBClientFactory

import com.github.tomakehurst.wiremock.client.{MappingBuilder, UrlMatchingStrategy, WireMock}
import com.github.tomakehurst.wiremock.client.WireMock._
import _root_.steps.ExampleMockServer._
import _root_.steps.ExampleApiSteps
import play.api.libs.json.JsValue
import play.api.libs.json.Json._

class ExampleDefaultSteps extends ExampleApiSteps with ExampleTestingUtilities {

  val mongoClient = ExampleMongoDBClientFactory.createMongoClient()
  val exampleDatabase = mongoClient.getDatabase("example-db-name")
  val exampleCollection = exampleDatabase.getCollection("example-collection-name")
  val fiveMinutes = 300000

  val testSQSMessage =
    """
      |{
      |  "Type": "Notification",
      |  "MessageId": "qwertyuio-asdf-ghjk-lzxc-vbnmqwertyui",
      |  "TopicArn": "arn:aws:sns:eu-west-1:123456789012:example-sns-topic-name",
      |  "Message": "{\"example_json_field1\":[],\"example_current_timestamp\":%s,\"example_json_field2\":\"blah2\"}",
      |  "Timestamp": "2015-11-01T19:53:17.961Z",
      |  "SignatureVersion": "1",
      |  "Signature": "[SOME-LONG-EXAMPLE-SIGNATURE-GUIID]",
      |  "SigningCertURL": "https://sns.eu-west-1.amazonaws.com/SimpleNotificationService-[EXAMPLE-GUIID].pem",
      |  "UnsubscribeURL": "https://sns.eu-west-1.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:eu-west-1:123456789012:example-sns-topic-name"
      |}
    """.format(System.currentTimeMillis()).stripMargin

  Given("""^that MongoDB is empty$""") { () =>
    exampleCollection.drop()
    exampleCollection.count() mustBe 0L
  }

  Given("""^that DynamoDB is empty$""") { () =>
    DummyDefaultDynamoDbDaoLayer.dynamoEntries.clear();
  }

  Given("""^a sqs notification is received$""") { () =>
    val exampleApiResponse = fixtureAsJson("/exampleapiresponses/example-api-response-1.json")
    // For the below line to work further development will be required, to create add() method which will abstract such functionality.
    mockServer.add(HttpGet, ignoringParameters(s"/content/curie/asset:88d2a41b-c110-a043-b79c-6779a6f9347h"), 200, stringify(exampleApiResponse))
    DummyExampleAWSSQSClient.addMessage(testSQSMessage, "name-of-sqs-1");
  }

  When("""^the notification has been processed$""") { () =>
    var maximumWaitingTime = 10000
    val wait = 1000
    while (!DummyExampleAWSSQSClient.isDummySQS2Empty() && maximumWaitingTime != 0) {
      maximumWaitingTime = maximumWaitingTime - wait
      Thread.sleep(wait)
      println("Waiting for sqs to become empty...")
      println(s"Waiting for ${maximumWaitingTime}ms more.")
    }
  }

  When("""^all notifications have been processed$""") { () =>
    var maximumWaitingTime = 10000
    val wait = 1000
    while (!DummyExampleAWSSQSClient.areQueuesEmpty() && maximumWaitingTime != 0) {
      maximumWaitingTime = maximumWaitingTime - wait
      Thread.sleep(wait)
      println("Waiting for sqs to become empty...")
      println(s"Waiting for ${maximumWaitingTime}ms more.")
    }
  }

  Then("""^a new record exists in MongoDB with the right information$""") { () =>
    val testRecordAsJson: JsValue = parse(exampleCollection.find().first().toJson)

    (testRecordAsJson \ "_id").as[Long] mustBe 12345L
    (testRecordAsJson \ "isFake").as[Boolean] mustBe true
    // timestamp must be recent, but not in the future
    (testRecordAsJson \ "timestamp" \ "$numberLong").as[String].toLong <= System.currentTimeMillis() mustBe true
    // timestamp must be newer than last five minutes
    (testRecordAsJson \ "timestamp" \ "$numberLong").as[String].toLong > (System.currentTimeMillis() - fiveMinutes) mustBe true
  }
}
