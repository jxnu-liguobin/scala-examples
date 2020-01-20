name := "scala-examples"
organization := "io.growing"
version := "1.0"

//级联编译，无其他关联
lazy val `scala-examples` = Project(id = "scala-examples", base = file(".")).enablePlugins(PlayScala).aggregate(
  `scala-akka-type`, `scala-akka-classic`
)

lazy val `scala-akka-classic` = Project(id = "scala-akka-classic", base = file("scala-akka-classic"))

lazy val `scala-akka-type` = Project(id = "scala-akka-type", base = file("scala-akka-type"))


lazy val `scala-other` = Project(id = "scala-other", base = file("scala-other"))

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
scalaVersion := "2.12.8"
//使用slick时删除jdbc
libraryDependencies ++= Seq(ehcache, ws, specs2 % Test, guice,
  "commons-io" % "commons-io" % "2.3",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "mysql" % "mysql-connector-java" % "5.1.47",
  "com.typesafe.play" %% "play-slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0",
  "org.postgresql" % "postgresql" % "9.4.1212",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.typesafe.akka" %% "akka-http" % "10.1.10",
  "com.typesafe.akka" %% "akka-actor" % "2.5.23"
)
unmanagedResourceDirectories in Test += {
  baseDirectory(_ / "target/web/public/test").value
}



      