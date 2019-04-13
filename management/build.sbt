import scala.sys.process._

name := "movies-management"
scalaVersion := "2.12.7"
organization := "pl.kurzawski"

val akkaVersion = "2.5.22"
val akkaHttpVersion = "10.1.0"

lazy val root = project in file(".")

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.github.tminglei" %% "slick-pg" % "0.17.2",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.3.0",
  "joda-time" % "joda-time" % "2.7",
  "org.joda" % "joda-convert" % "1.7",

  "org.scalatest" %% "scalatest" % "3.0.7" % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.mockito" %% "mockito-scala" % "1.0.0" % Test
)

val startPostgres = TaskKey[Unit]("start-postgres", "Start a local PostgreSQL docker instance")
(startPostgres in Test) := {
  println("Starting postgres docker container.")
  s"./scripts/start_postgres.sh" !
}

(test in Test) := (test in Test)
  .dependsOn(startPostgres in Test)
  .andFinally {
    s"./scripts/stop_postgres.sh" !
  }
  .value

enablePlugins(JavaServerAppPackaging)