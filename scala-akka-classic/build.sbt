
name := "scala-akka-classic"

version := "0.1"

scalaVersion := "2.12.8"

organization := "io.github.dreamylost"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23", //经典actor
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.8.1-akka-2.5.x"
)
