package controllers

import javax.inject.{Inject, Singleton}
import models.AppConfig
import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}

/**
 * @author 梦境迷离
 * @version 1.0, 2019-03-28
 */
@Singleton
class ConfigurationController @Inject()(config: Configuration, c: ControllerComponents) extends AbstractController(c) {


    /**
     * 获取配置文件的值
     *
     * @return
     */
    def getHello = Action { implicit request =>
        Ok(config.get[String]("hello"))
    }

    /**
     * 获取配置文件并转化为自定义类型
     *
     * @return
     */
    def getAppConfig = Action { implicit request =>
        Ok(Json.obj("appConfig" -> config.get[AppConfig]("app.config")))
    }
}
