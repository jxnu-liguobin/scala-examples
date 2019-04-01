package UnitTesting.FunctionalTests

import org.scalatest.TestData
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder

/**
 * 简单测试
 * 创建Application测试实例
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class FunctionalTestSpec2 extends PlaySpec with GuiceOneAppPerTest {
    //如果您需要每个测试来获得它自己的Application，而不是共享相同的一个
    override def newAppForTest(td: TestData): Application = {
        GuiceApplicationBuilder().configure(Map("ehcacheplugin" -> "disabled")).build()
    }

    "The OneAppPerTest trait" must {
        "provide a new Application for each test" in {
            app.configuration.getOptional[String]("ehcacheplugin") mustBe Some("disabled")
        }
    }
}
