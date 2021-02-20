import java.util

name := "scala-examples"
organization := "io.growing"
version := "1.0"

//级联编译，无其他关联
lazy val `scala-examples` = Project(id = "scala-examples", base = file(".")).enablePlugins(PlayScala).aggregate(
  `scala-akka-type`, `scala-akka-classic`
)

lazy val `scala-akka-classic` = Project(id = "scala-akka-classic", base = file("scala-akka-classic"))

lazy val `scala-akka-type` = Project(id = "scala-akka-type", base = file("scala-akka-type"))

lazy val `scala-sbt-plugin` = Project(id = "scala-sbt-plugin", base = file("scala-sbt-plugin"))

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
  "com.typesafe.akka" %% "akka-actor" % "2.5.23",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.23",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.3",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.11.3"
)
unmanagedResourceDirectories in Test += {
  baseDirectory(_ / "target/web/public/test").value
}


// graphql client config
enablePlugins(GraphQLCodegenPlugin)
GraphQLCodegenPluginDependencies
graphqlSchemaPaths := List("conf/schema.graphqls")
modelPackageName := Some("io.github.dreamylost.model")
apiPackageName := Some("io.github.dreamylost.api")
generateClient := true
generateApis := true
generatedLanguage := com.kobylynskyi.graphql.codegen.model.GeneratedLanguage.SCALA
generateImmutableModels := true
modelNameSuffix := Some("DO")
customAnnotationsMapping := {
  val mapping = new util.HashMap[String, util.List[String]]
  val annotations = new util.ArrayList[String]()
  annotations.add("@com.fasterxml.jackson.annotation.JsonTypeInfo(use=com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME, include=com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY,property = \"__typename\")")
  annotations.add(
    """@com.fasterxml.jackson.annotation.JsonSubTypes(value = Array(
      |        new com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = classOf[HumanDO], name = "Human"),
      |        new com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = classOf[DroidDO], name = "Droid")))""".stripMargin)
  mapping.put("Character", annotations)
  mapping.put("Droid.appearsIn", util.Arrays.asList("@com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[io.github.dreamylost.model.EpisodeDOTypeRefer])"))
  mapping.put("Human.appearsIn", util.Arrays.asList("@com.fasterxml.jackson.module.scala.JsonScalaEnumeration(classOf[io.github.dreamylost.model.EpisodeDOTypeRefer])"))
  mapping
}
// play的源码文件结构与普通项目不同，需要指定输出目录，同时如果有需要，将其赋予generateCodegenTargetPath以收到sbt类路径管理
outputDir in GraphQLCodegenConfig := crossTarget.value / "src_managed_graphql_scala"
generateCodegenTargetPath in GraphQLCodegenConfig := (outputDir in GraphQLCodegenConfig).value

generateEqualsAndHashCode := true
generateToString := true
// 编译器自动生成文件
(compile in Compile) := ((compile in Compile) dependsOn (graphqlCodegen in GraphQLCodegenConfig)).value