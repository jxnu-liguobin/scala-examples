package UnitTesting.Controllers

import org.scalatestplus.play.PlaySpec
import play.api.mvc.{Result, Results}
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}

import scala.concurrent.Future

/**
 * 测试控制器
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 */
class ExampleControllerSpec extends PlaySpec with Results {

    "Example Page#index" should {
        "should be valid" in {
            val controller = new ExampleController(Helpers.stubControllerComponents())
            val result: Future[Result] = controller.index().apply(FakeRequest())
            val bodyText: String = contentAsString(result)
            bodyText mustBe "ok"
        }
    }
}