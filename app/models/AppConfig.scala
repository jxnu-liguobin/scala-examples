package models

import java.net.URI

import com.typesafe.config.Config
import play.api.ConfigLoader
import play.api.libs.json.{Json, Writes}

/**
 * 将配置转换为自定义类型
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-27
 */
case class AppConfig(title: String, baseUri: URI)

object AppConfig {


    //不行，使用写转化器
    //从json到model使用Reads
    //implicit val appConfigFormat = Json.format[AppConfig]
    implicit val appConfigWrites = new Writes[AppConfig] {
        def writes(appConfig: AppConfig) = Json.obj(
            "title" -> appConfig.title,
            "baseUri" -> appConfig.baseUri.toString
        )
    }


    implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
        val config = rootConfig.getConfig(path)
        AppConfig(
            title = config.getString("title"),
            baseUri = new URI(config.getString("baseUri"))
        )
    }
}