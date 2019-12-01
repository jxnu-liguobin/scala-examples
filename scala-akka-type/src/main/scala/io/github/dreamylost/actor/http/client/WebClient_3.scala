package io.github.dreamylost.actor.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.`Set-Cookie`
import akka.stream.ActorMaterializer

import scala.concurrent.{ ExecutionContextExecutor, Future }

/**
 * 从服务器响应中收集headers
 *
 * @author 梦境迷离
 * @since 2019-12-01
 * @version v1.0
 */
object WebClient_3 {

  object Client {
    def main(args: Array[String]): Unit = {
      implicit val system: ActorSystem = ActorSystem()
      implicit val materializer: ActorMaterializer = ActorMaterializer()
      implicit val executionContext: ExecutionContextExecutor = system.dispatcher

      val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))

      responseFuture.map {
        case response@HttpResponse(StatusCodes.OK, _, _, _) =>
          val setCookies = response.headers[`Set-Cookie`]
          println(s"Cookies set by a server: $setCookies")
          response.discardEntityBytes()
        case _ => sys.error("something wrong")
      }
    }
  }

}
