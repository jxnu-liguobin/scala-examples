logLevel := Level.Warn
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.7.0")

addSbtPlugin("io.github.jxnu-liguobin" % "graphql-codegen-sbt-plugin" % "4.1.0")
