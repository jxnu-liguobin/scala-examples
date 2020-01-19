package websocket

import akka.actor._

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketActor(out))
}

class MyWebSocketActor(out: ActorRef) extends Actor {
  def receive = {
    case msg: String =>
      //获取用来返回信息的actor，将消息回复给客户端
      out ! ("I received your message: " + msg)
  }
}