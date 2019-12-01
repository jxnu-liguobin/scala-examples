package io.github.dreamylost.actor.http.introduction

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.io.StdIn

/**
 * @see https://doc.akka.io/docs/akka-http/current/introduction.html
 *      测试：curl http://localhost:8080/hello
 */
object WebServer_1 {

  def main(args: Array[String]) {

    //默认的actor
    implicit val system = ActorSystem("my-system")
    //创建一个ActorMaterializer，它可以将流的模板实例化为运行时的流。在actor外部使用时需要隐式system对象，在actor内部时可使用ActorContext
    implicit val materializer = ActorMaterializer()
    //future flatMap/onComplete需要的执行上下文
    implicit val executionContext = system.dispatcher

    //定义一个简单的路由 /hello
    val route =
      path("hello") {
        get {
          //HTTP返回类型
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

    //绑定端口8080的HTTP服务器
    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
    StdIn.readLine() // 输入回车结束
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate()) // 关闭system
  }
}