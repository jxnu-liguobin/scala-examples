package io.github.dreamylost.actor.objects

import akka.actor.typed.ActorSystem
import io.github.dreamylost.actor.functional.ChatRoomMain

/**
 * 启动
 *
 * @author 梦境迷离
 * @since 2020-04-13
 * @version v1.0
 */
object ChatRoomMain2 {

  def main(args: Array[String]): Unit = {
    ActorSystem(ChatRoomMain(), "ChatRoomDemo")
  }

}