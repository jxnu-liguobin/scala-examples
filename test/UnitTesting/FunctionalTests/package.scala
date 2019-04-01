package UnitTesting

package object FunctionalTests {


    //Writing functional tests with ScalaTest
    /**
     * Play provides a number of classes and convenience methods that assist with functional testing.
     * Most of these can be found either in the play.api.test package or in the Helpers object.
     * The ScalaTest + Play integration library builds on this testing support for ScalaTest.
     */
    /**
     * You can access all of Play’s built-in test support and ScalaTest + Play with the following imports:
     *
     * import org.scalatest._
     * import org.scalatestplus.play._
     * import play.api.http.MimeTypes
     * import play.api.test._
     */

    /**
     * 如果希望在连续测试之间维护应用程序状态，则需要使用GuiceOneAppPerSuite
     * 如果每个测试都需要一个干净的标记，则可以使用OneAppPerTest或使用GuiceOneAppPerSuite，但在测试结束清除状态
     */

    /**
     * 以Suite结：尾单例
     * 以Test结尾：全新实例
     */
}
