package scala.steps

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable}

import play.api.libs.json.{JsObject, Reads}
import play.api.libs.json.Json._

trait ExampleTestingUtilities {
  def complete[T](awaitable: => Awaitable[T]): T = Await.result(awaitable, 5.seconds)

  def fixture(path: String) = {
    val is = getClass.getResourceAsStream(path)
    io.Source.fromInputStream(is).mkString
  }

  def fixtureAs[T](path: String)(implicit fjs: Reads[T]): T = {
    parse {
      val is = getClass.getResourceAsStream(path)
      io.Source.fromInputStream(is).mkString
    }.as[T]
  }

  def fixtureAsJson(path: String): JsObject = {
    parse {
      val is = getClass.getResourceAsStream(path)
      io.Source.fromInputStream(is).mkString
    }.as[JsObject]
  }
}
