package UnitTesting.FunctionalTests

import org.scalatest.TestData
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.{DefaultActionBuilder, Results}
import play.api.test.Helpers._

/**
 * 测试服务端
 * 如果测试类中的所有测试都需要单独的服务器实例，请使用OneServerPerTest(这也将为用例提供一个新的Application)
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class FunctionalTestSpec4 extends PlaySpec with GuiceOneServerPerTest {
    // Override newAppForTest or mixin GuiceFakeApplicationFactory and use fakeApplication() for an Application
    override def newAppForTest(testData: TestData): Application = {
        GuiceApplicationBuilder()
          .appRoutes(app => {
              case ("GET", "/") => app.injector.instanceOf(classOf[DefaultActionBuilder]) {
                  Results.Ok("ok")
              }
          }).build()
    }

    "The OneServerPerTest trait" must {
        "test server logic" in {
            val wsClient = app.injector.instanceOf[WSClient]
            //默认19001
            //可以更改此值或重写port或通过设置系统属性testserver.port
            //通过重写app，可以自定义application
            val myPublicAddress = s"localhost:$port"
            val testPaymentGatewayURL = s"http://$myPublicAddress"
            // The test payment gateway requires a callback to this server before it returns a result...
            val callbackURL = s"http://$myPublicAddress/callback"
            // await is from play.api.test.FutureAwaits
            val response = await(wsClient.url(testPaymentGatewayURL).addQueryStringParameters("callbackURL" -> callbackURL).get())

            response.status mustBe OK
        }
    }
}
