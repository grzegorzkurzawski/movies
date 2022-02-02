name := "approving-reviews"
scalaVersion := "2.13.8"
organization := "pl.kurzawski"

val akkaVersion = "2.6.18"
val slickVersion = "3.3.3"

lazy val root = project in file(".")

ThisBuild / scalacOptions ++= Seq("-unchecked", "-deprecation")

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "ch.qos.logback" % "logback-classic" % "1.2.10",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.newmotion" %% "akka-rabbitmq" % "6.0.0",

  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.github.tminglei" %% "slick-pg" % "0.20.2"
)

enablePlugins(JavaServerAppPackaging)