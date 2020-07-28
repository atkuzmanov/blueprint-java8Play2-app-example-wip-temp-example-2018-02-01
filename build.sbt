import sbt._
import sbt.Keys._

name := """default-example-name-of-application"""

lazy val root = (project in file(".")).enablePlugins(PlayJava)

version := Option(System.getenv("DEFAULT_EXAMPLE_VERSION_BUILD_NUMBER")) getOrElse "DEV"

val manifestPattern = "(META-INF/MANIFEST*)".r
val playPattern = "(play/core/server/.*)".r
val springPattern = "(META-INF/.*)".r
val loggingPattern = "(org/apache/commons/logging/.*)".r

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "org.elasticmq" %% "elasticmq-rest-sqs" % "0.8.12",
  "com.amazonaws" % "aws-java-sdk" % "1.10.23",
  "org.scribe" % "scribe" % "1.3.6",
  "com.timgroup" % "java-statsd-client" % "3.0.1",
  "xml-apis" % "xml-apis" % "1.4.01",

  //---- Testing ----//
  "org.mockito" % "mockito-core" % "1.10.19",
  "org.powermock" % "powermock-mockito-release-full" % "1.6.3" % "test",
  "info.cukes" % "cucumber-java8" % "1.2.3",
  "com.github.tomakehurst" % "wiremock" % "1.57" ,
  "junit" % "junit" % "4.12",
  "info.cukes" % "cucumber-junit" % "1.2.3"
)

assemblyJarName in assembly := "default-example-name-of-application.jar"

mainClass in assembly := Some("play.core.server.NettyServer")

test in assembly := {}

assemblyMergeStrategy in assembly := {
  case loggingPattern(_) | playPattern(_) =>  MergeStrategy.first
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList("javax", "annotation", xs @ _*)      => MergeStrategy.first
  case "application.conf"  =>  MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

// There are two styles of routers in the Play Framework, the first expects it's actions to be injected and the second, the legacy one, expects it's actions to be accessed statically.

routesGenerator := InjectedRoutesGenerator