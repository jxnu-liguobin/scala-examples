
name := "scala-akka"

version := "0.1"

scalaVersion := "2.12.8"

organization := "io.growing"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.lihaoyi" %% "requests" % "0.2.0",
  "commons-codec" % "commons-codec" % "1.10",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-stream" % "2.5.23", // http需要,或任何更新的版本
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.10" //http json支持
)
