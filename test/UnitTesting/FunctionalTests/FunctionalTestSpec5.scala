package UnitTesting.FunctionalTests

import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.DefaultActionBuilder

/**
 * 用Web浏览器进行测试
 *
 * @author 梦境迷离
 * @version 1.0, 2019-04-01
 */
class FunctionalTestSpec5 extends PlaySpec with GuiceOneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

    //若要使用同一个浏览器实例运行测试类中的所有测试，请混合OneBrowserPerSuite
    //还需要混合一个BrowserFactory提供Selenium web驱动程序的特性:
    //ChromeFactory, FirefoxFactory, HtmlUnitFactory, InternetExplorerFactory, SafariFactory.
    //除了混合在BrowserFactory，还需要在ServerProvider提供一个TestServer*其中之一:
    // OneServerPerSuite, OneServerPerTest，或ConfiguredServer.

    /**
     * 如果希望在多个web浏览器中运行测试，以确保应用程序在所支持的所有浏览器中正确工作，则可以使用特性。AllBrowsersPerSuite或AllBrowsersPerTest。
     * 这两个特征都声明browsers类型域IndexedSeq[BrowserInfo]摘要sharedTests方法，该方法采用BrowserInfo
     */
    /**
     * browsers字段表示要在哪些浏览器中运行测试。默认是Chrome，Firefox，InternetExplorer，HtmlUnit，还有Safari
     */

    //若每个测试都需要新的浏览器实例，则使用OneBrowserPerTest
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
                        |</html>
                      """.stripMargin
                  ).as(HTML)
              }
          }).build()
    }

    "The OneBrowserPerTest trait" must {
        "provide a web driver" in {
            go to s"http://localhost:$port/testing"
            pageTitle mustBe "Test Page"
            click on find(name("b")).value
            eventually {
                pageTitle mustBe "scalatest"
            }
        }
    }
}
