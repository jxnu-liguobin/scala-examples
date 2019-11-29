package io.github.dreamylost.actor

import akka.actor.Actor
import akka.event.Logging

/**
 * Become/Unbecome
 *
 * Akka支持在运行时热交换Actor的消息遍历（例如其实现）：context.become从Actor内部调用该方法。become需要实现新的消息处理程序。热交换的代码保存在堆栈中，可以将其压入并弹出。PartialFunction[Any, Unit]
 *
 * @author liguobin@growingio.com
 * @version 1.0,2019/10/14
 */
object Become_Actor {

  //使用become方法热交换Actor行为
  class HotSwapActor extends Actor {

    //Stores the context for this actor, including self, and sender.
    //Only valid within the Actor itself, so do not close over it and publish it to other threads!

    import context._

    def angry: Receive = {
      case "foo" => sender() ! "I am already angry?"
      case "bar" => become(happy)
    }

    def happy: Receive = {
      case "bar" => sender() ! "I am already happy :-)"
      case "foo" => become(angry)
    }

    def receive = {
      case "foo" => become(angry)
      case "bar" => become(happy)
    }
  }


  //使用become的另一种方法不是替换而是添加到行为堆栈的顶部
  case object Swap

  class Swapper extends Actor {

    import context._

    val log = Logging(system, this)

    def receive = {
      case Swap =>
        log.info("Hi")
        become({
          case Swap =>
            log.info("Ho")
            unbecome() //reset最新的“become”
        }, discardOld = false) // push on top instead of replace
    }
  }

}
