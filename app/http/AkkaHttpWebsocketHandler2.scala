package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{ Message, TextMessage }
import akka.http.scaladsl.server.Directives
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Source }

import scala.io.StdIn

/**
 * akka-http websocket
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/1/20
 */
object AkkaHttpWebsocketHandler2 extends App {

  import Directives._

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val greeterWebSocketService =
    Flow[Message]
      .collect {
        case tm: TextMessage => TextMessage(Source.single("Hello ") ++ tm.textStream)
        // ignore binary messages
        // TODO #20096 in case a Streamed message comes in, we should runWith(Sink.ignore) its data
      }

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

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  import system.dispatcher

  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
