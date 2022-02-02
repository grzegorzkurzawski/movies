name := "movies-management"
scalaVersion := "2.13.8"
organization := "pl.kurzawski"

val akkaVersion = "2.6.18"
val akkaHttpVersion = "10.2.7"
val slickVersion = "3.3.3"

lazy val root = project in file(".")

ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.10",

  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.github.tminglei" %% "slick-pg" % "0.20.2",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.5.0",
  "joda-time" % "joda-time" % "2.10.13",
  "org.joda" % "joda-convert" % "2.2.2",
  "com.newmotion" %% "akka-rabbitmq" % "6.0.0",

  "org.scalatest" %% "scalatest" % "3.2.11" % Test,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "org.mockito" %% "mockito-scala" % "1.17.0" % Test,
  "org.testcontainers" % "postgresql" % "1.16.3" % Test
)

enablePlugins(JavaServerAppPackaging)