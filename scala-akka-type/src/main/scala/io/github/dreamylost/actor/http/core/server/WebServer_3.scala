package io.github.dreamylost.actor.http.core.server

import akka.stream.scaladsl.Sink

/**
 * 绑定端口失败
 *
 * @author 梦境迷离
 * @since 2019-11-17
 * @version v1.0
 */
object WebServer_3 extends App {

  import akka.actor.ActorSystem
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.Http.ServerBinding
  import akka.stream.ActorMaterializer

  import scala.concurrent.Future

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future foreach in the end
  implicit val executionContext = system.dispatcher

  // let's say the OS won't allow us to bind to 80.
  val (host, port) = ("localhost", 80)
  val serverSource = Http().bind(host, port)

  val bindingFuture: Future[ServerBinding] = serverSource
    .to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)
    }).run() // Sink[Http.IncomingConnection, _]

  bindingFuture.failed.foreach { ex =>
    println(ex, "Failed to bind to {}:{}!", host, port)
  }
}
