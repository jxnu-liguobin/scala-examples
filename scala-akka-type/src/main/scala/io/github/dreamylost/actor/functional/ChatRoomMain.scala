package io.github.dreamylost.actor.functional

import akka.actor.typed.{ ActorSystem, Behavior, Terminated }
import akka.actor.typed.scaladsl.Behaviors
import akka.NotUsed

/**
 * 启动
 *
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object ChatRoomMain {

  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val chatRoom = context.spawn(ChatRoom(), "chatroom")
      val gabblerRef = context.spawn(Gabbler(), "gabbler")
      context.watch(gabblerRef)
      chatRoom ! ChatRoom.GetSession("ol’ Gabbler", gabblerRef)

      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }

  def main(args: Array[String]): Unit = {
    ActorSystem(ChatRoomMain(), "ChatRoomDemo")
  }

}