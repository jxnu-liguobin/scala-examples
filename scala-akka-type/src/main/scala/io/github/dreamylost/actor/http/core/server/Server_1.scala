package io.github.dreamylost.actor.http.core.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._

import scala.concurrent.Future

/**
 * 在最基本的级别上，通过调用akka.http.scaladsl.Http的bind方法来绑定Akka HTTP服务器
 *
 * @author 梦境迷离
 * @since 2019-11-17
 * @version v1.0
 */
object Server_1 extends App {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val serverSource: Source[Http.IncomingConnection, Future[Http.ServerBinding]] =
    Http().bind(interface = "localhost", port = 8080)
  val bindingFuture: Future[Http.ServerBinding] =
    serverSource.to(Sink.foreach { connection => // foreach materializes the source
      println("Accepted new connection from " + connection.remoteAddress)
      //处理连接
    }).run()
}
