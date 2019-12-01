package io.github.dreamylost.actor.http.core.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink

import scala.concurrent.Future

/**
 * 请求 - 响应 周期
 *
 * @author 梦境迷离
 * @since 2019-11-17
 * @version v1.0
 */
object WebServer_2 extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val serverSource = Http().bind(interface = "localhost", port = 8080)

  val requestHandler: HttpRequest => HttpResponse = {
    case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
      HttpResponse(entity = HttpEntity(
        ContentTypes.`text/html(UTF-8)`,
        "<html><body>Hello world!</body></html>"))

    case HttpRequest(GET, Uri.Path("/ping"), _, _, _) =>
      HttpResponse(entity = "PONG!")

    case HttpRequest(GET, Uri.Path("/crash"), _, _, _) =>
      sys.error("BOOM!")

    case r: HttpRequest =>
      r.discardEntityBytes() // 耗尽传入的HTTP实体流，这很重要
      HttpResponse(404, entity = "Unknown resource!")
  }

  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)

      connection handleWithSyncHandler requestHandler
      // 相当于connection handleWith { Flow[HttpRequest] map requestHandler }
    }).run()
}
