package UnitTesting.FunctionalTests

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.{Call, DefaultActionBuilder}

/**
 * 包含WordSpec, MustMatchers, OptionValues，和WsScalaTestClient特殊
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class ExampleSpec extends PlaySpec with GuiceOneServerPerSuite with ScalaFutures with IntegrationPatience {

    override def fakeApplication(): Application = {

        import play.api.http.MimeTypes._
        import play.api.mvc.Results._

        GuiceApplicationBuilder()
          .appRoutes(app => {
              case ("GET", "/testing") => app.injector.instanceOf(classOf[DefaultActionBuilder]) {
                  Ok(
                      """
                        |<html>
                        | <head>
                        |   <title>Test Page</title>
                        |   <body>
                        |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
                        |   </body>
                        | </head>
                        |</html>""".stripMargin
                  ).as(HTML)
              }
          }).build()
    }


    /**
     * ws测试
     */
    "WsScalaTestClient's" must {

        "wsUrl works correctly" in {
            //隐式对象WSClient
            implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
            val futureResult = wsUrl("/testing").get
            val body = futureResult.futureValue.body
            val expectedBody =
                """
                  |<html>
                  | <head>
                  |   <title>Test Page</title>
                  |   <body>
                  |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
                  |   </body>
                  | </head>
                  |</html>""".stripMargin
            assert(body == expectedBody)
        }

        "wsCall works correctly" in {
            implicit val ws: WSClient = app.injector.instanceOf(classOf[WSClient])
            val futureResult = wsCall(Call("get", "/testing")).get
            val body = futureResult.futureValue.body
            val expectedBody =
                """
                  |<html>
                  | <head>
                  |   <title>Test Page</title>
                  |   <body>
                  |     <input type='button' name='b' value='Click Me' onclick='document.title="scalatest"' />
                  |   </body>
                  | </head>
                  |</html>""".stripMargin
            assert(body == expectedBody)
        }
    }
}
