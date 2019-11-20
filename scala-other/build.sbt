
name := "scala-other"

version := "0.1"

scalaVersion := "2.12.8"

organization := "io.growing"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "requests" % "0.2.0",
  "commons-codec" % "commons-codec" % "1.10",
  "com.lmax" % "disruptor" % "3.4.2",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.google.guava" % "guava" % "23.0",
  "org.apache.logging.log4j" %% "log4j-api-scala" % "11.0",
  "org.apache.logging.log4j" % "log4j-api" % "2.11.1",
  "org.apache.logging.log4j" % "log4j-core" % "2.11.1",
  "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.11.1",
  "org.redisson" % "redisson" % "3.10.7",
  "com.typesafe" % "config" % "1.3.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23"
)
