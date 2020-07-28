package java8.play2.example.persistence

import java8.play2.example.utilities.ExampleEnvConfiguration

import com.mongodb._
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success, Try}

object ExampleMongoDBClientFactory {
  private val LOGGER: Logger = LoggerFactory.getLogger(this.getClass)
  final val exampleConnectionErrorMsg = "Invalid parameter: must be a list of IP addresses or a valid MongoDB connection URL."

  def createMongoClient(): MongoClient = {
    val environment = ExampleEnvConfiguration.systemRunningEnvironment()

    environment match {
        // the "management" environment is for when running on Jenkins
      case "development" | "management" =>
        new MongoClient("127.0.0.1")
        LOGGER.info("Created MongoDB client locally: 127.0.0.1")
      case _ =>
        val sslConnectionString = addSSLOption(ExampleEnvConfiguration.getDefaultMongoDBConnectionUrl)
        Try(new MongoClientURI(sslConnectionString)) match {
          case Success(clientUri) =>
            new MongoClient(clientUri)
            LOGGER.info(s"Created MongoDB client: ($sslConnectionString)")
          case Failure(e) =>
            val message = s"MongoDB client creation failed: $exampleConnectionErrorMsg : ($sslConnectionString)"
            LOGGER.error(message, e)
            throw new Exception(message, e)
        }
    }
  }

  private def addSSLOption(url: String): String = (if (url.contains("?")) {
    url + "&"
  } else {
    url + "?"
  }) + "ssl=true"

  def obfuscatePasswordFromConnectionString(connectionURL: String): String = {
    connectionURL.replaceFirst(":[^/]+@", ":<hidden>@")
  }
}
