name := "scala_play_learn"

version := "1.0"

lazy val `scala_play_learn` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"
scalaVersion := "2.12.2"
//使用slick时删除jdbc
libraryDependencies ++= Seq(ehcache, ws, specs2 % Test, guice,
    "commons-io" % "commons-io" % "2.3",
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
    "mysql" % "mysql-connector-java" % "5.1.47",
    "com.typesafe.play" %% "play-slick" % "3.0.0",
    "com.typesafe.play" %% "play-slick-evolutions" % "3.0.0"

)

unmanagedResourceDirectories in Test += {
    baseDirectory(_ / "target/web/public/test").value
}

      