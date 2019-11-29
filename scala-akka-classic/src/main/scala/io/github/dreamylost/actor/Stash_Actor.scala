package io.github.dreamylost.actor

import akka.actor.{ Actor, ActorSystem, Props, Stash }
import io.github.dreamylost.actor.Become_Actor.Swapper
import io.github.dreamylost.actor.Become_Actor.Swapper

/**
 * Stash
 *
 * 利用“储藏”特征，演员可以暂时储藏无法或不应使用该演员的当前行为来处理的消息。
 * 更改参与者的消息处理程序后，即在调用context.become或context.unbecome之前，可以将所有已储藏的消息“取消储藏”，
 * 从而将其保留在参与者的邮箱中。这样，可以按照与原始接收相同的顺序来处理已储藏的消息
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object Stash_Actor extends App {

  object Swap

  class ActorWithProtocol extends Actor with Stash {
    def receive = {
      case "open" =>
        unstashAll()
        context.become({
          case "write" => // do writing...
          case "close" =>
            unstashAll()
            context.unbecome()
          case msg => stash()
        }, discardOld = false) // stack on top instead of replacing
      case msg => stash()
    }

  }

  val system = ActorSystem("SwapperSystem")
  val swap = system.actorOf(Props[Swapper], name = "swapper")
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
  swap ! Swap // logs Hi
  swap ! Swap // logs Ho
}
