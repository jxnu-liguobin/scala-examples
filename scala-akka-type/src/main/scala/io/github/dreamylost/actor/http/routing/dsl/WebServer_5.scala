package io.github.dreamylost.actor.http.routing.dsl

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

/**
 * 在高级API中处理HTTP服务器故障
 *
 * 与akka的核心基础API相似，绑定失败和路由失败
 *
 * @see https://doc.akka.io/docs/akka-http/current/server-side/low-level-api.html#handling-http-server-failures-low-level
 * @author 梦境迷离
 * @since 2019-11-29
 * @version v1.0
 */
object WebServer_5 extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  //foreach最终需要
  implicit val executionContext = system.dispatcher

  val handler = get {
    complete("Hello world!")
  }

  //假设操作系统不允许我们绑定到80。
  val (host, port) = ("localhost", 80)
  val bindingFuture: Future[ServerBinding] =
    Http().bindAndHandle(handler, host, port)

  bindingFuture.failed.foreach { ex =>
    println(ex, "Failed to bind to {}:{}!", host, port) //演示用println
  }

}
