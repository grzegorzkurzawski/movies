name := "approving-reviews"
scalaVersion := "2.12.7"
organization := "pl.kurzawski"

val akkaVersion = "2.5.22"

lazy val root = project in file(".")

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3",

  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  "com.typesafe.slick" %% "slick" % "3.2.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1",
  "com.github.tminglei" %% "slick-pg" % "0.17.2",
)

enablePlugins(JavaServerAppPackaging)