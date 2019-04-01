package UnitTesting.FunctionalTests

import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
//must
import play.api.test.Helpers._

/**
 * 简单测试、测试模板
 * 创建Application测试实例
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-1
 */
class FunctionalTestSpec1 extends PlaySpec with GuiceOneAppPerSuite {

    //如果需要自定义Application，覆盖fakeApplication()，GuiceOneAppPerSuite已经混入GuiceFakeApplicationFactory
    override def fakeApplication(): Application = {
        GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
    }

    "The GuiceOneAppPerSuite trait" must {
        "provide an Application" in {
            app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
        }
    }


    /**
     * 测试模板
     */
    "render index template" in new App {
        val html = views.html.index("index")

        contentAsString(html) must include("Welcome to Play")
    }

}