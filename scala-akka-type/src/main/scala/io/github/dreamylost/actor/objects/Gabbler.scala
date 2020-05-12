package io.github.dreamylost.actor.objects

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.github.dreamylost.actor.objects.ChatRoom._

/**
 * 使用聊天室的客户端
 * 面向对象风格
 *
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object Gabbler {


  def apply(): Behavior[SessionEvent] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionDenied(reason) =>
          context.log.info("cannot start chat room session: {}", reason)
          Behaviors.stopped
        case SessionGranted(handle) =>
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.log.info(s"""message has been posted by '$screenName': $message""")
          Behaviors.stopped
      }
    }
}