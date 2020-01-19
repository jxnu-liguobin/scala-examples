package websocket

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{ Flow, Sink, Source }
import javax.inject.{ Inject, Singleton }
import play.api.libs.streams.ActorFlow
import play.api.mvc._

/**
 * https://www.playframework.com/documentation/2.7.x/ScalaWebSockets
 *
 * @param cc
 * @param system
 * @param mat
 */
@Singleton
class WebSocketApplication @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer)
  extends AbstractController(cc) {

  /**
   * 描述Play在收到WebSocket连接时应创建的actor描述Play在收到WebSocket连接时应创建的actor
   *
   * @return
   */
  def socket = WebSocket.accept[String, String] { _ =>
    ActorFlow.actorRef { out =>
      MyWebSocketActor.props(out)
    }
  }

  /**
   * 直接使用Akka streams
   *
   * @return
   */
  def socket2 = WebSocket.accept[String, String] { _ =>
    // 将事件记录到控制台
    val in = Sink.foreach[String](println)
    // 发送一个“ Hello！”消息，然后关闭套接字，输入直接当做输出被打印到控制台
    val out = Source.single("Hello!").concat(Source.maybe)
    Flow.fromSinkAndSource(in, out)
  }

  /**
   * 直接将输入打印到控制台
   *
   * @return
   */
  def socket3 = WebSocket.accept[String, String] { _ =>
    // 将消息记录到stdout并将响应发送回客户端
    Flow[String].map { msg =>
      println(msg)
      "I received your message: " + msg
    }
  }

}