package io.github.dreamylost.actor.functional

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import io.github.dreamylost.actor.functional.ChatRoom._

/**
 * 使用聊天室的客户端
 * 函数式风格
 *
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object Gabbler {

  def apply(): Behavior[SessionEvent] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionGranted(handle) =>
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.log.info(s"""message has been posted by '$screenName': $message""")
          Behaviors.stopped
      }
    }
}