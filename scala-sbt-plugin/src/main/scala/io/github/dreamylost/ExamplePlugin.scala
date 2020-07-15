package io.github.dreamylost

import sbt._

/**
 * 一个sbt 插件编写的例子，记录踩坑
 *
 * @author 梦境迷离 dreamylost
 * @since 2020-07-15
 * @version v1.0
 */
object ExamplePlugin extends AutoPlugin {

  //依赖JVM插件
  override def requires = plugins.JvmPlugin

  //任何需要的时候都会触发
  override def trigger = allRequirements

  //配置setting key和task，一般使用autoImport
  object autoImport extends ExampleKeys

  import autoImport._

  override lazy val globalSettings = Seq(
    exampleTaskValueOption := None,
    //非Option的，也不能为null
    exampleTaskValueMust := "default"
  )

  /**
   * 使用，在plugins.sbt 添加 addSbtPlugin("io.github.dreamylost" % "scala-sbt-plugin" % "0.1-SNAPSHOT")
   *
   * 执行 sbt exampleTask
   * 输出 List(None, Some(default))
   *
   * 在build.sbt中配置 setting
   *
   * exampleTaskValueMust := "not default"
   * exampleTaskValueOption := Some("not default")
   *
   * 配置后，执行 sbt exampleTask
   * 输出 List(Some(not default), Some(not default))
   */
  override lazy val projectSettings = Seq(
    //必须在globalSettings声明初始化需要用的 setting key，否则在exampleTask里面拿不到值
    exampleTask := {
      val valueOption = exampleTaskValueOption.value
      val valueMust = exampleTaskValueMust.value
      println(Seq(valueOption, Option(valueMust)))
    }
  )

}
