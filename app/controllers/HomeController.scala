package controllers

import javax.inject._
import play.api.mvc._

/**
 * 主页
 */
@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

    /**
     * 返回html
     *
     * @return
     */
    def index = Action {
        //包名/格式/模板名
        Ok(views.html.index("Your new application is ready."))
    }

}
