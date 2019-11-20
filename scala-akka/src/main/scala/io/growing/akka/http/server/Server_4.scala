package io.growing.akka.http.server

import akka.actor.{ Actor, ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink }

/**
 * 连接源失败
 *
 * @author 梦境迷离
 * @since 2019-11-17
 * @version v1.0
 */
object Server_4 extends App {


  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val (host, port) = ("localhost", 8080)
  val serverSource = Http().bind(host, port)

  val failureMonitor: ActorRef = system.actorOf(MyExampleMonitoringActor.props)

  val reactToTopLevelFailures = Flow[IncomingConnection]
    .watchTermination()((_, termination) => termination.failed.foreach {
      cause => failureMonitor ! cause
    })

  serverSource
    .via(reactToTopLevelFailures)
    .to(Sink.foreach { connection =>
      println("Accepted new connection from " + connection.remoteAddress)
    }).run()
}

class MyExampleMonitoringActor extends Actor {
  override def receive: Actor.Receive = {
    //绑定失败打印
    //receive: akka.stream.impl.io.ConnectionSourceStage$$anon$1$$anon$2: Bind failed because of java.net.BindException: Address already in use
    case e: Throwable => println("receive: " + e)
  }

}

object MyExampleMonitoringActor {
  val props = Props(classOf[MyExampleMonitoringActor])
}
