package io.growing.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{ Failure, Success }

/**
 * HTTP client API
 *
 * The client APIs provide methods for calling a HTTP server using the same HttpRequest and HttpResponse abstractions that Akka HTTP server uses
 * but adds the concept of connection pools to allow multiple requests to the same server to be handled more performantly by re-using TCP connections to the server.
 */
object WebClient_1 {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    //通过重用TCP连接来更有效地处理对同一服务器的多个请求
    //可以配置连接池、日志
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = "http://akka.io"))
    responseFuture
      .onComplete {
        case Success(res) => println(res)
        case Failure(_) => sys.error("something wrong")
      }
  }
}