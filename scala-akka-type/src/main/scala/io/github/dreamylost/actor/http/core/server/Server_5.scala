package io.github.dreamylost.actor.http.core.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow

/**
 * 连接失败
 *
 * 正确建立连接后，然后突然终止
 *
 * @author 梦境迷离
 * @since 2019-11-17
 * @version v1.0
 */
object Server_5 extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val (host, port) = ("localhost", 8080)
  val serverSource = Http().bind(host, port)

  val reactToConnectionFailure = Flow[HttpRequest]
    .recover[HttpRequest] {
      case ex =>
        // handle the failure somehow
        throw ex
    }

  val httpEcho = Flow[HttpRequest]
    .via(reactToConnectionFailure)
    .map { request =>
      // simple streaming (!) "echo" response:
      HttpResponse(entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, request.entity.dataBytes))
    }

  serverSource
    .runForeach { con =>
      con.handleWith(httpEcho)
    }
}
