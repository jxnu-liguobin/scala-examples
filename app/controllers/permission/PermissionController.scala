package controllers.permission

import action.ComponentsController
import javax.inject._
import play.api.libs.json.JsValue
import play.api.mvc._

import scala.concurrent.{ ExecutionContext, Future }

/**
 * permission
 */
@Singleton
class PermissionController @Inject()(implicit cc: ControllerComponents, ce: ExecutionContext) extends ComponentsController()(ce, cc) {

  def index: Action[JsValue] = (AuthAction andThen PermissionCheckAction("root")).async(parse.tolerantJson) {
    implicit request =>
      Future.successful(Ok(request.user))
  }
}
