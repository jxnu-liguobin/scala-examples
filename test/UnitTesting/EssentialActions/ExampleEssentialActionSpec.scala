package UnitTesting.EssentialActions

import akka.stream.Materializer
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.libs.json.Json
import play.api.mvc.{DefaultActionBuilder, EssentialAction, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers._

class ExampleEssentialActionSpec extends PlaySpec with GuiceOneAppPerSuite {

    implicit lazy val materializer: Materializer = app.materializer
    implicit lazy val Action = app.injector.instanceOf(classOf[DefaultActionBuilder])

    "An essential action" should {
        "can parse a JSON body" in {
            //模拟action解析json
            val action: EssentialAction = Action { request =>
                val value = (request.body.asJson.get \ "field").as[String]
                Results.Ok(value)
            }

            /**
             * Object with helper methods for building [[FakeRequest]] values. This object uses a
             * [[play.api.mvc.request.DefaultRequestFactory]] with default configuration to build
             * the requests.
             */
            val request = FakeRequest(POST, "/").withJsonBody(Json.parse("""{ "field":"value" }"""))

            val result = call(action, request)

            status(result) mustEqual OK
            contentAsString(result) mustEqual "value"
        }
    }
}