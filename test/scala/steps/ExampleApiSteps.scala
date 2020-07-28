package steps

import com.github.tomakehurst.wiremock.WireMockServer
import org.scalatest.MustMatchers
import cucumber.api.scala.{EN, ScalaDsl}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import play.api.test.TestServer
import play.api.libs.ws.WSResponse
import java.util.concurrent.Executors
import java8.play2.example.utilities.ExampleEnvConfiguration

trait ExampleApiSteps extends ScalaDsl with EN with ExampleTestEnv with MustMatchers {
  implicit val executionContext = ExecutionContext fromExecutorService Executors.newSingleThreadExecutor

  def response_=(response: WSResponse) = ExampleResponseHolder.response = response

  def response = ExampleResponseHolder.response


  def complete(future: Future[WSResponse]): Unit = response = Await.result(future, 20.seconds)

  ExampleApiTestServer
}

object ExampleResponseHolder {
  var response: WSResponse = _
}

trait ExampleTestEnv {
  val baseUrl = "http://localhost:9063"
}

object ExampleMockServer {
  val port = ExampleEnvConfiguration.exampleStubServerPort
  val mockServer = new WireMockServer(port)
}

object ExampleApiTestServer {
  implicit val executionContext = scala.concurrent.ExecutionContext.Implicits.global

  private val port = 9063
  private val server = new TestServer(port)

  server.start()
}


