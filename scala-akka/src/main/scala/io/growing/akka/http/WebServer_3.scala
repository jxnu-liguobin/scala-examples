package io.growing.akka.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.io.StdIn
import scala.util.Random

/**
 * Streaming,Example that streams random numbers as long as the client accepts them:
 *
 * 测试：
 * curl --limit-rate 50b 127.0.0.1:8080/random
 */
object WebServer_3 {

  def main(args: Array[String]) {

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    //流是可重用的，因此我们可以在这里定义它，并将其用于每个请求
    val numbers = Source.fromIterator(() => Iterator.continually(Random.nextInt()))

    //curl --limit-rate 50b 127.0.0.1:8080/random
    val route =
      path("random") {
        get {
          complete(
            HttpEntity(
              ContentTypes.`text/plain(UTF-8)`,
              //ByteString是字节的字符串形式
              numbers.map(n => ByteString(s"$n\n"))
            )
          )
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}