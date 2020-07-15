package io.github.dreamylost

import sbt._

/**
 *
 * @author 梦境迷离 dreamylost
 * @since 2020-07-15
 * @version v1.0
 */
trait ExampleKeys {

  //key类型和描述
  lazy val exampleTask = taskKey[Unit]("obfuscate the source")
  lazy val exampleTaskValueOption = settingKey[Option[String]]("options to configure obfuscate")
  lazy val exampleTaskValueMust = settingKey[String]("value to configure obfuscate")
  //对于非必须字段，应该都使用Option，然后在globalSettings中的初始化为None（当然，如果不需要嵌套调用setting key，就不需要在globalSettings初始化了）
  //否则在第三方引入插件时，会在编译时错误，提示找不到setting key

}
