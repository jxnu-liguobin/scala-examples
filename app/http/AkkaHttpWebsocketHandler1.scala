package http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, Uri }
import akka.http.scaladsl.model.ws.{ BinaryMessage, Message, TextMessage, UpgradeToWebSocket }
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink, Source }

import scala.io.StdIn

/**
 * akka-http websocket
 *
 * @author liguobin@growingio.com
 * @version 1.0,2020/1/20
 */
object AkkaHttpWebsocketHandler1 extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  //收到websocket消息后，在前面追加Hello，并响应客户端
  val greeterWebSocketService =
    Flow[Message].mapConcat {
      case tm: TextMessage => TextMessage(Source.single("Hello ") ++ tm.textStream) :: Nil
      case bm: BinaryMessage =>
        bm.dataStream.runWith(Sink.ignore)
        Nil
    }

  val requestHandler: HttpRequest => HttpResponse = {
    case req@HttpRequest(GET, Uri.Path("/greeter"), _, _, _) =>
      req.header[UpgradeToWebSocket] match {
        case Some(upgrade) => upgrade.handleMessages(greeterWebSocketService)
        case None => HttpResponse(400, entity = "Not a valid websocket request!")
      }
    case r: HttpRequest =>
      r.discardEntityBytes()
      HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture =
    Http().bindAndHandleSync(requestHandler, interface = "localhost", port = 8080)
  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()

  import system.dispatcher


  bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
}
