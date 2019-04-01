package UnitTesting.Controllers

import play.api.mvc.{BaseController, ControllerComponents}

/**
 * 测试使用的控制器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 * @param controllerComponents
 */
class ExampleController(val controllerComponents: ControllerComponents) extends BaseController {
    def index() = Action {
        Ok("ok")
    }
}