package io.github.dreamylost.actor.functional

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors

/**
 * 聊天室
 *
 * 函数式风格
 *
 * @see https://doc.akka.io/docs/akka/current/typed/reliable-delivery.html#point-to-point-example
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object ChatRoom {

  sealed trait RoomCommand

  final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

  sealed trait SessionEvent

  final case class SessionGranted(handle: ActorRef[PostMessage]) extends SessionEvent

  final case class SessionDenied(reason: String) extends SessionEvent

  final case class MessagePosted(screenName: String, message: String) extends SessionEvent

  trait SessionCommand

  final case class PostMessage(message: String) extends SessionCommand

  private final case class NotifyClient(message: MessagePosted) extends SessionCommand

  private final case class PublishSessionMessage(screenName: String, message: String) extends RoomCommand

  def apply(): Behavior[RoomCommand] = chatRoom(List.empty)

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
  //Behavior实例恰好在actor运行之前创建
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, client) =>
          //创建一个子actor以与客户进一步互动
          val ses = context.spawn(session(context.self, screenName, client), name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name))
          client ! SessionGranted(ses)
          chatRoom(ses :: sessions)
        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }
    }

  private def session(room: ActorRef[PublishSessionMessage], screenName: String, client: ActorRef[SessionEvent]): Behavior[SessionCommand] =
    Behaviors.receiveMessage {
      case PostMessage(message) =>
        //来自客户端，通过聊天室发布给其他人
        room ! PublishSessionMessage(screenName, message)
        Behaviors.same
      case NotifyClient(message) =>
        //从聊天室发布
        client ! message
        Behaviors.same
    }
}