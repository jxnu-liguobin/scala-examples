package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{ BinaryMessage, Message, TextMessage }
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink, Source }

import scala.io.StdIn

/**
 * akka-http websocket
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/1/20
 */
object AkkaHttpWebsocketHandler2 extends App {

  import java.util.concurrent.atomic.AtomicInteger

  import Directives._
  import akka.http.scaladsl.settings.ServerSettings
  import akka.util.ByteString

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val greeterWebSocketService: Flow[Message, Message, Any] =
    Flow[Message].mapConcat {
      case tm: TextMessage => TextMessage(Source.single("Hello ") ++ tm.textStream) :: Nil
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }

  //自定义保持活动数据有效负载
  val defaultSettings = ServerSettings(system)
  val pingCounter = new AtomicInteger()
  val customWebsocketSettings = defaultSettings.websocketSettings.
    withPeriodicKeepAliveData(() => ByteString(s"debug-${pingCounter.incrementAndGet()}"))
  val customServerSettings = defaultSettings.withWebsocketSettings(customWebsocketSettings)

  //根据路径进行路由
  //  val route =
  //    path("greeter") {
  //      get {
  //        handleWebSocketMessages(greeterWebSocketService)
  //      }
  //    }

  val route = path("websocket") {
    get {
      parameters("uid".as[String]) { uid =>
        println("当前有用户连接了： " + uid)
        handleWebSocketMessages(greeterWebSocketService)
      }
    }
  }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080, settings = customServerSettings)

  println(
    """
      | __      __      ___.     _________              __           __      _________
      |/  \    /  \ ____\_ |__  /   _____/ ____   ____ |  | __ _____/  |_   /   _____/ ______________  __ ___________
      |\   \/\/   // __ \| __ \ \_____  \ /  _ \_/ ___\|  |/ // __ \   __\  \_____  \_/ __ \_  __ \  \/ // __ \_  __ \
      | \        /\  ___/| \_\ \/        (  <_> )  \___|    <\  ___/|  |    /        \  ___/|  | \/\   /\  ___/|  | \/
      |  \__/\  /  \___  >___  /_______  /\____/ \___  >__|_ \\___  >__|   /_______  /\___  >__|    \_/  \___  >__|
      |       \/       \/    \/        \/            \/     \/    \/               \/     \/                 \/
      |""".stripMargin)
  StdIn.readLine()

  import system.dispatcher

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}


