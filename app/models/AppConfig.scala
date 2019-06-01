package models

import java.net.URI

import com.typesafe.config.Config
import play.api.ConfigLoader

/**
 * 将配置转换为自定义类型
 *
 * @author 梦境迷离
 * @version 1.0, 2019-03-27
 */
case class AppConfig(title: String, baseUri: URI)

object AppConfig {

    import play.api.libs.functional.syntax._
    import play.api.libs.json._

    //使用写转化器，写的时候序列化URL
    //基本类型直接使用implicit val appConfigFormat = Json.format[AppConfig]
    implicit val appConfigWrites = new Writes[AppConfig] {
        def writes(appConfig: AppConfig) = Json.obj(
            "title" -> appConfig.title,
            "baseUri" -> appConfig.baseUri.toString
        )
    }

    //从json到model使用Reads
    //读取的String转换为URL
    implicit val appConfigReads: Reads[AppConfig] = (
      (JsPath \ "title").read[String] and
        (JsPath \ "baseUri").read[String].map(x => new URI((x)))
      ) (AppConfig.apply _)

    implicit val configLoader: ConfigLoader[AppConfig] = (rootConfig: Config, path: String) => {
        val config = rootConfig.getConfig(path)
        AppConfig(
            title = config.getString("title"),
            baseUri = new URI(config.getString("baseUri"))
        )
    }

    //    implicit val appConfigReads = new Reads[AppConfig] {
    //        override def reads(json: JsValue): JsResult[AppConfig] = {
    //            val title = (json \ "title").as[String]
    //            val url = (json \ "baseUri").as[String]
    //            AppConfig(title, new URI((url)))
    //        }
    //    }
    //    implicit val appConfigWrites: Writes[AppConfig] = (
    //      (JsPath \ "title").write[String] and
    //        (JsPath \ "baseUri").write[URI]
    //      ) (unlift(AppConfig.unapply))

}