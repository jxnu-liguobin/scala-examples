package io.github.dreamylost.actor.http.introduction

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
 * Low-level HTTP server APIs
 */
object WebServer_5 {

  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    //参数为HttpRequest，返回为HttpResponse的函数
    val requestHandler: HttpRequest => HttpResponse = {
      //默认：无请求头、/、GET、HTTP/1.1
      case HttpRequest(GET, Uri.Path("/"), _, _, _) =>
        HttpResponse(entity = HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
      case HttpRequest(GET, Uri.Path("/ping"), _, _, _) => HttpResponse(entity = "PONG!")
      case HttpRequest(GET, Uri.Path("/crash"), _, _, _) => sys.error("BOOM!")
      case r: HttpRequest =>
        r.discardEntityBytes() //耗尽传入的HTTP实体流
        HttpResponse(404, entity = "Unknown resource!")
    }

    val bindingFuture = Http().bindAndHandleSync(requestHandler, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())

  }
}

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ ContentTypes, HttpEntity }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
 * routing dsl的
 * 这是使用可组合的高级API重写上述示例
 */
object WebServer_5_1 {
  def main(args: Array[String]) {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val executionContext = system.dispatcher

    val route =
      get {
        concat(
          pathSingleSlash {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<html><body>Hello world!</body></html>"))
          },
          path("ping") {
            complete("PONG!")
          },
          path("crash") {
            sys.error("BOOM!")
          }
        )
      }

    // `route` will be implicitly converted to `Flow` using `RouteResult.route2HandlerFlow`
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)
    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}