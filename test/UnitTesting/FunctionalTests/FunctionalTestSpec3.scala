package UnitTesting.FunctionalTests

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.mvc.{DefaultActionBuilder, Results}
import play.api.test.Helpers._

/**
 * 测试服务端
 * 如果测试类中的所有测试都可以重用同一个服务器实例，则可以混合使用OneServerPerSuite(这也将为用例提供一个新的Application)：
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class FunctionalTestSpec3 extends PlaySpec with GuiceOneServerPerSuite {

    override def fakeApplication(): Application = {
        GuiceApplicationBuilder()
          .appRoutes(app => {
              case ("GET", "/") => app.injector.instanceOf(classOf[DefaultActionBuilder]) {
                  Results.Ok("ok")
              }
          }).build()
    }

    "test server logic" in {
        val wsClient = app.injector.instanceOf[WSClient]
        val myPublicAddress = s"localhost:$port"
        val testPaymentGatewayURL = s"http://$myPublicAddress"
        // The test payment gateway requires a callback to this server before it returns a result...
        val callbackURL = s"http://$myPublicAddress/callback"
        // await is from play.api.test.FutureAwaits
        val response = await(wsClient.url(testPaymentGatewayURL).addQueryStringParameters("callbackURL" -> callbackURL).get())

        response.status mustBe OK
    }
}
