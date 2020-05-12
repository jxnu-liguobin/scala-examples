package io.github.dreamylost.actor.objects

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.{ AbstractBehavior, ActorContext, Behaviors }

/**
 * 聊天室
 *
 * 面向对象风格
 *
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

  def apply(): Behavior[RoomCommand] =
    Behaviors.setup(context => new ChatRoomBehavior(context))

  class ChatRoomBehavior(context: ActorContext[RoomCommand]) extends AbstractBehavior[RoomCommand](context) {
    private var sessions: List[ActorRef[SessionCommand]] = List.empty

    override def onMessage(message: RoomCommand): Behavior[RoomCommand] = {
      message match {
        case GetSession(screenName, client) =>
          val ses = context.spawn(
            SessionBehavior(context.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name))
          client ! SessionGranted(ses)
          sessions = ses :: sessions
          this
        case PublishSessionMessage(screenName, message) =>
          val notification = NotifyClient(MessagePosted(screenName, message))
          sessions.foreach(_ ! notification)
          this
      }
    }
  }

  object SessionBehavior {
    def apply(
      room: ActorRef[PublishSessionMessage],
      screenName: String,
      client: ActorRef[SessionEvent]): Behavior[SessionCommand] =
      Behaviors.setup(ctx => new SessionBehavior(ctx, room, screenName, client))
  }

  private class SessionBehavior(
    context: ActorContext[SessionCommand],
    room: ActorRef[PublishSessionMessage],
    screenName: String,
    client: ActorRef[SessionEvent])
    extends AbstractBehavior[SessionCommand](context) {

    override def onMessage(msg: SessionCommand): Behavior[SessionCommand] = {
      msg match {
        case PostMessage(message) =>
          room ! PublishSessionMessage(screenName, message)
          Behaviors.same
        case NotifyClient(message) =>
          client ! message
          Behaviors.same
      }
    }
  }

}