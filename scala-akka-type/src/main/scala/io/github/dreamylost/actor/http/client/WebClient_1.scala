package io.github.dreamylost.actor.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{ Failure, Success }

/**
 * HTTP client API
 *
 * @see https://doc.akka.io/docs/akka-http/current/client-side/index.html
 */
object WebClient_1 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    //通过重用TCP连接来更有效地处理对同一服务器的多个请求
    //可以配置连接池、日志
    //这是request级别的客户端API，还有host和connection级
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))
    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => sys.error("something wrong")
      }
  }
}